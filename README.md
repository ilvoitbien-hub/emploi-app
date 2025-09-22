# Emploi App - Guide d'installation et d'exécution

Ce projet contient :
- Un **backend** (API Spring Boot + MongoDB)
- Un **frontend** (Next.js)

## 🚀 Installation et exécution sans Docker

### 1. Lancer le script d'installation

Exécutez ce script bash pour cloner les deux projets et préparer l'environnement :

```bash
#!/bin/bash

echo "=== Téléchargement des projets ==="

# Cloner le backend
git clone https://github.com/ilvoitbien-hub/emploi-app.git

# Cloner le frontend
git clone https://github.com/ilvoitbien-hub/mes-offres-app.git

echo "=== Backend téléchargé dans ./backend-app ==="
echo "=== Frontend téléchargé dans ./mes-offres-app ==="

echo ""
echo "➡️ Rendez-vous dans le dossier backend-app pour lancer l'API"
echo "   cd backend-app"
echo ""
echo "Vous pouvez ensuite :"
echo "  - Compiler et exécuter le jar :"
echo "      mvn clean package -DskipTests"
echo "      java -jar target/emploi-app-0.0.1-SNAPSHOT.jar"
echo ""
echo "  - OU lancer directement avec Maven :"
echo "      mvn spring-boot:run"
echo ""

echo "➡️ Préparation du frontend..."
cd mes-offres-app

# Installer les dépendances Node.js
npm install --legacy-peer-deps

echo ""
echo "=== Pour lancer le frontend (Next.js) ==="
echo "   cd mes-offres-app"
echo "   npm run dev"
echo ""
echo "Le front sera disponible sur http://localhost:3000"
```

## 📋 Prérequis système

### Backend (API Spring Boot)

- **Java 17** ou supérieur
- **Maven** 3.6+
- **MongoDB** (local ou instance distante)
- Dépendances principales :
    - Spring Boot Starter Web
    - Spring Data MongoDB
    - Spring Boot Starter Security
    - JWT pour l'authentification

### Frontend (Next.js)

- **Node.js** version 18 ou 20 (recommandé)
- **npm** ou **yarn**
- Dépendances principales :
    - React 18+
    - Next.js 14+
    - Axios pour les appels API
    - Bibliothèque UI (Material-UI, Tailwind CSS, etc.)

## 🔧 Configuration manuelle

### Backend

1. Assurez-vous que Java 17 est installé :
   ```bash
   java -version
   ```

2. Vérifiez Maven :
   ```bash
   mvn -version
   ```

3. Installez et configurez MongoDB si nécessaire

4. Configurez les variables d'environnement pour la base de données dans `application.properties`

### Frontend

1. Vérifiez la version de Node.js :
   ```bash
   node --version
   ```

2. Si nécessaire, installez Node.js 20 via nvm :
   ```bash
   nvm install 20
   nvm use 20
   ```

## 🛠️ Dépannage des dépendances

### Problèmes courants backend

- Erreur de version Java : installez JDK 17
- Problèmes Maven : exécutez `mvn clean install -DskipTests`
- Connexion MongoDB : vérifiez que MongoDB est démarré

### Problèmes courants frontend

- Erreurs de compatibilité Node : utilisez Node.js 18 ou 20
- Échec d'installation des dépendances :
  ```bash
  rm -rf node_modules package-lock.json
  npm cache clean --force
  npm install --legacy-peer-deps
  ```
- Problèmes de build : vérifiez la configuration dans `next.config.js`

## 🌐 Accès aux applications

- **API Backend** : http://localhost:8080
- **Frontend** : http://localhost:3000
- **Base de données** : MongoDB sur localhost:27017 (par défaut)
