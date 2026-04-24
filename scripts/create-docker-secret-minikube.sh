#!/bin/bash

# Script pour créer le secret Docker Hub dans Minikube

read -p "Enter your Docker Hub username: " DOCKER_USERNAME
read -sp "Enter your Docker Hub password or token: " DOCKER_PASSWORD
echo
read -p "Enter your email: " DOCKER_EMAIL

kubectl create secret docker-registry my-registry-key \
  --docker-server=docker.io \
  --docker-username="$DOCKER_USERNAME" \
  --docker-password="$DOCKER_PASSWORD" \
  --docker-email="$DOCKER_EMAIL" \
  --dry-run=client -o yaml | kubectl apply -f -

echo "Docker registry secret created successfully!"

kubectl get secret my-registry-key
