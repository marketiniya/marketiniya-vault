# Secret Retrieval Usage Examples

This document shows how to use the ultra-simple Marketiniya Vault API to retrieve secrets.

## Simple API Examples

### Get Firebase API Key

```bash
curl -X GET "http://localhost:8080/api/vault/secrets?name=firebase-wip-api-key" \
  -H "X-API-Key: your-api-key"
```

**Response:**
```json
{
  "environment": "wip",
  "platform": "web",
  "status": "SUCCESS",
  "config": {
    "WIP_API_KEY": "your-firebase-api-key",
    "WIP_APP_ID": "your-firebase-app-id",
    "WIP_MESSAGING_SENDER_ID": "123456789",
    "WIP_PROJECT_ID": "your-project-id",
    "WIP_AUTH_DOMAIN": "your-project.firebaseapp.com",
    "WIP_STORAGE_BUCKET": "your-project.appspot.com",
    "WIP_MEASUREMENT_ID": "G-XXXXXXXXXX"
  }
}
```

### Get Firebase Config for PROD Android Environment

```bash
curl -X GET "http://localhost:8080/api/vault/get-secret-from-vault?type=firebase&env=prod&platform=android" \
  -H "X-API-Key: your-api-key"
```

**Response:**
```json
{
  "environment": "prod",
  "platform": "android",
  "status": "SUCCESS",
  "config": {
    "PROD_ANDROID_API_KEY": "your-android-api-key",
    "PROD_ANDROID_APP_ID": "your-android-app-id",
    "PROD_MESSAGING_SENDER_ID": "123456789",
    "PROD_PROJECT_ID": "marketinya-a4876",
    "PROD_STORAGE_BUCKET": "marketinya-a4876.appspot.com"
  }
}
```

### Get Individual Secrets

```bash
curl -X GET "http://localhost:8080/api/vault/get-secret-from-vault?type=secret&name=my-api-key" \
  -H "X-API-Key: your-api-key"
```

### Ultra-Simple Flutter Integration

```dart
// Super simple Flutter service
class MarketiniyaVaultService {
  static const String baseUrl = 'https://your-vault-url.com';
  static const String apiKey = 'your-api-key';

  // Get Firebase config in one call
  static Future<Map<String, String>> getFirebaseConfig(String env, String platform) async {
    final url = '$baseUrl/api/vault/get-secret-from-vault?type=firebase&env=$env&platform=$platform';
    final response = await http.get(Uri.parse(url), headers: {'X-API-Key': apiKey});

    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      return Map<String, String>.from(data['config']);
    }
    throw Exception('Failed to load Firebase config');
  }

  // Get any secret
  static Future<String> getSecret(String name) async {
    final url = '$baseUrl/api/vault/get-secret-from-vault?type=secret&name=$name';
    final response = await http.get(Uri.parse(url), headers: {'X-API-Key': apiKey});

    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      return data['value'];
    }
    throw Exception('Failed to load secret');
  }
}
```

## Integration with Flutter

### Using the Configuration in Your Flutter App

```dart
// Example service to fetch Firebase config from vault
class FirebaseConfigService {
  static const String vaultBaseUrl = 'https://your-vault-url.com';
  static const String apiKey = 'your-api-key';
  
  static Future<Map<String, String>> getFirebaseConfig(
    String environment,
    String platform
  ) async {
    final response = await http.get(
      Uri.parse('$vaultBaseUrl/api/vault/MARKETINIYA_VAULT_GET_SECRET?type=firebase&env=$environment&platform=$platform'),
      headers: {'X-API-Key': apiKey},
    );
    
    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      return Map<String, String>.from(data['config']);
    }
    
    throw Exception('Failed to load Firebase config');
  }
}

// Usage in your Firebase initialization
Future<void> initializeFirebase() async {
  final platform = _getCurrentPlatform();
  final environment = _getCurrentEnvironment();
  
  final config = await FirebaseConfigService.getFirebaseConfig(
    environment, 
    platform
  );
  
  final firebaseOptions = FirebaseOptions(
    apiKey: config['${environment.toUpperCase()}_API_KEY']!,
    appId: config['${environment.toUpperCase()}_APP_ID']!,
    messagingSenderId: config['${environment.toUpperCase()}_MESSAGING_SENDER_ID']!,
    projectId: config['${environment.toUpperCase()}_PROJECT_ID']!,
    authDomain: config['${environment.toUpperCase()}_AUTH_DOMAIN'],
    storageBucket: config['${environment.toUpperCase()}_STORAGE_BUCKET'],
  );
  
  await Firebase.initializeApp(options: firebaseOptions);
}
```

## Error Handling

### Partial Configuration Response (206 Partial Content)

When some secrets are missing, you'll receive a 206 status:

```json
{
  "environment": "prod",
  "platform": "android",
  "status": "PARTIAL",
  "config": {
    "PROD_ANDROID_API_KEY": "available-key",
    "PROD_PROJECT_ID": "available-project-id"
    // Missing: PROD_ANDROID_APP_ID, PROD_MESSAGING_SENDER_ID, etc.
  }
}
```

### Not Found Response (404)

When environment or platform doesn't exist:

```json
{
  "environment": "invalid",
  "platform": "web",
  "status": "NOT_FOUND",
  "config": null
}
```
