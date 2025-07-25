package main

import (
	"context"
	"flag"
	"fmt"
	"os"
	"path/filepath"

	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/util/intstr"
	"k8s.io/client-go/kubernetes"
	"k8s.io/client-go/rest"
	"k8s.io/client-go/tools/clientcmd"
)

func createDeployment(clientset *kubernetes.Clientset, name, image string, port int32) error {
	deploymentsClient := clientset.AppsV1().Deployments("default")

	deployment := &appsv1.Deployment{
		ObjectMeta: metav1.ObjectMeta{
			Name: name,
		},
		Spec: appsv1.DeploymentSpec{
			Replicas: int32Ptr(1),
			Selector: &metav1.LabelSelector{
				MatchLabels: map[string]string{"app": name},
			},
			Template: corev1.PodTemplateSpec{
				ObjectMeta: metav1.ObjectMeta{
					Labels: map[string]string{"app": name},
				},
				Spec: corev1.PodSpec{
					Containers: []corev1.Container{
						{
							Name:            name,
							Image:           image,
							ImagePullPolicy: corev1.PullIfNotPresent,
							Ports: []corev1.ContainerPort{
								{
									Name:          "http",
									Protocol:      corev1.ProtocolTCP,
									ContainerPort: port,
								},
							},
							Env: []corev1.EnvVar{
								// 数据库配置
								{
									Name:  "QUARKUS_DATASOURCE_JDBC_URL",
									Value: "jdbc:postgresql://postgres:5432/ticketdb",
								},
								{
									Name:  "QUARKUS_DATASOURCE_JDBC_DRIVER",
									Value: "org.postgresql.Driver",
								},
								{
									Name:  "QUARKUS_DATASOURCE_USERNAME",
									Value: "postgres",
								},
								{
									Name:  "QUARKUS_DATASOURCE_PASSWORD",
									Value: "yourpassword",
								},
								// CORS 配置
								{
									Name:  "QUARKUS_HTTP_CORS_ORIGINS",
									Value: "http://a73ba1d08df5f4e54beb2bb05ba0c9ff-1118012893.us-east-1.elb.amazonaws.com",
								},
								{
									Name:  "QUARKUS_HTTP_CORS_METHODS",
									Value: "GET,POST,PUT,DELETE,OPTIONS",
								},
								{
									Name:  "QUARKUS_HTTP_CORS_ACCESS_CONTROL_ALLOW_HEADERS",
									Value: "Authorization,Content-Type",
								},
								{
									Name:  "quarkus.rest-client.operations-api.url",
									Value: "http://operations-management-service.default.svc.cluster.local:8080",
								},
							},
						},
					},
				},
			},
		},
	}

	// 创建 Deployment
	_, err := deploymentsClient.Create(context.TODO(), deployment, metav1.CreateOptions{})
	return err
}


func createService(clientset *kubernetes.Clientset, name string, targetPort, servicePort int32) error {
	servicesClient := clientset.CoreV1().Services("default")

	service := &corev1.Service{
		ObjectMeta: metav1.ObjectMeta{
			Name: name,
		},
		Spec: corev1.ServiceSpec{
			Selector: map[string]string{"app": name},
			Ports: []corev1.ServicePort{
				{
					Name:       "http",
					Port:       servicePort,
					TargetPort: intstr.FromInt(int(targetPort)),
					Protocol:   corev1.ProtocolTCP,
				},
			},
			Type: corev1.ServiceTypeNodePort,
		},
	}

	fmt.Printf("Creating service %s...\n", name)
	_, err := servicesClient.Create(context.TODO(), service, metav1.CreateOptions{})
	if err != nil {
		return err
	}
	fmt.Printf("Created service %s\n", name)
	return nil
}

func deleteDeployment(clientset *kubernetes.Clientset, name string) error {
    deploymentsClient := clientset.AppsV1().Deployments("default")

    fmt.Printf("Deleting deployment %s...\n", name)
    deletePolicy := metav1.DeletePropagationForeground
    if err := deploymentsClient.Delete(context.TODO(), name, metav1.DeleteOptions{
        PropagationPolicy: &deletePolicy,
    }); err != nil {
        return err
    }
    fmt.Printf("Deleted deployment %s\n", name)
    return nil
}

func deleteService(clientset *kubernetes.Clientset, name string) error {
    servicesClient := clientset.CoreV1().Services("default")

    fmt.Printf("Deleting service %s...\n", name)
    deletePolicy := metav1.DeletePropagationForeground
    if err := servicesClient.Delete(context.TODO(), name, metav1.DeleteOptions{
        PropagationPolicy: &deletePolicy,
    }); err != nil {
        return err
    }
    fmt.Printf("Deleted service %s\n", name)
    return nil
}

func int32Ptr(i int32) *int32 { return &i }

func homeDir() string {
	if h := os.Getenv("HOME"); h != "" {
		return h
	}
	return os.Getenv("USERPROFILE") // windows
}

func main() {
	var kubeconfig *string
	var action *string

	if home := homeDir(); home != "" {
		kubeconfig = flag.String("kubeconfig", filepath.Join(home, ".kube", "config"), "(optional) absolute path to the kubeconfig file")
	} else {
		kubeconfig = flag.String("kubeconfig", "", "absolute path to the kubeconfig file")
	}

	action = flag.String("action", "create", "Action to perform: 'create' or 'delete'")
	flag.Parse()

	var config *rest.Config
	var err error
	if _, err := os.Stat(*kubeconfig); err == nil {
		config, err = clientcmd.BuildConfigFromFlags("", *kubeconfig)
	} else {
		config, err = rest.InClusterConfig()
	}
	if err != nil {
		panic(err.Error())
	}

	clientset, err := kubernetes.NewForConfig(config)
	if err != nil {
		panic(err.Error())
	}

	switch *action {
	case "create":
		// 创建 order-management-service
		err = createDeployment(clientset, "order-management-service", "admininit/ticket-system-order-management-service:latest", 50081)
		if err != nil {
			panic(err.Error())
		}
		err = createService(clientset, "order-management-service", 50081, 8081)
		if err != nil {
			panic(err.Error())
		}

		// 创建 operations-management-service
		err = createDeployment(clientset, "operations-management-service", "admininit/ticket-system-operations-management-service:latest", 50080)
		if err != nil {
			panic(err.Error())
		}
		err = createService(clientset, "operations-management-service", 50080, 8080)
		if err != nil {
			panic(err.Error())
		}
	case "delete":
		// 删除 order-management-service
		err = deleteDeployment(clientset, "order-management-service")
		if err != nil {
			panic(err.Error())
		}
		err = deleteService(clientset, "order-management-service")
		if err != nil {
			panic(err.Error())
		}

		// 删除 operations-management-service
		err = deleteDeployment(clientset, "operations-management-service")
		if err != nil {
			panic(err.Error())
		}
		err = deleteService(clientset, "operations-management-service")
		if err != nil {
			panic(err.Error())
		}
	default:
		fmt.Println("Invalid action. Please use 'create' or 'delete'.")
	}
}