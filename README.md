# Full-Stack DevOps Automation avec Jenkins, Docker, Minikube, Ansible & Prometheus

## 📌 Description du projet

Pipeline CI/CD complet automatisant :
- ✅ Provisioning d'infrastructure (DigitalOcean)
- ✅ Déploiement Jenkins dans Docker
- ✅ Build Maven d'application Java
- ✅ Création et push d'images Docker
- ✅ Déploiement sur Kubernetes local (Minikube)
- ✅ Monitoring avec Prometheus & Grafana

## 🛠️ Technologies utilisées

- **CI/CD** : Jenkins (multi-branch pipeline)
- **Containerisation** : Docker
- **Orchestration** : Kubernetes (Minikube)
- **IaC** : Ansible
- **Cloud** : DigitalOcean
- **Monitoring** : Prometheus, Grafana
- **Build** : Maven
- **VCS** : GitHub

## 🏗️ Architecture
```
┌─────────────────────────────────────────────────────┐
│              VM Ubuntu (VMware)                     │
│  ┌───────────────────────────────────────────────┐ │
│  │           Minikube (Kubernetes)               │ │
│  │  ┌──────────────┐  ┌──────────────┐          │ │
│  │  │  Java App    │  │ Prometheus   │          │ │
│  │  │  Deployment  │  │   Grafana    │          │ │
│  │  └──────────────┘  └──────────────┘          │ │
│  └───────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────┘
                        ↑
                        │ CI/CD Pipeline
                        │
┌─────────────────────────────────────────────────────┐
│         DigitalOcean Droplet                        │
│  ┌───────────────────────────────────────────────┐ │
│  │        Jenkins (Docker Container)             │ │
│  │  - Maven builds                               │ │
│  │  - Docker image creation                      │ │
│  │  - kubectl deployments                        │ │
│  └───────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────┘
```

## 📂 Structure du projet
```
.
├── Ansible/              # Playbooks Ansible
├── kubernetes/           # Manifests K8s
├── scripts/             # Scripts Bash automation
├── src/                 # Code source Java
├── Jenkinsfile          # Pipeline CI/CD
├── Dockerfile           # Image Docker
└── pom.xml             # Configuration Maven
```

## ⚙️ Configuration requise

- Ubuntu 20.04+ (VM ou physique)
- RAM : 8 GB minimum
- Docker
- Minikube
- kubectl
- Ansible

---

**Auteur** : Chaya  
**Contexte** : Préparation stage DevOps SAP France
