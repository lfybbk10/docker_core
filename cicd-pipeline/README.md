# MCLOUD-15: CI/CD и автоматический деплой в Kubernetes

Учебный проект показывает полный путь от коммита до деплоя Spring Boot приложения в Kubernetes через `Jib`, `GitHub Actions`, `GitLab CI` и `Jenkins`.

## Что внутри

- Spring Boot 3.2 приложение с endpoint'ами `/` и `/health`.
- Сборка контейнеров без Docker daemon через `Jib`.
- GitHub Actions pipeline `test -> build/push -> deploy -> health-check -> notify`.
- GitLab pipeline с локальным include и параллельной валидацией.
- Jenkins declarative pipeline с `parallel` stage и rollback при неудачном rollout.
- Kubernetes manifests для deployment, service и rollback job.
- Docker Compose для локального GitLab, GitLab Runner и Jenkins.

## Структура

```text
cicd-pipeline/
├── src/main/java/ru/mentee/power/
├── src/test/java/ru/mentee/power/
├── .github/workflows/deploy.yml
├── ci/
│   ├── .gitlab-ci.yml
│   ├── Jenkinsfile
│   ├── docker-compose-cicd.yml
│   ├── gitlab/register-runner.sh
│   └── jenkins/init.groovy.d/jenkins-setup.groovy
├── k8s/
│   ├── deployment.yaml
│   ├── service.yaml
│   └── rollback.yaml
├── build.gradle
├── settings.gradle
└── README.md
```

## Локальный запуск

```bash
./gradlew test
./gradlew bootRun
curl http://localhost:8080/
curl http://localhost:8080/health
```

## Jib и версионирование

- Базовый образ: `eclipse-temurin:21-jre-alpine`.
- Реестр образов задается через `IMAGE_REPOSITORY`.
- Теги задаются через `IMAGE_TAGS`.
- Версия для CI берется из `git describe --tags --always --dirty`.
- Для production-релизов используйте теги вида `v1.2.3`.

Пример локальной сборки:

```bash
IMAGE_REPOSITORY=ghcr.io/acme/cicd-pipeline \
IMAGE_TAGS=latest,1.0.0 \
REGISTRY_USERNAME=<user> \
REGISTRY_PASSWORD=<token> \
./gradlew jib
```

## GitHub Actions secrets

- `KUBE_CONFIG_B64` - base64 от kubeconfig для кластера или minikube.
- `SLACK_WEBHOOK_URL` - optional webhook для уведомлений.
- `GITHUB_TOKEN` используется для push в `ghcr.io`.

Для `minikube` можно подготовить secret так:

```bash
kubectl config view --raw | base64 -w0
```

## GitHub Actions pipeline

- `test` запускает `./gradlew test`.
- `package-preview` запускает `./gradlew bootJar -x test` параллельно с тестами, чтобы показать независимую сборку.
- `build-and-push` собирает OCI image через Jib и публикует теги `latest`, `git tag`, `short sha`.
- `deploy` применяет манифесты и обновляет image в deployment.
- `post-deploy-health` делает in-cluster curl на `http://cicd-app:8080/health`.
- `notify` пишет summary и опционально отправляет сообщение в Slack.

## GitLab и Jenkins локально

Поднять сервисы:

```bash
docker compose -f ci/docker-compose-cicd.yml up -d
```

Регистрация runner:

```bash
./ci/gitlab/register-runner.sh <registration-token> http://gitlab.local
```

Jenkins поднимается на `http://localhost:8081/jenkins`.

- Login: `admin`
- Password: `admin123`
- Script Path для pipeline: `ci/Jenkinsfile`

## Webhooks

GitHub:

- `Settings -> Webhooks -> Add webhook`
- URL: `http://<jenkins-or-gitlab>/webhook`
- Events: `Push`, `Pull request`

GitLab:

- `Settings -> Webhooks`
- URL для Jenkins: `http://localhost:8081/jenkins/project/<job-name>`
- В Jenkins должны быть включены `gitlab` trigger и credentials для GitLab token.

## Kubernetes деплой и rollback

Пайплайн делает:

```bash
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/deployment.yaml
kubectl set image deployment/cicd-app cicd-app=<image>:<version>
kubectl rollout status deployment/cicd-app
```

Rollback при ошибке:

```bash
kubectl rollout undo deployment/cicd-app
```

Альтернативный rollback job описан в [k8s/rollback.yaml](k8s/rollback.yaml).

## Что проверить на защите

- Push в `main` запускает pipeline автоматически.
- Tag `v1.0.0` приводит к версионированной сборке образа.
- После деплоя `kubectl get pods -n cicd-demo` показывает новый rollout.
- `curl http://localhost:8080/health` внутри кластера возвращает `UP`.
- При искусственной ошибке в deployment срабатывает `rollout undo`.
