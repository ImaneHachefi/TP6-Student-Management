# Student Management – Spring Boot, JPA, MySQL

Application REST de gestion d'étudiants (CRUD + statistiques), documentée avec Swagger et testée avec Postman.

## Étape 1 : Création du projet avec Spring Initializr

L'objectif de cette première étape est de générer automatiquement la structure de base du projet Spring Boot.

Accéder à https://start.spring.io et remplir les champs comme suit :

* Project : Maven Project
* Language : Java
* Spring Boot : dernière version stable proposée
* Group : `com.example`
* Artifact : `student-management`
* Packaging : Jar
* Java version : 17
* Dependencies :
  * Spring Web
  * Spring Data JPA
  * Spring Data REST
  * Spring Boot DevTools
  * Validation
  * MySQL Driver

> Conseil : ne pas ajouter trop de dépendances dès le début, cela facilite la compréhension du rôle de chacune.

Cliquer sur **Generate** pour télécharger le fichier ZIP, puis l'importer dans votre IDE (IntelliJ IDEA, Eclipse, NetBeans, etc.).

## Étape 2 : Analyse rapide du fichier pom.xml

Ce fichier décrit les dépendances et la configuration Maven du projet.

* **Parent Project** : hérite du parent Spring Boot pour bénéficier de versions cohérentes et stables.
* **Starters principaux** :
  * `spring-boot-starter-web` → contrôleurs REST
  * `spring-boot-starter-data-jpa` → persistance des entités
  * `spring-boot-starter-validation` → validation des entrées
  * `spring-boot-starter-test` → tests unitaires

> Astuce : vérifier la version de Java définie dans `<java.version>` pour qu'elle corresponde à celle installée sur votre machine.

## Étape 3 : Base de données MySQL avec Docker + DBeaver

### 3.1 Démarrer MySQL avec Docker Compose

À la racine du projet :

```bash
docker compose up -d
docker compose ps
```

Le conteneur `studentdb-mysql` crée automatiquement la base `studentdb`.

| Paramètre | Valeur |
|---|---|
| Hôte | `localhost` |
| Port | `3306` |
| Base | `studentdb` |
| Utilisateur | `root` |
| Mot de passe | `root` |

> Si le port 3306 est déjà utilisé par un MySQL local, changer `"3306:3306"` en `"3307:3306"` dans `docker-compose.yml` et mettre `3307` dans l'URL JDBC.

### 3.2 Connexion avec DBeaver

1. Ouvrir DBeaver → **Nouvelle connexion** → **MySQL**.
2. Renseigner : Host `localhost`, Port `3306`, Database `studentdb`, Username `root`, Password `root`.
3. Onglet **Driver properties** : mettre `allowPublicKeyRetrieval` à `true`.
4. Cliquer sur **Tester la connexion**, puis **Terminer**.

📸 **Capture : réussite de la connexion DBeaver**

<img width="446" height="438" alt="Capture d&#39;écran 2026-10-28 211926" src="https://github.com/user-attachments/assets/665c8ee2-8230-4045-bdbc-00a108c08a9b" />

### 3.3 Configuration du fichier `application.properties`

Le fichier se trouve dans `src/main/resources`.

```ini
spring.datasource.url=jdbc:mysql://localhost:3306/studentdb?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root

spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.hibernate.ddl-auto=update
```

* `spring.jpa.hibernate.ddl-auto=update` permet à Hibernate de créer ou mettre à jour automatiquement les tables selon les entités.
* En cas d'erreur de connexion, vérifier le mot de passe, le port, et que le conteneur est bien démarré.

📸 **Capture : application.properties**

<img width="1356" height="540" alt="image" src="https://github.com/user-attachments/assets/0af73b08-b39f-4e3b-b289-98993122d890" />

## Étape 4 : Entité et Repository

Package `com.example.demo.entities` : classe `Student` (`id`, `nom`, `prenom`, `dateNaissance`) annotée `@Entity`.
Package `com.example.demo.repositories` : interface `StudentRepository extends JpaRepository<Student, Integer>` avec une requête JPQL personnalisée (`findNbrStudentByYear`) qui compte les étudiants par année de naissance.

