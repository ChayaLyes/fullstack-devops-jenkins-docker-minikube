# Full-Stack DevOps Automation

> Pipeline CI/CD complet : DigitalOcean · Jenkins · Docker · Kubernetes · Ansible · Prometheus · Grafana

---

## Description

Ce projet implémente un pipeline CI/CD de bout en bout qui automatise :

- Provisioning d'un serveur Jenkins sur **DigitalOcean** via **Ansible**
- Build et tests d'une application **Java Spring Boot** avec **Maven**
- Création et push d'une image **Docker** vers Docker Hub
- Déploiement sur **Kubernetes (Minikube)** avec autoscaling (HPA)
- Monitoring complet via **Prometheus** (métriques `/actuator/prometheus`) et **Grafana**

---

## Architecture

```
GitHub (push)
     │
     ▼
┌──────────────────────────────────────────┐
│  DigitalOcean Droplet                    │
│  ┌────────────────────────────────────┐  │
│  │  Jenkins (Docker)                  │  │
│  │  • mvn clean package               │  │
│  │  • docker build & push             │  │
│  │  • kubectl apply                   │  │
│  └────────────────────────────────────┘  │
└──────────────────────────────────────────┘
     │
     ▼
┌──────────────────────────────────────────┐
│  Minikube (Kubernetes local)             │
│                                          │
│  default namespace                       │
│  ┌──────────────────┐                   │
│  │  java-maven-app  │  :30080           │
│  │  replicas: 2-5   │  (HPA CPU/RAM)    │
│  └──────────────────┘                   │
│                                          │
│  monitoring namespace                    │
│  ┌────────────┐  ┌─────────┐            │
│  │ Prometheus │  │ Grafana │            │
│  │   :30090   │  │  :30030 │            │
│  └────────────┘  └─────────┘            │
└──────────────────────────────────────────┘
```

---

## Technologies

| Couche | Outil |
|--------|-------|
| CI/CD | Jenkins (pipeline déclaratif) |
| Build | Maven 3.9 + Spring Boot 3.2 |
| Conteneurisation | Docker + Docker Hub |
| Orchestration | Kubernetes / Minikube |
| IaC | Ansible + community.digitalocean |
| Cloud | DigitalOcean |
| Monitoring | Prometheus + Grafana |
| VCS | GitHub |

---

## Structure du projet

```
.
├── ansible/
│   ├── ansible.cfg
│   ├── requirements.yml
│   ├── group_vars/all.yml          # Variables globales (région DO, ports…)
│   ├── inventory/hosts.yml         # IP injectée après provision
│   ├── playbooks/
│   │   ├── provision_droplet.yml   # Crée le droplet DigitalOcean
│   │   └── deploy_jenkins.yml      # Installe Docker + Jenkins
│   └── roles/
│       ├── docker/                 # Installation Docker CE
│       └── jenkins/                # Déploiement Jenkins via Docker Compose
│
├── docker/
│   └── jenkins/
│       ├── Dockerfile              # Jenkins + Docker CLI + kubectl + envsubst
│       ├── docker-compose.yml
│       └── plugins.txt             # Plugins pré-installés
│
├── kubernetes/
│   ├── deployment.yaml             # App + Service (NodePort 30080)
│   ├── hpa.yaml                    # HorizontalPodAutoscaler (2-5 replicas)
│   ├── secret.yaml                 # Doc pour créer le pull secret Docker Hub
│   └── monitoring/
│       ├── prometheus-configmap.yaml
│       ├── prometheus-deployment.yaml  # NodePort 30090
│       └── grafana-deployment.yaml     # NodePort 30030 — datasource auto-configurée
│
├── scripts/
│   └── create-docker-secret-minikube.sh
│
├── src/
│   └── main/java/com/example/app/
│       ├── App.java                # Point d'entrée Spring Boot
│       └── AppController.java      # GET / et GET /info
│
├── Dockerfile                      # Image runtime (copie target/*.jar)
├── Jenkinsfile                     # Pipeline 8 stages
└── pom.xml                         # Spring Boot + Actuator + Micrometer
```

---

## Démarrage rapide

### 1. Prérequis

```bash
# Outils requis
docker, docker compose, minikube, kubectl, ansible, maven 3.9+

# Installer les collections Ansible
cd ansible
ansible-galaxy collection install -r requirements.yml
```

### 2. Provisionner l'infrastructure (DigitalOcean)

```bash
export DO_API_TOKEN=<votre-token-digitalocean>

ansible-playbook ansible/playbooks/provision_droplet.yml
# → IP du droplet injectée automatiquement dans ansible/inventory/hosts.yml

ansible-playbook ansible/playbooks/deploy_jenkins.yml
# → Jenkins accessible sur http://<droplet-ip>:8080
```

### 3. Démarrer Jenkins localement (sans DigitalOcean)

```bash
cd docker/jenkins
docker compose up -d
# → Jenkins sur http://localhost:8080
```

### 4. Créer les credentials Jenkins

Dans **Jenkins > Manage Jenkins > Credentials** :

| ID | Type | Contenu |
|----|------|---------|
| `docker-hub-credentials` | Username/Password | Docker Hub login |
| `github-credentials` | Username/Password | GitHub token |

### 5. Déployer sur Minikube

```bash
minikube start
minikube addons enable metrics-server   # requis pour HPA

# Créer le pull secret Docker Hub
bash scripts/create-docker-secret-minikube.sh

# Déployer le monitoring
kubectl apply -f kubernetes/monitoring/
```

### 6. Lancer le pipeline Jenkins

Créer un **Multibranch Pipeline** pointant sur ce repo.  
Le pipeline exécute automatiquement les 8 stages :

```
Checkout → Increment Version → Build → Test → Docker Build
→ Docker Push → Deploy to Minikube → Deploy Monitoring → Verify
```

---

## Accès aux services

| Service | URL | Credentials |
|---------|-----|-------------|
| Application | `http://$(minikube ip):30080` | — |
| Prometheus | `http://$(minikube ip):30090` | — |
| Grafana | `http://$(minikube ip):30030` | admin / admin123 |
| Jenkins (local) | http://localhost:8080 | — |

```bash
# Ouvrir directement dans le navigateur
minikube service java-maven-app-service
minikube service grafana-service -n monitoring
minikube service prometheus-service -n monitoring
```

---

## Endpoints de l'application

| Endpoint | Description |
|----------|-------------|
| `GET /` | Nom de l'app, statut, heure |
| `GET /info` | Version Java, OS |
| `GET /actuator/health` | Statut Spring Boot (liveness/readiness) |
| `GET /actuator/prometheus` | Métriques Micrometer pour Prometheus |

---

## Autoscaling

Le HPA (`kubernetes/hpa.yaml`) adapte automatiquement le nombre de pods :

- **Min** : 2 replicas
- **Max** : 5 replicas
- **Seuils** : CPU > 70% ou RAM > 80%

```bash
kubectl get hpa
kubectl top pods
```

---

**Auteur** : Chaya Lyes  
**Contexte** : Projet DevOps — Jenkins · Docker · Kubernetes · Ansible · Prometheus
