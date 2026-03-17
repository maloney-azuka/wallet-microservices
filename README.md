# 💰 Wallet API — Microservices Architecture

A production-grade fintech wallet system built with Spring Boot microservices.

## 🏗️ Architecture

| Service | Port | Description |
|---|---|---|
| eureka-server | 8761 | Service Registry |
| gateway-service | 8080 | API Gateway |
| auth-service | 8084 | JWT Authentication |
| user-service | 8081 | User Management |
| wallet-service | 8082 | Wallet Operations |
| transaction-service | 8083 | Deposits, Withdrawals, Transfers |

## 🛠️ Tech Stack

- **Java 17** + **Spring Boot 3**
- **Spring Cloud Gateway** — API routing
- **Netflix Eureka** — Service discovery
- **OpenFeign** — Inter-service communication
- **Spring Security** + **JWT** — Authentication
- **PostgreSQL** — Database (one per service)
- **Docker** + **Docker Compose** — Containerization
- **Maven** — Build tool

## 🔄 Service Communication Flow
```
Client → Gateway (8080)
           ↓
      auth-service → user-service → wallet-service
                          ↓
                  transaction-service → wallet-service
```

## 📦 Microservices

### Auth Service
Handles user registration, login, JWT token generation and validation.

**Endpoints:**
- `POST /api/auth/register` — Register new user
- `POST /api/auth/login` — Login and get tokens
- `POST /api/auth/refresh` — Refresh access token
- `POST /api/auth/logout` — Logout and revoke token

### User Service
Manages user profiles. Called by auth-service via Feign on registration.

**Endpoints:**
- `POST /api/users` — Create user
- `GET /api/users/{id}` — Get user by ID
- `GET /api/users/email/{email}` — Get user by email
- `PUT /api/users/{id}` — Update user

### Wallet Service
Manages wallets and balances. Auto-created on user registration.

**Endpoints:**
- `POST /api/wallets` — Create wallet
- `GET /api/wallets/{userId}` — Get wallet
- `GET /api/wallets/balance/{userId}` — Get balance
- `PUT /api/wallets/credit` — Credit wallet
- `PUT /api/wallets/debit` — Debit wallet

### Transaction Service
Orchestrates all money movement via Feign calls to wallet-service.

**Endpoints:**
- `POST /api/transactions/deposit` — Deposit funds
- `POST /api/transactions/withdraw` — Withdraw funds
- `POST /api/transactions/transfer` — Transfer between wallets
- `GET /api/transactions/{id}` — Get transaction
- `GET /api/transactions/user/{userId}` — Get user transactions

## 🚀 Running Locally

### Prerequisites
- Java 17+
- Maven
- PostgreSQL
- Docker (optional)

### Setup Databases
```sql
CREATE DATABASE authdb;
CREATE DATABASE usersdb;
CREATE DATABASE walletdb;
CREATE DATABASE transactdb;
```

### Start Services (in order)
```bash
# 1. Eureka Server
cd eureka-server && mvn spring-boot:run

# 2. Gateway
cd gateway-service && mvn spring-boot:run

# 3. Auth Service
cd auth-service && mvn spring-boot:run

# 4. User Service
cd user-service && mvn spring-boot:run

# 5. Wallet Service
cd wallet-service && mvn spring-boot:run

# 6. Transaction Service
cd transaction-service && mvn spring-boot:run
```

### Or with Docker
bash
docker-compose up --build


## 🔐 Authentication

All endpoints except `/api/auth/**` require a Bearer token:
Authorization: Bearer <your_access_token>


## 📝 Sample Requests

### Register
json
POST /api/auth/register
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "phone": "08012345678"
}

### Transfer
```json
POST /api/transactions/transfer
{
  "senderId": 1,
  "receiverId": 2,
  "amount": 5000.00,
  "description": "Payment for services"
}


maloney— [GitHub](https://github.com/maloney-azuka)