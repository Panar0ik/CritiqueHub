# CritiqueHub - Community Spaces API

[![Quality Gate Status]([https://sonarcloud.io/api/project_badges/measure?project=YOUR_PROJECT_KEY&metric=alert_status](https://sonarcloud.io/project/overview?id=Panar0ik_CritiqueHub))]([https://sonarcloud.io/summary/new_code?id=YOUR_PROJECT_KEY](https://sonarcloud.io/summary/new_code?id=Panar0ik_CritiqueHub&branch=main))

CritiqueHub is a Spring Boot-based RESTful service designed for managing interest-based communities (Spaces) and facilitating communication through integrated messaging.

## 🚀 Features

- **Space Management**: Create and browse community spaces.
- **Messaging System**: Post messages within specific spaces.
- **Advanced Filtering**: Search for communities by category (e.g., Movies, Tech, Art).
- **In-Memory Storage**: Fast execution using Java collections for data persistence.
- **Code Quality**: Strict adherence to Sun/Google Java Style via Checkstyle.

## 🛠 Tech Stack

* **Java 17**
* **Spring Boot 3.x** (Web, DevTools)
* **Maven** (Dependency Management)
* **Checkstyle** (Static Code Analysis)
* **SonarCloud** (Continuous Inspection)

## 🏗 Architecture

The project follows a **layered architecture** to ensure separation of concerns:
1.  **Controller Layer**: Handles HTTP requests and maps them to DTOs.
2.  **Service Layer**: Encapsulates business logic and validations.
3.  **Repository Layer**: Manages data access (currently In-Memory).
4.  **DTO & Mappers**: Decouples internal entities from the external API representation.

## 📡 API Endpoints

### Spaces
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/community/spaces` | Get all spaces |
| `GET` | `/api/community/spaces/{id}` | Get space by ID |
| `GET` | `/api/community/search?category={c}` | Filter spaces by category |
| `POST` | `/api/community/spaces` | Create a new space |

### Messages
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/community/spaces/{id}/messages` | Post a message to a space |

## 🔧 Installation & Setup

1. **Clone the repository** (via SSH):
   ```bash
   git clone git@github.com:Panar0ik/CritiqueHub.git
