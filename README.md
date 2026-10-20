# TaskFlow — Gestionnaire de tâches (Spring Boot)

Application web de gestion de projets et de tâches : inscription et connexion, CRUD des projets, tâches avec statut, échéance et pièce jointe, interface Thymeleaf.

## Prérequis

- Java 21
- Aucun Maven à installer : le wrapper `./mvnw` est versionné (sous Windows : `mvnw.cmd`)

## Lancer le projet

```bash
./mvnw spring-boot:run        # démarre l'application sur http://localhost:8080
./mvnw test                   # lance les tests (JUnit 5 + MockMvc)
./mvnw clean package          # construit le jar dans target/
```

- Application : http://localhost:8080 (redirige vers `/login` si l'on n'est pas connecté)
- Console H2 : http://localhost:8080/h2-console
  - JDBC URL : `jdbc:h2:file:./data/taskflow`
  - Utilisateur : `sa`, mot de passe vide
- Base de données : H2 en mode fichier (dossier `./data`). Le schéma est créé et mis à jour automatiquement par JPA (`spring.jpa.hibernate.ddl-auto=update`), il est donc reproductible avec `./mvnw spring-boot:run`.
- Pièces jointes : stockées dans `uploads/attachments` (propriété `app.storage.attachments-dir`).
- Une seule instance de l'application peut tourner à la fois : H2 en mode fichier verrouille la base.

## Fonctionnalités par user story

| US | Contenu |
|----|---------|
| US1 | Inscription, connexion et déconnexion avec Spring Security. Mots de passe hachés en BCrypt. Les pages protégées redirigent vers `/login`. |
| US2 | CRUD des projets. Chaque utilisateur ne voit, ne modifie et ne supprime que ses propres projets. |
| US3 | Tâches avec statut (`PENDING`, `IN_PROGRESS`, `DONE`) et échéance. Formulaire validé avec `TaskForm`, `@Valid` et `BindingResult`. |
| US4 | Layout Thymeleaf réutilisable (`fragments/layout.html`), fragment d'alertes (`fragments/alerts.html`), page de détail d'un projet avec tableau des tâches et changement rapide de statut. |
| US5 | `ProjectOwnershipInterceptor` : réponse 403 si le projet du chemin n'appartient pas à l'utilisateur connecté. |
| US6 | Requête dérivée des tâches en retard (`findOverdue`), `getStatusLabel()` en français, `@EntityGraph` contre les requêtes N+1 (`open-in-view` désactivé). |
| US7 | Pièce jointe sur une tâche, nom de fichier UUID, fichiers servis par `/attachments/**`. |
| US8 | Tests : redirection vers la connexion, création d'un projet en base, validation du titre vide. |

## Routes principales

| Route | Rôle |
|-------|------|
| `GET /login`, `POST /login` | Connexion |
| `GET /register`, `POST /register` | Inscription |
| `POST /logout` | Déconnexion |
| `GET /dashboard` | Tableau de bord |
| `GET /projects`, `GET /projects/new`, `POST /projects` | Liste et création de projets |
| `GET /projects/{projectId}` | Détail d'un projet et tableau des tâches |
| `GET /projects/{projectId}/edit`, `POST /projects/{projectId}` | Modification d'un projet |
| `POST /projects/{projectId}/delete` | Suppression d'un projet |
| `GET /projects/{projectId}/tasks/new`, `POST /projects/{projectId}/tasks` | Création d'une tâche |
| `GET /projects/{projectId}/tasks/{id}/edit`, `POST /projects/{projectId}/tasks/{id}` | Modification d'une tâche |
| `POST /projects/{projectId}/tasks/{id}/status` | Changement rapide de statut |
| `GET /tasks/overdue` | Tâches en retard de l'utilisateur |
| `GET /attachments/**` | Téléchargement des pièces jointes |

## Structure du code

