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
	"k8s.io/apimachinery/pkg/api/errors"
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
							ImagePullPolicy: corev1.PullIfNotPresent, // set imagePullPolicy
							Ports: []corev1.ContainerPort{
								{
									Name:          "http",
									Protocol:      corev1.ProtocolTCP,
									ContainerPort: port,
								},
							},
							Env: []corev1.EnvVar{
								{
									Name:  "QUARKUS_DATASOURCE_JDBC_URL",
									Value: "jdbc:postgresql://postgres-ticketdb-cluster-rw.cnpg-system.svc.cluster.local:5432/ticketdb",
								},
								{
									Name:  "QUARKUS_DATASOURCE_JDBC_DRIVER",
									Value: "org.postgresql.Driver",
								},
								{
									Name: "QUARKUS_DATASOURCE_USERNAME",
									ValueFrom: &corev1.EnvVarSource{
										SecretKeyRef: &corev1.SecretKeySelector{
											LocalObjectReference: corev1.LocalObjectReference{
												Name: "postgres-ticketdb-cluster-app",
											},
											Key: "username",
										},
									},
								},
								{
									Name: "QUARKUS_DATASOURCE_PASSWORD",
									ValueFrom: &corev1.EnvVarSource{
										SecretKeyRef: &corev1.SecretKeySelector{
											LocalObjectReference: corev1.LocalObjectReference{
												Name: "postgres-ticketdb-cluster-app",
											},
											Key: "password",
										},
									},
								},
							},
						},
					},
				},
			},
		},
	}

	fmt.Printf("Creating deployment %s...\n", name)
	_, err := deploymentsClient.Create(context.TODO(), deployment, metav1.CreateOptions{})
	if err != nil {
		return err
	}
	fmt.Printf("Created deployment %s\n", name)
	return nil
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

func copySecret(clientset *kubernetes.Clientset, secretName string, sourceNamespace, targetNamespace string) error {
	// 获取源命名空间中的Secret
	secret, err := clientset.CoreV1().Secrets(sourceNamespace).Get(context.TODO(), secretName, metav1.GetOptions{})
	if err != nil {
		return fmt.Errorf("failed to get secret from namespace %s: %v", sourceNamespace, err)
	}

	// 构造新的 Secret 对象
	newSecret := &corev1.Secret{
		ObjectMeta: metav1.ObjectMeta{
			Name: secretName,
		},
		Type: corev1.SecretTypeBasicAuth, // 显式设置类型
		Data: secret.Data,                 // 直接复制数据
	}

	// 设置目标命名空间
	newSecret.Namespace = targetNamespace

	// 清除不允许在创建时设置的字段
	newSecret.ResourceVersion = ""
	newSecret.UID = ""
	newSecret.SelfLink = ""
	newSecret.ManagedFields = nil
	newSecret.CreationTimestamp = metav1.Time{}

	// 尝试在目标命名空间中创建或更新Secret
	_, err = clientset.CoreV1().Secrets(targetNamespace).Create(context.TODO(), newSecret, metav1.CreateOptions{})
	if err != nil {
		if errors.IsAlreadyExists(err) {
			// 如果Secret已经存在，则尝试更新它
			_, err = clientset.CoreV1().Secrets(targetNamespace).Update(context.TODO(), newSecret, metav1.UpdateOptions{})
			if err != nil {
				return fmt.Errorf("failed to update secret in namespace %s: %v", targetNamespace, err)
			}
		} else {
			return fmt.Errorf("failed to create secret in namespace %s: %v", targetNamespace, err)
		}
	}

	fmt.Printf("Copied/Updated secret %s to namespace %s\n", secretName, targetNamespace)
	return nil
}

func deleteSecret(clientset *kubernetes.Clientset, name, namespace string) error {
	secretsClient := clientset.CoreV1().Secrets(namespace)

	fmt.Printf("Deleting secret %s from namespace %s...\n", name, namespace)
	deletePolicy := metav1.DeletePropagationForeground
	err := secretsClient.Delete(context.TODO(), name, metav1.DeleteOptions{
		PropagationPolicy: &deletePolicy,
	})
	if err != nil {
		return fmt.Errorf("failed to delete secret %s from namespace %s: %v", name, namespace, err)
	}
	fmt.Printf("Deleted secret %s from namespace %s\n", name, namespace)
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
		// 复制Secret到default命名空间
		err = copySecret(clientset, "postgres-ticketdb-cluster-app", "cnpg-system", "default")
		if err != nil {
			panic(err.Error())
		}

		// 接下来是原有的创建Deployment和Service的逻辑...
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

		// 新增：删除Secret
		err = deleteSecret(clientset, "postgres-ticketdb-cluster-app", "default")
		if err != nil {
			// Secret可能不存在，可以忽略错误或打印提示
			fmt.Printf("Warning: failed to delete secret: %v\n", err)
		}
	default:
		fmt.Println("Invalid action. Please use 'create' or 'delete'.")
	}
}