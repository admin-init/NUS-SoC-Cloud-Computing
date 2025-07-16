package main

import (
	"context"
	"fmt"
	"log"
	"time"

	appsv1 "k8s.io/api/apps/v1"
	autoscalingv2 "k8s.io/api/autoscaling/v2"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/resource"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/util/intstr"
	"k8s.io/client-go/kubernetes"
	"k8s.io/client-go/tools/clientcmd"
	"k8s.io/client-go/util/homedir"
    "k8s.io/client-go/rest"
	"path/filepath"
)


type K8sManager struct {
	clientset *kubernetes.Clientset
	namespace string
}

func NewK8sManagerInCluster(namespace string) (*K8sManager, error) {
    config, err := rest.InClusterConfig()
    if err != nil {
        return nil, fmt.Errorf("failed to load in-cluster config: %v", err)
    }
    
    clientset, err := kubernetes.NewForConfig(config)
    if err != nil {
        return nil, fmt.Errorf("failed to create clientset: %v", err)
    }
    
    return &K8sManager{
        clientset: clientset,
        namespace: namespace,
    }, nil
}

func NewK8sManager(namespace string) (*K8sManager, error) {
	// Load kubeconfig
	kubeconfig := filepath.Join(homedir.HomeDir(), ".kube", "config")
	config, err := clientcmd.BuildConfigFromFlags("", kubeconfig)
	if err != nil {
		return nil, fmt.Errorf("failed to load kubeconfig: %v", err)
	}

	clientset, err := kubernetes.NewForConfig(config)
	if err != nil {
		return nil, fmt.Errorf("failed to create clientset: %v", err)
	}

	return &K8sManager{
		clientset: clientset,
		namespace: namespace,
	}, nil
}

// 1. Deploy a service with resource limits
func (k *K8sManager) DeployService(serviceName, image string, port int32) error {
	deployment := &appsv1.Deployment{
		ObjectMeta: metav1.ObjectMeta{
			Name:      serviceName,
			Namespace: k.namespace,
			Labels: map[string]string{
				"app": serviceName,
			},
		},
		Spec: appsv1.DeploymentSpec{
			Replicas: int32Ptr(2), // Start with 2 replicas
			Selector: &metav1.LabelSelector{
				MatchLabels: map[string]string{
					"app": serviceName,
				},
			},
			Template: corev1.PodTemplateSpec{
				ObjectMeta: metav1.ObjectMeta{
					Labels: map[string]string{
						"app": serviceName,
					},
				},
				Spec: corev1.PodSpec{
					Containers: []corev1.Container{
						{
							Name:  serviceName,
							Image: image,
							Ports: []corev1.ContainerPort{
								{
									ContainerPort: port,
								},
							},
							Resources: corev1.ResourceRequirements{
								Requests: corev1.ResourceList{
									corev1.ResourceCPU:    resource.MustParse("100m"),
									corev1.ResourceMemory: resource.MustParse("128Mi"),
								},
								Limits: corev1.ResourceList{
									corev1.ResourceCPU:    resource.MustParse("500m"),
									corev1.ResourceMemory: resource.MustParse("512Mi"),
								},
							},
						},
					},
				},
			},
		},
	}

	// Create deployment
	_, err := k.clientset.AppsV1().Deployments(k.namespace).Create(
		context.TODO(), deployment, metav1.CreateOptions{})
	if err != nil {
		return fmt.Errorf("failed to create deployment: %v", err)
	}

	// Create service
	service := &corev1.Service{
		ObjectMeta: metav1.ObjectMeta{
			Name:      serviceName,
			Namespace: k.namespace,
			Labels: map[string]string{
				"app": serviceName,
			},
		},
		Spec: corev1.ServiceSpec{
			Selector: map[string]string{
				"app": serviceName,
			},
			Ports: []corev1.ServicePort{
				{
					Port:       port,
					TargetPort: intstr.FromInt(int(port)),
				},
			},
			Type: corev1.ServiceTypeClusterIP,
		},
	}

	_, err = k.clientset.CoreV1().Services(k.namespace).Create(
		context.TODO(), service, metav1.CreateOptions{})
	if err != nil {
		return fmt.Errorf("failed to create service: %v", err)
	}

	fmt.Printf("Successfully deployed %s\n", serviceName)
	return nil
}

