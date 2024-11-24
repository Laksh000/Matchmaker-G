# Matchmaking System

A **Spring Boot**-based matchmaking service designed to group players for multiplayer games based on their attributes. The system supports secure, JWT-based authentication for game administrators and provides robust APIs for player management and matchmaking.

---

## Features

- **Player Management:** Registration, retrieval, and updates.
- **Flexible Matchmaking:**
    - Match players from a pool of active players.
    - Match players from a custom list of IDs.
- **Secure Access:** JWT-based authentication for game admins.
- **Database:** MongoDB integration for data storage.
- **Testing:** Comprehensive unit and integration tests with **Testcontainers**.

---

## Technologies
### Backend

* Java (JDK 22)
* Spring Boot (MVC, Data, Security, JWT)
* MongoDB (with Testcontainers for testing)

### Testing

* JUnit 5
* Mockito
* MockMvc
* Testcontainers

### Tools

* Maven
* Docker

---

## Project Structure

The project follows a modular structure for better maintainability:

- **controller/**: Handles API endpoints.
- **service/**: Implements business logic.
- **repo/**: Handles database interactions.
- **model/**: Defines data models.
- **dto/**: Structures for transferring data between layers.
- **constant/**: Stores constants used across the application.
- **exception/**: Custom exception handling.

---

## Setup and Running

### Prerequisites

* Java 22
* Maven
* MongoDB (Production instance of MongoDB 5.0.1 or Testcontainers for testing)
* Docker

Clone the Repository
```command
git clone https://github.com/Laksh000/matchmaking.git  
cd matchmaking
```  
Build the Project
```command
mvn clean install
```
Run the Application
```command
mvn spring-boot:run 
``` 
Run Tests

Unit tests:
```command
mvn test
```
Integration tests:
```command
mvn verify
```
## Authentication

Game admins must authenticate via JWT tokens to access all endpoints.

### Authentication Endpoints

#### POST `/auth/register`
Registers a new game admin.

**Request**:
```json
{
  "username": "admin",
  "password": "password123"
}
```
**Response:**

201 Created:

```
 "User registration successful!"
```
400 Bad Request:
```
"User registration failed: as user with admin username already exists"
```
#### POST `/auth/login`
Logs in a registered game admin.

**Request**:
```json
{
  "username": "admin",
  "password": "password123"
}
```
**Response:**

200 OK:

```
 eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```
403 Forbidden:
```
"Invalid username or password"
```
### Usage of JWT Token

To access the protected endpoints, include the JWT token in the `Authorization` header of your HTTP requests.

#### Format:

Replace `<your-jwt-token>` with the actual token received from the `/auth/login` endpoint after successful authentication.

#### Example:
**Request**:
```http
GET /players/all HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## API Endpoints
### Player Management
#### GET `/players/all`
Fetch all registered players.

Response:

200 OK:
```json
[
    {
        "id": "abadfbahgfg",
        "name": "Player1",
        "attributes": {
        "strength": 85,
        "speed": 92,
        "isVIP": true,
        "experiencePoints": 2500,
        "specialAbility": "Invisibility"
        }
        },
        {
        "id": "akebgfieFas",
        "name": "Player2",
        "attributes": {
        "strength": 78,
        "speed": 72,
        "isVIP": false,
        "experiencePoints": 1800,
        "specialAbility": "Fire"
        }
    }
]
```
204 No Content

#### GET `/players/{name}`

Fetch a specific player by name.

Response:

200 OK:
```json
{
  "id": "akebgfieFas",
  "name": "Player2",
  "attributes": {
    "strength": 78,
    "speed": 72,
    "isVIP": false,
    "experiencePoints": 1800,
    "specialAbility": "Fire"
  }
}
```
204 No Content

#### POST `/players/register`
Register a list of new players.

Request:

```json
[
  {
    "name": "Player1",
    "attributes": {
      "strength": 85,
      "speed": 92,
      "isVIP": true,
      "experiencePoints": 2500,
      "specialAbility": "Invisibility"
    }
  },

  {
    "name": "Player2",
    "attributes": {
      "strength": 78,
      "speed": 72,
      "isVIP": false,
      "experiencePoints": 1800,
      "specialAbility": "Fire"
    }
  }
]
```
Response:

200 OK:
```
"Players registered successfully."
```
400 Bad Request:
```
"Player registration failed for the following players: Player1, Player2, as players with the same name already exists"
```

#### PUT   `/players/update`
Update player information.

Request:
```json
[ 
    {
        "id": "abadfbahgfg",
        "attributes": {
        "strength": 90,
        "speed": 98,
        "isVIP": true,
        "experiencePoints": 3200,
        "specialAbility": "Invisibility"
        }
    },
    {
        "id": "akebgfieFas",
        "name": "Player2Updated"
    } 
]
```
Response:

200 OK:
```
"Players updated successfully."
```

400 Bad Request:
```
 "Update failed for the following players as they were not found:  abadfbahgfg, akebgfieFas ."
```
or (if id's are not sent)

```
"Update failed for 2 players, as player id's were not found in the request"
```

## Matchmaking
#### POST `/match/pool`
Generate match groups from a pool of active players.

Request:

```json
{
  "matchTypeFair": true,
  "groupSize": 2,
  "targetAttributes": {
    "strength": 80,
    "speed": 85,
    "isVIP": true,
    "experiencePoints": 2000
  },
  "attributeWeights": {
    "strength": 0.4,
    "speed": 0.3,
    "isVIP": 0.2,
    "experiencePoints": 0.1
  }
}
```
Response:

200 OK:

```json
{
"groups": [
                [
                    { "id": "iauhgfbgf", "name": "Player1" },
                    { "id": "niauhffa", "name": "Player4" }
                ],
                [
                    { "id": "aienuhf", "name": "Player3" },
                    { "id": "knacfg", "name": "Player5" }
                   
                ],
                [
                  { "id": "niuahfga", "name": "Player6" },
                  { "id": "buagefug", "name": "Player2" }
                ]
          ],
  "message": "Matchmaking was Successful"
}
```
400 Bad Request:

```json
{
    "groups": [],
    "message": "The number of active players doesn't match the criteria for matchmaking"
}
```

#### POST `/match/custom`
Generate match groups from a custom list of player IDs.

Request:
```json
{
  "playerIds": ["iauhgfbgf", "niauhffa", "aienuhf", "knacfg"],
  "matchTypeFair": false,
  "groupSize": 2,
  "targetAttributes": {
    "strength": 80,
    "speed": 85,
    "isVIP": true,
    "experiencePoints": 2000
  },
  "attributeWeights": {
    "strength": 0.4,
    "speed": 0.3,
    "isVIP": 0.2,
    "experiencePoints": 0.1
  }
}
```

Response:

200 OK:

```json
{
  "groups": [
                [
                    { "id": "iauhgfbgf", "name": "Player1" },
                    { "id": "niauhffa", "name": "Player3" }
                ],
                [
                    { "id": "aienuhf", "name": "Player4" },
                    { "id": "knacfg", "name": "Player5" }
                ]
            ],
  "message": "Matchmaking was Successful"
}
```
400 Bad Request:

if player id's are not sent
```json
{
    "groups": [],
    "message":  "Player Id's are mandatory"
}
```
or (if player id's doesn't exist)
```json
{
    "groups": [],
    "message":  "Player with ID iauhgfbgf not found"
}
```

### Note:
The **MatchRequest** object includes:

* playerIds (optional only required for custom matchmaking)
* targetAttributes (criteria for matchmaking)
* attributeWeights (weights for each attribute)
* groupSize (number of players per match group)
* isMatchTypeFair (to indicate whether matchmaking should be balanced based on scores).

The **MatchResponse** object contains:
* groups (list of player groups)
* message (status or error details).

## Future Enhancements

* Add caching for frequently accessed player data.
* Integration with external APIs for real-time player stats.
* Expand attribute filtering logic to support complex scoring algorithms.