📸 **Capture : la table `student` dans DBeaver**

<img width="451" height="534" alt="Capture d&#39;écran 2026-10-28 205821" src="https://github.com/user-attachments/assets/18cb3374-6205-42f2-b6a6-e2a4c1b5d92c" />

## Étape 5 : Service et Contrôleur REST

* `StudentService` : logique métier (`save`, `delete`, `findAll`, `countStudents`, `findNbrStudentByYear`).
* `StudentController` (`@RequestMapping("students")`) : expose les endpoints REST.

📸 **Capture : structure du projet dans l'IDE**

<img width="572" height="910" alt="Capture d&#39;écran 2026-10-28 210213" src="https://github.com/user-attachments/assets/4d1b731d-ed79-41d5-bf66-5c273a9870c6" />

## Étape 6 : Lancer et tester l'application

Ordre à respecter :

```bash
docker compose up -d      # 1. base de données
mvn spring-boot:run       # 2. application
```

Le message suivant confirme un démarrage réussi :

```
Started StudentManagementApplication in X seconds (JVM running for X.X)
```

> En cas d'erreur (connexion MySQL, port 8080 déjà utilisé, dépendance manquante), consulter les logs.

📸 **Capture : logs de démarrage de l'application**

<img width="1918" height="785" alt="Capture d&#39;écran 2026-10-28 205746" src="https://github.com/user-attachments/assets/92467065-e11f-4704-a94f-2c65e0013691" />
<img width="1821" height="727" alt="Capture d&#39;écran 2026-10-28 205841" src="https://github.com/user-attachments/assets/45b1957b-6c7b-4852-91ee-f5621d062100" />

## Étape 7 : Tester les endpoints REST avec Postman

### 7.1 Préparation

1. Vérifier que MySQL (Docker) et l'application tournent (`http://localhost:8080`).
2. Ouvrir **Postman**.
3. *(Option rapide)* **Import** → sélectionner `postman/StudentManagement.postman_collection.json`. Les 5 requêtes ci-dessous sont alors déjà créées (variable `baseUrl` = `http://localhost:8080`).

Sinon, créer chaque requête manuellement comme indiqué ci-dessous.

### 7.2 Ajouter un étudiant (POST)

1. **New → HTTP Request**.
2. Méthode : **POST** ; URL : `http://localhost:8080/students/save`.
3. Onglet **Body** → cocher **raw** → choisir **JSON** dans la liste déroulante.
4. Coller :

```json
{
  "nom": "Hachefi",
  "prenom": "Imane",
  "dateNaissance": "1998-06-27"
}
```

5. Cliquer sur **Send**.

**Résultat attendu** : statut `201 Created` (visible en haut à droite de la réponse) et un corps JSON :

```json
{
  "id": 1,
  "nom": "Hachefi",
  "prenom": "Imane",
  "dateNaissance": "1998-06-27"
}
```

> Erreur 500 : vérifier les logs Spring Boot (connexion MySQL, structure de la base).
> Erreur 415 : vérifier que le Body est bien en **raw → JSON**.

📸 **Capture : POST /students/save**

<img width="1370" height="906" alt="Capture d&#39;écran 2026-10-28 210952" src="https://github.com/user-attachments/assets/9a3fd508-9f83-4bfb-88c4-0b3fecc87609" />

### 7.3 Lister les étudiants (GET)

1. Méthode : **GET** ; URL : `http://localhost:8080/students/all`.
2. Cliquer sur **Send**.
3. Dans la réponse, choisir l'affichage **Pretty → JSON**.

**Résultat attendu** : `200 OK`

```json
[
  {
    "id": 1,
    "nom": "Hachefi",
    "prenom": "Imane",
    "dateNaissance": "1998-06-27"
  }
]
```

📸 **Capture : GET /students/all**

<img width="1365" height="900" alt="Capture d&#39;écran 2026-10-28 211103" src="https://github.com/user-attachments/assets/fde8b880-e992-4379-874c-044c3839beca" />

