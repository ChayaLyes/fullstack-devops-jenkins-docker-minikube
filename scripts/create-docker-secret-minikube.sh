#!/bin/bash

# Script pour créer le secret Docker Hub dans Minikube

read -p "Enter your Docker Hub username: " DOCKER_USERNAME
read -sp "Enter your Docker Hub password or token: " DOCKER_PASSWORD
echo

# Créer le secret Kubernetes
kubectl create secret docker-registry my-registry-key \
  --docker-server=docker.io \
  --docker-username=$DOCKER_USERNAME \
  --docker-password=$DOCKER_PASSWORD \
  --docker-email=your-email@example.com \
  --dry-run=client -o yaml | kubectl apply -f -

echo "✅ Docker registry secret created successfully!"

# Vérifier le secret
kubectl get secret my-registry-key
