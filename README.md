# Marketiniya Vault

Secure proxy service for Firebase secrets from Google Cloud Secret Manager with caching.

## Features

- 🔐 API key authentication
- ☁️ Google Secret Manager integration
- 🚀 In-memory caching (100x faster)
- 🔥 All Firebase secrets in one call

## Architecture

```mermaid
graph LR
    A[Flutter App] -->|X-API-Key| B[Vault API]
    B --> C{Cache?}
    C -->|Hit| D[Return Cached]
    C -->|Miss| E[Google Secret Manager]
    E --> F[Cache & Return]

    subgraph "Vault Service"
        B
        C
        G[Spring Security]
        H[Cache Manager]
    end

    subgraph "Google Cloud"
        E
    end
```

## Project Structure

```
marketiniya-vault/
├── src/
│   ├── main/
│   │   ├── java/com/marketiniya/vault/
│   │   │   ├── MarketiniyaVaultApplication.java
│   │   │   ├── config/
│   │   │   │   ├── CacheConfig.java
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   └── SecretManagerConfig.java
│   │   │   ├── constants/
│   │   │   │   └── SecretNames.java
│   │   │   ├── controller/
│   │   │   │   └── VaultController.java
│   │   │   ├── model/
│   │   │   │   └── SecretsResponse.java
│   │   │   └── service/
│   │   │       ├── SecretService.java
│   │   │       └── impl/
│   │   │           └── GoogleSecretManagerService.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/com/marketiniya/vault/
│           └── controller/
│               └── VaultControllerTest.java
├── build.gradle
├── settings.gradle
├── gradlew
└── README.md
```

## API Endpoints

### Get All Firebase Secrets (Cached)

Retrieve all Firebase configuration secrets in a single API call with intelligent caching.

```
GET /api/vault/secrets
Headers: X-API-Key: your-api-key
```

**Response:**
```json
{
  "MARKETINYA_PROD_WEB_FIREBASE_API_KEY": "your-prod-api-key",
  "MARKETINYA_PROD_WEB_FIREBASE_APP_ID": "your-prod-app-id",
  "MARKETINYA_PROD_WEB_FIREBASE_AUTH_DOMAIN": "your-prod-auth-domain",
  "MARKETINYA_PROD_WEB_FIREBASE_MESSAGING_SENDER_ID": "your-prod-sender-id",
  "MARKETINYA_PROD_WEB_FIREBASE_PROJECT_ID": "your-prod-project-id",
  "MARKETINYA_PROD_WEB_FIREBASE_STORAGE_BUCKET": "your-prod-storage-bucket",
  "MARKETINYA_WIP_WEB_FIREBASE_API_KEY": "your-wip-api-key",
  "MARKETINYA_WIP_WEB_FIREBASE_APP_ID": "your-wip-app-id",
  "MARKETINYA_WIP_WEB_FIREBASE_AUTH_DOMAIN": "your-wip-auth-domain",
  "MARKETINYA_WIP_WEB_FIREBASE_MEASUREMENT_ID": "your-wip-measurement-id",
  "MARKETINYA_WIP_WEB_FIREBASE_MESSAGING_SENDER_ID": "your-wip-sender-id",
  "MARKETINYA_WIP_WEB_FIREBASE_PROJECT_ID": "your-wip-project-id",
  "MARKETINYA_WIP_WEB_FIREBASE_STORAGE_BUCKET": "your-wip-storage-bucket"
}
```

### Cache Management

Clear the in-memory cache to force fresh retrieval from Google Secret Manager.

```
POST /api/vault/cache/clear
Headers: X-API-Key: your-api-key
```

**Response:**
```
Cache cleared successfully
```

### Health Check

Public endpoint for monitoring application health.

```
GET /actuator/health
```

**Response:**
```json
{"status":"UP"}
```

## Performance

### Intelligent Caching

The application implements smart in-memory caching for optimal performance:

- **First request**: ~500ms (retrieves from Google Secret Manager)
- **Cached requests**: ~5ms (retrieves from memory)
- **Performance gain**: 100x faster for subsequent requests!

### Cache Behavior

- **Automatic caching**: All secrets are cached after first retrieval
- **Manual cache clearing**: Use `POST /api/vault/cache/clear` to force refresh
- **Memory efficient**: Uses Spring's ConcurrentMapCacheManager
- **Thread-safe**: Handles concurrent requests safely

## Configuration

### Environment Variables

**⚠️ IMPORTANT: Set these environment variables before running the application:**

```bash

export GOOGLE_CLOUD_PROJECT_ID=your-google-cloud-project-id
export VAULT_API_KEY=your-secure-api-key
```

**Or create a `.env` file (not committed to git):**
```bash

GOOGLE_CLOUD_PROJECT_ID=your-google-cloud-project-id
VAULT_API_KEY=your-secure-api-key
```

### Firebase Secret Naming Convention

Store Firebase credentials in Google Secret Manager using these naming patterns:

**WIP Environment:**
- `firebase-wip-api-key`
- `firebase-wip-app-id`
- `firebase-wip-messaging-sender-id`
- `firebase-wip-project-id`
- `firebase-wip-auth-domain`
- `firebase-wip-storage-bucket`
- `firebase-wip-measurement-id`

**PROD Environment:**
- Web: `firebase-prod-api-key`, `firebase-prod-app-id`, etc.
- Android: `firebase-prod-android-api-key`, `firebase-prod-android-app-id`, etc.
- iOS/macOS: `firebase-prod-ios-api-key`, `firebase-prod-ios-app-id`, `firebase-prod-ios-bundle-id`, etc.
- Windows: `firebase-prod-windows-app-id`, etc.

## Running the Application

### Prerequisites
- Java 21
- Maven 3.6+
- Google Cloud credentials configured

### Local Development
```bash

# Set up Google Cloud authentication
gcloud auth application-default login

# Run the application
./gradlew bootRun
```

## Testing

```bash

# Run all tests
./gradlew test

# Build the project
./gradlew build
```

## Security

- API key authentication via `X-API-Key` header
- No secret values in logs
- Secure error responses that don't leak information
- Non-root Docker user

## Monitoring

- Health check endpoint at `/actuator/health`
- Structured logging
- Error tracking and metrics ready