// 2. Create HPA for autoscaling
func (k *K8sManager) CreateHPA(serviceName string, minReplicas, maxReplicas int32, cpuThreshold, memoryThreshold int32) error {
	hpa := &autoscalingv2.HorizontalPodAutoscaler{
		ObjectMeta: metav1.ObjectMeta{
			Name:      serviceName + "-hpa",
			Namespace: k.namespace,
		},
		Spec: autoscalingv2.HorizontalPodAutoscalerSpec{
			ScaleTargetRef: autoscalingv2.CrossVersionObjectReference{
				APIVersion: "apps/v1",
				Kind:       "Deployment",
				Name:       serviceName,
			},
			MinReplicas: &minReplicas,
			MaxReplicas: maxReplicas,
			Metrics: []autoscalingv2.MetricSpec{
				{
					Type: autoscalingv2.ResourceMetricSourceType,
					Resource: &autoscalingv2.ResourceMetricSource{
						Name: corev1.ResourceCPU,
						Target: autoscalingv2.MetricTarget{
							Type:               autoscalingv2.UtilizationMetricType,
							AverageUtilization: &cpuThreshold,
						},
					},
				},
				{
					Type: autoscalingv2.ResourceMetricSourceType,
					Resource: &autoscalingv2.ResourceMetricSource{
						Name: corev1.ResourceMemory,
						Target: autoscalingv2.MetricTarget{
							Type:               autoscalingv2.UtilizationMetricType,
							AverageUtilization: &memoryThreshold,
						},
					},
				},
			},
			Behavior: &autoscalingv2.HorizontalPodAutoscalerBehavior{
				ScaleUp: &autoscalingv2.HPAScalingRules{
					StabilizationWindowSeconds: int32Ptr(60),
					Policies: []autoscalingv2.HPAScalingPolicy{
						{
							Type:          autoscalingv2.PercentScalingPolicy,
							Value:         100,
							PeriodSeconds: 60,
						},
					},
				},
				ScaleDown: &autoscalingv2.HPAScalingRules{
					StabilizationWindowSeconds: int32Ptr(300),
					Policies: []autoscalingv2.HPAScalingPolicy{
						{
							Type:          autoscalingv2.PercentScalingPolicy,
							Value:         10,
							PeriodSeconds: 60,
						},
					},
				},
			},
		},
	}

	_, err := k.clientset.AutoscalingV2().HorizontalPodAutoscalers(k.namespace).Create(
		context.TODO(), hpa, metav1.CreateOptions{})
	if err != nil {
		return fmt.Errorf("failed to create HPA: %v", err)
	}

	fmt.Printf("Successfully created HPA for %s\n", serviceName)
	return nil
}

// 3. Monitor and supervise services
func (k *K8sManager) MonitorService(serviceName string) error {
	fmt.Printf("Monitoring service: %s\n", serviceName)

	for {
		// Check deployment status
		deployment, err := k.clientset.AppsV1().Deployments(k.namespace).Get(
			context.TODO(), serviceName, metav1.GetOptions{})
		if err != nil {
			log.Printf("Error getting deployment: %v", err)
			time.Sleep(10 * time.Second)
			continue
		}

		// Check HPA status
		hpa, err := k.clientset.AutoscalingV2().HorizontalPodAutoscalers(k.namespace).Get(
			context.TODO(), serviceName+"-hpa", metav1.GetOptions{})
		if err != nil {
			log.Printf("Error getting HPA: %v", err)
		} else {
			fmt.Printf("HPA Status - Current: %d, Desired: %d, Min: %d, Max: %d\n",
				hpa.Status.CurrentReplicas,
				hpa.Status.DesiredReplicas,
				*hpa.Spec.MinReplicas,
				hpa.Spec.MaxReplicas)
		}

		// Check pod status
		pods, err := k.clientset.CoreV1().Pods(k.namespace).List(
			context.TODO(), metav1.ListOptions{
				LabelSelector: fmt.Sprintf("app=%s", serviceName),
			})
		if err != nil {
			log.Printf("Error getting pods: %v", err)
		} else {
			fmt.Printf("Deployment Status - Ready: %d/%d, Available: %d\n",
				deployment.Status.ReadyReplicas,
				deployment.Status.Replicas,
				deployment.Status.AvailableReplicas)

			// Check individual pod status
			for _, pod := range pods.Items {
				fmt.Printf("Pod %s: %s\n", pod.Name, pod.Status.Phase)
			}
		}

		fmt.Println("---")
		time.Sleep(30 * time.Second)
	}
}

