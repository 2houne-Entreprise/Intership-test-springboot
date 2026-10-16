# Internship Test — Spring Boot

Toutes les instructions et consignes relatives à ce projet sont décrites dans le fichier [ISSUE.md](./ISSUE.md) de ce dépôt.

Veuillez lire l'issue en entier avant de commencer.

## 🧱 Contenu du dépôt

Ce dépôt ne contient **volontairement aucun code** :

* `ISSUE.md` — le sujet du test technique (US1 à US8) ;
* `.github/workflows/maven-tests.yml` — le workflow d'intégration continue, qui exécute `./mvnw -B test` une fois le projet généré ;
* `.gitignore` — les fichiers Java / Maven / IDE à ne pas versionner.

La génération du projet Spring Boot (US1) fait partie du test : à vous de la réaliser.

## 🚀 Démarrage

```bash
# 1. Générer le projet (US1)
#    https://start.spring.io → Maven, Java 21, Spring Boot 4.1
#    dépendances : Spring Web, Thymeleaf, Spring Data JPA, Validation, Spring Security, H2

# 2. Lancer l'application
./mvnw spring-boot:run           # http://localhost:8080
                                 # console H2 : http://localhost:8080/h2-console

# 3. Tests et package
./mvnw test
./mvnw clean package
```

> Sous Windows, remplacez `./mvnw` par `mvnw.cmd`.

## Workflow

Pour contribuer à la résolution de l'issue, créez une nouvelle branche nommée `feature/<nom-stagiaire>` à partir de `develop`, résolvez l'issue, puis ouvrez une pull request vers `develop`.
# Intership-test-springboot
