# Marketiniya Vault

A secure proxy service that reads secrets from Google Cloud Secret Manager and exposes them via REST API to the main Marketiniya application.

## Architecture

This application follows SOLID, DRY, and KISS principles:

- **Single Responsibility**: Each class has one clear purpose
- **Open/Closed**: Components can be extended without modification
- **Liskov Substitution**: Implementations are fully substitutable
- **Interface Segregation**: Focused interfaces with minimal methods
- **Dependency Inversion**: Depends on abstractions, not concretions
- **DRY**: Common logic is reused across components
- **KISS**: Simple, clear implementations without unnecessary complexity

## Features

- Secure API key-based authentication
- Google Cloud Secret Manager integration
- RESTful API for secret retrieval
- **Firebase configuration management** - Specialized endpoints for Firebase credentials
- Support for multiple environments (WIP/PROD) and platforms
- Health check endpoints
- Comprehensive error handling
- Docker support

## API Endpoints

### Ultra-Simple Single Endpoint

#### Get Secrets - Simple secret retrieval

```
GET /api/vault/secrets
Headers: X-API-Key: your-api-key
```

**Get Latest Secret:**
```
GET /api/vault/secrets?name=my-secret
```

**Get Specific Secret Version:**
```
GET /api/vault/secrets?name=my-secret&version=2
```

**Parameters:**
- `name` (required): Secret name in Google Secret Manager
- `version` (optional): Specific version number (defaults to latest)

### Health Check
```
GET /actuator/health
```

## Configuration

### Environment Variables

Set the following environment variables:

```bash
GOOGLE_CLOUD_PROJECT_ID=your-project-id
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

### Docker
```bash
# Build image
docker build -t marketiniya-vault .

# Run container
docker run -p 8080:8080 \
  -e GOOGLE_CLOUD_PROJECT_ID=your-project-id \
  -e VAULT_API_KEY=your-api-key \
  marketiniya-vault
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