// 4. Manual scaling (for testing)
func (k *K8sManager) ScaleService(serviceName string, replicas int32) error {
	deployment, err := k.clientset.AppsV1().Deployments(k.namespace).Get(
		context.TODO(), serviceName, metav1.GetOptions{})
	if err != nil {
		return fmt.Errorf("failed to get deployment: %v", err)
	}

	deployment.Spec.Replicas = &replicas

	_, err = k.clientset.AppsV1().Deployments(k.namespace).Update(
		context.TODO(), deployment, metav1.UpdateOptions{})
	if err != nil {
		return fmt.Errorf("failed to scale deployment: %v", err)
	}

	fmt.Printf("Successfully scaled %s to %d replicas\n", serviceName, replicas)
	return nil
}

// 5. Get scaling metrics
func (k *K8sManager) GetScalingMetrics(serviceName string) error {
	hpa, err := k.clientset.AutoscalingV2().HorizontalPodAutoscalers(k.namespace).Get(
		context.TODO(), serviceName+"-hpa", metav1.GetOptions{})
	if err != nil {
		return fmt.Errorf("failed to get HPA: %v", err)
	}

	fmt.Printf("=== HPA Metrics for %s ===\n", serviceName)
	fmt.Printf("Current Replicas: %d\n", hpa.Status.CurrentReplicas)
	fmt.Printf("Desired Replicas: %d\n", hpa.Status.DesiredReplicas)
	fmt.Printf("Min Replicas: %d\n", *hpa.Spec.MinReplicas)
	fmt.Printf("Max Replicas: %d\n", hpa.Spec.MaxReplicas)

	if hpa.Status.CurrentMetrics != nil {
		for _, metric := range hpa.Status.CurrentMetrics {
			if metric.Resource != nil {
				fmt.Printf("Resource %s: Current=%s, Target=%s\n",
					metric.Resource.Name,
					metric.Resource.Current.AverageUtilization,
					metric.Resource.Target.AverageUtilization)
			}
		}
	}

	return nil
}

// 6. Cleanup resources
func (k *K8sManager) CleanupService(serviceName string) error {
	// Delete HPA
	err := k.clientset.AutoscalingV2().HorizontalPodAutoscalers(k.namespace).Delete(
		context.TODO(), serviceName+"-hpa", metav1.DeleteOptions{})
	if err != nil {
		log.Printf("Error deleting HPA: %v", err)
	}

	// Delete service
	err = k.clientset.CoreV1().Services(k.namespace).Delete(
		context.TODO(), serviceName, metav1.DeleteOptions{})
	if err != nil {
		log.Printf("Error deleting service: %v", err)
	}

	// Delete deployment
	err = k.clientset.AppsV1().Deployments(k.namespace).Delete(
		context.TODO(), serviceName, metav1.DeleteOptions{})
	if err != nil {
		log.Printf("Error deleting deployment: %v", err)
	}

	fmt.Printf("Cleaned up resources for %s\n", serviceName)
	return nil
}

// Example usage
func main() {
	manager, err := NewK8sManagerInCluster("default")
	if err != nil {
		log.Fatal(err)
	}

	// Deploy your ticket booking services
	services := []struct {
		name  string
		image string
		port  int32
	}{
		{"operations-management-service", "operations-service:latest", 8080},
		{"order-management-service", "order-service:latest", 8081},
	}

	for _, svc := range services {
		// 1. Deploy service
		if err := manager.DeployService(svc.name, svc.image, svc.port); err != nil {
			log.Printf("Error deploying %s: %v", svc.name, err)
			continue
		}

		// 2. Create HPA (min: 2, max: 10, CPU: 70%, Memory: 80%)
		if err := manager.CreateHPA(svc.name, 2, 10, 70, 80); err != nil {
			log.Printf("Error creating HPA for %s: %v", svc.name, err)
			continue
		}

		// 3. Monitor service (run in goroutine for concurrent monitoring)
		go func(serviceName string) {
			if err := manager.MonitorService(serviceName); err != nil {
				log.Printf("Error monitoring %s: %v", serviceName, err)
			}
		}(svc.name)
	}

	// Keep the main goroutine alive
	select {}
}

// Helper function
func int32Ptr(i int32) *int32 {
	return &i
}
