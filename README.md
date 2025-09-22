# 🚀 Tanaye Backend - Application de Transport Sécurisée

## 📋 Description

Tanaye Backend est une application Spring Boot sécurisée pour la gestion d'un système de transport. L'application implémente les meilleures pratiques de sécurité et de clean code.

## 🔐 Sécurité

Cette application a été conçue avec la sécurité comme priorité absolue :

- ✅ **Variables d'environnement** pour toutes les données sensibles
- ✅ **Chiffrement AES-256** des propriétés sensibles
- ✅ **Validation automatique** des configurations au démarrage
- ✅ **Logging sécurisé** sans exposition de secrets
- ✅ **Tests de sécurité** automatisés

## 🚀 Démarrage Rapide

### Prérequis

- Java 21+
- Maven 3.6+
- PostgreSQL 12+
- Variables d'environnement configurées

### 1. Configuration des Variables d'Environnement

Copiez le fichier d'exemple et configurez vos variables :

```bash
cp src/main/resources/env.example .env.local
```

Éditez `.env.local` avec vos valeurs réelles :

```bash
# Base de données
DATABASE_URL=jdbc:postgresql://localhost:5432/tanaye_dev
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your_secure_password

# JWT (générez une clé sécurisée)
JWT_SECRET=your_very_secure_jwt_secret_key_minimum_32_chars

# Supabase
SUPABASE_PROJECT_URL=https://your-project.supabase.co
SUPABASE_API_KEY=your_supabase_service_role_key
SUPABASE_BUCKET=your_bucket_name
```

### 2. Génération de Clés Sécurisées

Utilisez les scripts fournis pour générer des clés robustes :

```bash
# Linux/Mac
./scripts/generate-secrets.sh

# Windows
scripts\generate-secrets.bat
```

### 3. Tests de Sécurité

Vérifiez que votre configuration est sécurisée :

```bash
# Linux/Mac
./scripts/test-security.sh

# Windows
scripts\test-security.bat
```

### 4. Démarrage de l'Application

```bash
# Développement
mvn spring-boot:run

# Production
mvn package
java -jar target/tanaye_back-0.0.1-SNAPSHOT.jar
```

## 🛠️ Fonctionnalités

### Authentification et Autorisation

- **JWT** pour l'authentification stateless
- **Spring Security** pour l'autorisation
- **Gestion des rôles** (ADMIN, BIBLIOTHECAIRE, USER)
- **Protection contre les attaques** par force brute

### Gestion des Données

- **JPA/Hibernate** pour l'ORM
- **PostgreSQL** comme base de données
- **Audit automatique** des entités
- **Validation des données** avec Bean Validation

### API et Documentation

- **REST API** complète
- **Swagger/OpenAPI** pour la documentation
- **Gestion des erreurs** centralisée
- **CORS** configuré pour le frontend

### Sécurité Avancée

- **Chiffrement** des propriétés sensibles
- **Validation** des configurations
- **Logging sécurisé** sans exposition de secrets
- **Tests de sécurité** automatisés

## 📁 Structure du Projet

```
tanaye_back/
├── src/main/java/com/tanaye/www/
│   ├── configuration/          # Configurations Spring
│   ├── service/                # Services métier
│   └── ...
├── src/main/resources/
│   ├── application.properties  # Configuration par défaut
│   ├── application-prod.properties  # Configuration production
│   └── env.example            # Exemple de variables d'environnement
├── scripts/                   # Scripts utilitaires
│   ├── generate-secrets.bat   # Génération de clés
│   └── test-security.bat      # Tests de sécurité
└── SECURITY.md               # Guide de sécurité
```

## 🔧 Configuration

### Variables d'Environnement Obligatoires

| Variable               | Description                 | Exemple                                   |
| ---------------------- | --------------------------- | ----------------------------------------- |
| `DATABASE_URL`         | URL de la base de données   | `jdbc:postgresql://localhost:5432/tanaye` |
| `DATABASE_PASSWORD`    | Mot de passe de la base     | `secure_password_123`                     |
| `JWT_SECRET`           | Clé secrète JWT (≥32 chars) | `your_very_secure_jwt_secret_key`         |
| `SUPABASE_API_KEY`     | Clé API Supabase            | `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...` |
| `SUPABASE_PROJECT_URL` | URL du projet Supabase      | `https://project.supabase.co`             |

### Variables d'Environnement Optionnelles

| Variable             | Description               | Défaut |
| -------------------- | ------------------------- | ------ |
| `SERVER_PORT`        | Port du serveur           | `9999` |
| `HIBERNATE_SHOW_SQL` | Afficher les requêtes SQL | `true` |
| `MAX_FILE_SIZE`      | Taille max des fichiers   | `50MB` |

## 🧪 Tests

### Tests Unitaires

```bash
mvn test
```

### Tests de Sécurité

```bash
scripts\test-security.bat
```

### Tests d'Intégration

```bash
mvn verify
```

## 🚀 Déploiement

### Déploiement

```bash
# 1. Tests de sécurité
scripts\test-security.bat

# 2. Compilation
mvn clean package

# 3. Déploiement
java -jar target/tanaye_back-0.0.1-SNAPSHOT.jar
```

## 📚 Documentation

- [Guide de Sécurité](SECURITY.md) - Bonnes pratiques de sécurité
- [API Documentation](http://localhost:9999/swagger-ui.html) - Documentation Swagger
- [Variables d'Environnement](src/main/resources/env.example) - Configuration

## 🤝 Contribution

1. **Fork** le projet
2. **Créez** une branche feature (`git checkout -b feature/AmazingFeature`)
3. **Commitez** vos changements (`git commit -m 'Add some AmazingFeature'`)
4. **Poussez** vers la branche (`git push origin feature/AmazingFeature`)
5. **Ouvrez** une Pull Request

### Règles de Contribution

- ✅ Suivez les principes de clean code
- ✅ Écrivez des tests pour les nouvelles fonctionnalités
- ✅ Vérifiez la sécurité avec les scripts fournis
- ✅ Documentez les changements importants

## 🔒 Sécurité

Si vous découvrez une vulnérabilité de sécurité :

1. **Ne créez pas** d'issue publique
2. **Contactez** l'équipe de sécurité
3. **Attendez** la confirmation avant de divulguer

## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.

## 👥 Équipe

- **Développement** : Équipe Tanaye
- **Sécurité** : Équipe Tanaye
- **DevOps** : Équipe Tanaye

## 📞 Support

- **Documentation** : [Guide de Sécurité](SECURITY.md)
- **Issues** : [GitHub Issues](https://github.com/tanaye/issues)
- **Email** : support@tanaye.com

---

**⚠️ IMPORTANT** : Cette application gère des données sensibles. Suivez toujours les bonnes pratiques de sécurité et ne commitez jamais de secrets dans le code source.
