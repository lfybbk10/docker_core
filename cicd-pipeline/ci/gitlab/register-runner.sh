#!/usr/bin/env bash
set -euo pipefail

if [[ $# -lt 1 ]]; then
  echo "Usage: $0 <registration-token> [gitlab-url]"
  exit 1
fi

REGISTRATION_TOKEN="$1"
GITLAB_URL="${2:-http://gitlab.local}"

docker exec -it gitlab-runner gitlab-runner register \
  --non-interactive \
  --url "${GITLAB_URL}" \
  --registration-token "${REGISTRATION_TOKEN}" \
  --executor "docker" \
  --docker-image "eclipse-temurin:21-jdk" \
  --description "docker-java-runner" \
  --tag-list "docker,java,k8s" \
  --run-untagged="true" \
  --locked="false" \
  --docker-privileged="true" \
  --docker-volumes "/var/run/docker.sock:/var/run/docker.sock"