### 7.4 Compter les étudiants (GET)

1. Méthode : **GET** ; URL : `http://localhost:8080/students/count`.
2. **Send**.

**Résultat attendu** : `200 OK`, corps : `1` (le nombre d'étudiants en base).

📸 **Capture : GET /students/count**

<img width="1363" height="903" alt="Capture d&#39;écran 2026-10-28 211206" src="https://github.com/user-attachments/assets/0105c50c-0b9b-4a43-9e7f-3129e596b5b4" />

### 7.5 Nombre d'étudiants par année de naissance (GET)

Ajouter d'abord 2 ou 3 étudiants avec des années différentes (via le POST), puis :

1. Méthode : **GET** ; URL : `http://localhost:8080/students/byYear`.
2. **Send**.

**Résultat attendu** : `200 OK`

```json
[
  [1998, 1]
]
```

Interprétation : 2 étudiants nés en 1985, 1 étudiant né en 1990.

📸 **Capture : GET /students/byYear**

<img width="1370" height="902" alt="Capture d&#39;écran 2026-10-28 211304" src="https://github.com/user-attachments/assets/02809541-80fd-4bfd-af36-a0a403ba20e3" />

### 7.6 Supprimer un étudiant (DELETE)

1. Méthode : **DELETE** ; URL : `http://localhost:8080/students/delete/1`.
2. **Send**.

**Résultat attendu** :
* `204 No Content` si la suppression réussit (aucun corps).
* `404 Not Found` si l'étudiant n'existe pas (renvoyer la même requête pour le vérifier).

### 7.7 Vérification dans DBeaver

Dans DBeaver, ouvrir `studentdb` → `student` → onglet **Données** (F5 pour rafraîchir) : les lignes doivent correspondre aux appels effectués.

📸 **Capture : table `student` après les tests**

<img width="1070" height="688" alt="Capture d&#39;écran 2026-10-28 211412" src="https://github.com/user-attachments/assets/9e23bbba-d25f-432a-842f-e9337c6b4ca7" />

### 7.8 Résumé des codes de statut attendus

| Méthode | Endpoint | Code attendu | Description |
|---|---|---|---|
| POST | `/students/save` | 201 Created | Étudiant ajouté avec succès |
| GET | `/students/all` | 200 OK | Liste complète des étudiants |
| DELETE | `/students/delete/{id}` | 204 No Content | Étudiant supprimé |
| DELETE | `/students/delete/{id}` | 404 Not Found | Étudiant inexistant |
| GET | `/students/count` | 200 OK | Nombre total d'étudiants |
| GET | `/students/byYear` | 200 OK | Répartition par année de naissance |

## Étape 8 : Tests unitaires avec JUnit 5 et Mockito

```bash
mvn test
```

Les 5 tests de `StudentControllerTest` (`save`, `delete`, `findAll`, `count`, `findByYear`) utilisent Mockito pour simuler `StudentService`, sans base de données.

📸 **Capture : résultats des tests (tous verts)**

<img width="913" height="670" alt="Capture d&#39;écran 2026-10-28 211636" src="https://github.com/user-attachments/assets/1b858a02-9cb4-4946-8e4b-ab9d65a147a7" />

## Étape 9 : Intégration de Swagger

La dépendance `springdoc-openapi-starter-webmvc-ui` est déjà dans le `pom.xml`. Chaque endpoint est documenté avec `@Operation`.

Application démarrée, ouvrir :

```
http://localhost:8080/swagger-ui.html
```

Swagger permet de visualiser et tester les endpoints directement depuis le navigateur (alternative à Postman).

📸 **Capture : interface Swagger**

<img width="1802" height="1025" alt="Capture d&#39;écran 2026-10-28 211735" src="https://github.com/user-attachments/assets/3c9ce4fe-b12d-4cd1-9ed9-5d0fcce91d06" />

## Arrêter l'environnement

```bash
docker compose down        # arrête MySQL (données conservées)
docker compose down -v     # arrête et supprime aussi les données
```
