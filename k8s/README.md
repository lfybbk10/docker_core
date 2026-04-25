## Kubernetes Deployment

# Kubernetes Deployment

## Установка Minikube

## Запуск кластера bash
```minikube start```

## Загрузка образа bash
```minikube image load```

## Применение манифестов bash
```kubectl apply -f k8s/```

## Доступ к приложению bash
```kubectl port-forward service/имя-сервиса 8080:80```

## Полезные команды 
- ```kubectl get pods``` - список pods 
- ```kubectl logs pod-name``` - логи 
- ```kubectl describe pod pod-name``` - детальная информация