```
config/      SecurityConfig, WebMvcConfig, ProjectOwnershipInterceptor
controller/  AuthController, DashboardController (ou HomeController), ProjectController,
             TaskController, OverdueTaskController, GlobalModelAttributes
services/    UserService, CustomUserDetailsService, ProjectService, TaskService, StorageService
repository/  UserRepository, ProjectRepository, TaskRepository
entity/      User, Project, Task, TaskStatus
dto/         RegisterForm, ProjectForm, TaskForm
templates/   login, register, dashboard, projects/*, tasks/*, fragments/*, error/403
```

## Choix techniques

**Architecture en couches (contrôleur, service, repository).** Le contrôleur ne contient aucune logique métier : il appelle le service et choisit la vue. Le service porte les règles (hachage du mot de passe, appartenance d'un projet, stockage des fichiers). Le repository est une interface Spring Data dont les requêtes sont dérivées du nom des méthodes, ce qui évite d'écrire du SQL à la main.

**Objets de formulaire séparés des entités** (`RegisterForm`, `ProjectForm`, `TaskForm`). Les règles de validation portent sur la saisie (par exemple un mot de passe en clair de 8 caractères minimum), pas sur la valeur stockée (le hash BCrypt). De plus, un utilisateur ne peut pas modifier d'autres champs en ajoutant des paramètres à la requête.

**Autorisation à deux niveaux.** L'intercepteur répond 403 avant l'exécution du contrôleur quand le `projectId` du chemin n'appartient pas à l'utilisateur connecté. Les services filtrent en plus toutes leurs requêtes par utilisateur (jamais de `findById` seul). Une route ajoutée plus tard sans passer par l'intercepteur reste donc protégée.

**Performances.** `spring.jpa.open-in-view=false` : les relations sont chargées explicitement. `@EntityGraph(attributePaths = "tasks")` charge les projets avec leurs tâches en une seule requête, ce qui évite le problème N+1 sur la liste des projets.

**Pièces jointes.** Le nom stocké est généré (`UUID.extension`) pour éviter les collisions, et les extensions sont limitées à une liste blanche, car les fichiers sont servis depuis l'origine de l'application. Le nom d'origine n'est jamais utilisé dans le chemin, ce qui évite les attaques par traversée de répertoire.

**Tests.** `MockMvc` traverse les vrais filtres Spring Security, sans ouvrir de port. Les tests utilisent une base H2 en mémoire (`src/test/resources/application.properties`) et ne touchent donc pas à `./data`. Chaque test est transactionnel et annulé à la fin.

**Java 21 et Spring Boot 4.** Java 21 (LTS) apporte les `switch` en expressions (utilisés dans `Task.getStatusLabel()`) et une syntaxe plus concise. Spring Boot 4 apporte Spring Framework 7, Spring Security 7 et Hibernate 7. La sécurité se configure avec un bean `SecurityFilterChain`, sans `WebSecurityConfigurerAdapter`.

## Difficultés rencontrées

Ma plus grande difficulté a été la mise en place du projet. Les noms de packages ne correspondaient pas à l'arborescence des dossiers sources (`src/main/java` ne fait pas partie du nom du package), ce qui empêchait Spring de détecter les composants et la compilation de réussir. J'ai aussi perdu du temps sur des erreurs de Spring Data liées aux noms de propriétés (`No property 'userId' found for type 'project'`), causées par une entité restée sous une ancienne forme, et sur le verrou H2 sur le fichier de base quand une ancienne instance de l'application tournait encore. J'en ai retenu qu'il faut toujours vérifier que les fichiers sont bien enregistrés et lancer `./mvnw clean` après un changement de structure.

## Limites connues

- Un fichier de plus de 5 Mo est refusé par Spring avec la page d'erreur générique, sans message dans le formulaire.
- Les pièces jointes sont accessibles à tout utilisateur connecté qui connaît le nom UUID du fichier.
- Il n'y a pas de suppression de tâche, car elle n'est pas demandée dans le sujet.
