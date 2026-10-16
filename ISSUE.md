# 🏷️ Titre : [Test Technique Stagiaire] Développement de "TaskFlow" - Gestionnaire de tâches collaboratif (Spring Boot)

## 📝 Contexte et Objectifs

Dans le cadre de votre stage, nous souhaitons évaluer vos compétences sur le framework **Spring Boot**. Vous devez développer une petite application nommée **"TaskFlow"**.

L'objectif n'est pas d'avoir une UI parfaite, mais de démontrer que vous maîtrisez les fondamentaux de Spring Boot (contrôleurs MVC, Thymeleaf, Spring Data JPA, migrations/entités, Bean Validation, Spring Security, etc.) et que vous écrivez un code propre, moderne et maintenable.

> 💡 Ce dépôt ne contient **volontairement aucun code** : vous devez générer le projet vous-même (US1), puis implémenter les user stories les unes après les autres. Les seuls fichiers fournis sont ce document, le `README.md` et le workflow d'intégration continue.

## ⏱️ Durée estimée

**4 à 6 heures** (À réaliser seul(e), sans aide extérieure ni copie de code existant).

## 🛠️ Prérequis techniques

* **Java 21** (LTS) installé (`java -version`).
* **Spring Boot 4.1** : générez le projet avec [Spring Initializr](https://start.spring.io) (ou votre IDE) et versionnez le wrapper Maven `./mvnw` (aucun Maven à installer sur la machine).
* Utilisation de **H2 en mode fichier** (pour faciliter la configuration de la base de données sans avoir à installer MySQL/PostgreSQL). La configuration se fait dans `src/main/resources/application.properties`.
* L'authentification doit être mise en place **par vos soins** avec **Spring Security** (page de connexion personnalisée, inscription, déconnexion).

---

## 📋 User Stories et Critères d'Acceptation

Veuillez cocher les cases au fur et à mesure de votre avancement en faisant des commits réguliers.

### US1 : Initialisation du projet et Authentification

**En tant que** visiteur
**Je veux** pouvoir m'inscrire et me connecter
**Afin de** accéder à mon espace personnel.

- [ ] **CA1 :** Le projet Spring Boot 4.1 (Java 21, Maven) est généré, le wrapper `./mvnw` est versionné, `spring-boot-starter-thymeleaf`, `spring-boot-starter-validation` et `spring-boot-starter-data-jpa` sont déclarés, et la base de données H2 en mode fichier est configurée dans `application.properties`.
- [ ] **CA2 :** **Spring Security** est configuré : `SecurityFilterChain`, page de connexion personnalisée (Thymeleaf), inscription (contrôleur + service + entité utilisateur persistée avec un mot de passe haché) et déconnexion.
- [ ] **CA3 :** Les routes `/login`, `/register` et `/logout` fonctionnent correctement, et la page protégée de votre espace (`/dashboard` ou `/projects`) redirige un visiteur non connecté vers la page de connexion.

### US2 : Gestion des Projets (CRUD)

**En tant que** utilisateur connecté
**Je veux** créer, lister, modifier et supprimer des projets
**Afin d'** organiser mes tâches.

- [ ] **CA1 :** Une entité JPA `Project` est créée (`@Entity`) avec les champs : `id`, `user` (`@ManyToOne` vers l'utilisateur créateur), `name` (`String`), `description` (`@Column(columnDefinition = "TEXT")`, nullable), `createdAt`, `updatedAt`. Le schéma est créé/mis à jour par le schéma JPA (ou par une migration versionnée de votre choix).
- [ ] **CA2 :** L'entité `Project` possède la relation `@ManyToOne` vers `User` et le côté inverse (`@OneToMany(mappedBy = "user")`) est présent sur `User`.
- [ ] **CA3 :** Un `ProjectRepository` (Spring Data JPA) expose les méthodes nécessaires, dont une méthode filtrant par propriétaire (ex : `findByUserId(...)` ou une requête dérivée équivalente).
- [ ] **CA4 :** Les routes sont regroupées dans un contrôleur dédié (`ProjectController`) : `GET /projects` (index), `GET /projects/new` + `POST /projects` (store), `GET /projects/{id}/edit` + `POST /projects/{id}` (update), `POST /projects/{id}/delete` (destroy). Toutes exigent un utilisateur authentifié.
- [ ] **CA5 :** Le contrôleur utilise une approche "resource" (une action par opération CRUD) et ne contient pas de logique métier complexe (elle est déléguée au service).
- [ ] **CA6 :** L'utilisateur ne peut voir/modifier/supprimer que ses **propres** projets (requêtes filtrées par l'utilisateur courant, jamais un `findById()` seul).

### US3 : Gestion des Tâches avec Validation

**En tant que** utilisateur connecté
**Je veux** ajouter des tâches à mes projets avec un statut
**Afin de** suivre leur avancement.

- [ ] **CA1 :** Une entité JPA `Task` est créée : `id`, `project` (`@ManyToOne`), `title` (`String`, `length = 255`), `status` (énumération : `PENDING`, `IN_PROGRESS`, `DONE`, persistée en `@Enumerated(EnumType.STRING)`), `deadline` (`LocalDate`, nullable), `createdAt`, `updatedAt`.
- [ ] **CA2 :** L'entité `Task` possède les relations appropriées (`Task.project` et `Project.tasks`).
- [ ] **CA3 :** La création et la modification d'une tâche utilisent un **objet de formulaire validé** (`TaskForm`, `@ModelAttribute` + `@Valid`) — équivalent d'une Form Request.
- [ ] **CA4 :** Les règles de validation (Bean Validation + `BindingResult`) imposent : `title` obligatoire (`@NotBlank`, `@Size(max = 255)`), `status` obligatoire (doit être l'une des 3 valeurs), `deadline` doit être une date valide (`@DateTimeFormat(iso = ISO.DATE)`).
- [ ] **CA5 :** En cas d'échec de validation, les erreurs s'affichent clairement dans le template Thymeleaf (`th:errors="*{title}"`) et l'ancienne saisie est conservée (`th:field="*{title}"`).

### US4 : Templates Thymeleaf et Layout

**En tant que** utilisateur
**Je veux** avoir une interface claire et cohérente
**Afin de** naviguer facilement dans l'application.

- [ ] **CA1 :** Création d'un layout principal réutilisable (`templates/fragments/layout.html`) sous forme de **fragment Thymeleaf** (`th:fragment="layout(title, content)"`), utilisé par toutes les pages via `th:replace`.
- [ ] **CA2 :** Utilisation de **fragments réutilisables** (ex : `templates/fragments/alerts.html`) pour afficher les messages de succès ou d'erreur de façon réutilisable (flash attributes / `RedirectAttributes`).
- [ ] **CA3 :** Sur la page de détail d'un projet, les tâches sont affichées dans un tableau avec un formulaire pour changer le statut rapidement.

### US5 : Sécurité et Autorisation (Logique métier)

**En tant qu'** administrateur système (Contrainte technique)
**Je veux** m'assurer qu'un utilisateur ne peut pas interagir avec un projet qui ne lui appartient pas
**Afin de** garantir la sécurité des données.

- [ ] **CA1 :** Création d'un **`HandlerInterceptor`** `ProjectOwnershipInterceptor` (équivalent du middleware `EnsureUserOwnsProject`), enregistré dans une classe de configuration `WebMvcConfigurer` (`addInterceptors`).
- [ ] **CA2 :** L'intercepteur vérifie que le `projectId` présent dans les variables de chemin appartient bien à l'utilisateur authentifié. Si non, il renvoie une erreur **403** (retourner un `HttpServletResponse.SC_FORBIDDEN` ou lever une `AccessDeniedException`).
- [ ] **CA3 :** L'intercepteur (ou un contrôle équivalent dans le service) est appliqué aux routes de modification/suppression des projets **et** des tâches associées.

### US6 : Fonctionnalités avancées de JPA (Requêtes dérivées & Accessors)

**En tant qu'** utilisateur
**Je veux** voir mes tâches triées et formatées proprement
**Afin de** gagner du temps dans ma lecture.

- [ ] **CA1 :** Création d'une **requête dérivée** dans `TaskRepository` permettant de filtrer les tâches en retard (ex : `List<Task> findByDeadlineBeforeAndStatusNot(LocalDate date, TaskStatus status)`), exposée via une méthode de service du type `findOverdue`.
- [ ] **CA2 :** Création d'une **méthode d'accès** sur l'entité `Task` pour formater le statut en français (ex : si `status` est `PENDING`, `task.getStatusLabel()` retourne "En attente").
- [ ] **CA3 :** Utilisation du **fetching explicite** (`@EntityGraph`, `JOIN FETCH` ou `project.tasks` chargé via une requête dédiée) lors de la récupération des projets pour éviter le problème des requêtes N+1 sur les tâches (`spring.jpa.open-in-view` est désactivé).

### US7 : Upload de fichiers (Storage)

**En tant qu'** utilisateur
**Je veux** pouvoir attacher une pièce jointe à une tâche
**Afin de** fournir des documents complémentaires.

- [ ] **CA1 :** Ajout d'un champ `attachmentPath` (nullable) sur l'entité `Task` (et dans le schéma de la base).
- [ ] **CA2 :** Sur le formulaire de création de tâche, ajout d'un input de type `file` (`enctype="multipart/form-data"`, `MultipartFile attachment` dans l'objet de formulaire).
- [ ] **CA3 :** Le dossier de stockage est configuré via une propriété dans `application.properties` (ex : `app.storage.attachments-dir=uploads/attachments`) et le fichier y est enregistré avec un nom de fichier unique (UUID) pour éviter les collisions.
- [ ] **CA4 :** Le lien de téléchargement de la pièce jointe s'affiche sur la vue de la tâche, et les fichiers sont servis par l'application (`addResourceHandlers` dans le `WebMvcConfigurer`, ex : `/attachments/**`).

### US8 : Tests unitaires et fonctionnels (JUnit 5 + MockMvc)

**En tant que** développeur
**Je veux** m'assurer que mes fonctionnalités clés fonctionnent
**Afin de** prévenir les régressions.

- [ ] **CA1 :** Écriture d'un test pour vérifier qu'un utilisateur non connecté est redirigé vers la page de connexion s'il tente d'accéder aux projets.
- [ ] **CA2 :** Écriture d'un test pour vérifier la création réussie d'un projet dans la base de données.
- [ ] **CA3 :** Écriture d'un test pour vérifier que les règles de validation échouent si le `title` de la tâche est vide.

---

## 📦 Livrables attendus

1. **Dépôt Git (GitHub/GitLab) :** contenant l'intégralité du code source.
2. **Configuration de la base de données :** H2 en mode fichier dans `src/main/resources/application.properties`. *(Attention : ne jamais commiter de vrais mots de passe ou d'identifiants de production).*
3. **Schéma de la base de données :** fourni par le schéma JPA ou par des migrations versionnées (Flyway/Liquibase), mais dans tous les cas reproductible avec `./mvnw spring-boot:run`.
4. **Fichier `README.md` :** à la racine du projet, contenant :
    *   Les instructions pour lancer le projet (`./mvnw spring-boot:run`, `./mvnw test`, `./mvnw clean package`, etc.).
    *   Un petit paragraphe expliquant vos choix techniques (pourquoi vous avez utilisé tel pattern, quelle a été votre plus grande difficulté, ce que vous apporte Java 21 / Spring Boot 4 dans ce contexte, etc.).

## 🚫 Consignes strictes

*   **Aucun package frontend tiers (React, Vue, Angular, Tailwind, htmx, WebJars, etc.) n'est autorisé.** Nous voulons du **Thymeleaf / HTML / CSS basique**.
*   **Aucun package d'administration ou d'interface prête à l'emploi (Spring Boot Admin, Vaadin, etc.).** La console H2 intégrée reste bien sûr disponible (`http://localhost:8080/h2-console`).
*   Le code doit respecter les **conventions Java** (nommage, structure de packages, pas de code mort). Formatez votre code avec `google-java-format` (ou l'outil de votre IDE) — par exemple via `./mvnw spotless:apply` si vous ajoutez le plugin.
*   Les tests doivent être exécutables avec la commande `./mvnw test`.
*   Le workflow d'intégration continue fourni (`.github/workflows/maven-tests.yml`) exécute `./mvnw -B test` : il ne passera qu'une fois le projet généré à la racine du dépôt (avec le wrapper `mvnw`).

## 💡 Conseils pour réussir

*   Commencez par l'**US1** : le dépôt ne contient aucun code, c'est à vous de générer le projet.
*   Faites des **commits petits et fréquents** (ex : un commit par User Story). Nous regarderons l'historique Git !
*   Pensez à la réutilisation de votre code (DRY - Don't Repeat Yourself), notamment pour la vérification des droits et l'affichage des messages.
*   Ne bloquez pas 2 heures sur du CSS, l'important est le backend et la logique Spring Boot.
*   En cas de blocage sur une US, passez à la suivante et documentez votre blocage dans le README.

Bon courage ! 🚀
