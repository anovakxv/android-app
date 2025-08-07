# Rep Android App

A social networking Android app for "Reps" (representatives) built with Kotlin and Jetpack Compose, integrated with a Python Flask backend.

## Features

- **User Authentication**: JWT-based login and registration
- **User Profiles**: Profile management with skills and locations
- **Portals**: Group collaboration spaces with image galleries and stories
- **Goals**: Tracking with progress visualization and team management
- **Real-time Messaging**: Chat with other users
- **Content Sharing**: Portal creation with image uploads and story blocks

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: MVVM with Repository pattern
- **Dependency Injection**: Dagger Hilt
- **Database**: Room (local storage)
- **Networking**: Retrofit with Moshi (JSON parsing)
- **Image Loading**: Coil
- **Navigation**: Navigation Compose
- **Async Operations**: Kotlin Coroutines with Flow

## Project Structure

```
app/src/main/java/com/networkedcapital/rep/
├── RepApplication.kt         # Application class with Hilt setup
├── MainActivity.kt           # Main entry point
├── data/
│   ├── api/                 # API interfaces and configuration
│   │   ├── ApiConfig.kt     # API endpoints and base URL
│   │   ├── AuthApiService.kt     # Authentication API
│   │   ├── PortalApiService.kt   # Portal management API
│   │   ├── GoalApiService.kt     # Goals tracking API
│   │   ├── MessagingApiService.kt # Real-time messaging API
│   │   ├── NetworkModule.kt      # Retrofit/Hilt setup
│   │   └── AuthInterceptor.kt    # JWT token management
│   └── repository/          # Repository implementations
│       ├── AuthRepository.kt     # Authentication data layer
│       └── PortalRepository.kt   # Portal data layer
├── domain/
│   └── model/              # Domain models and entities
│       └── Models.kt       # User, Portal, Goal, Message models
└── presentation/           # UI layer (ViewModels and Composables)
    ├── auth/              # Authentication screens
    │   ├── AuthViewModel.kt
    │   ├── LoginScreen.kt
    │   ├── RegisterScreen.kt
    │   └── OnboardingScreen.kt
    ├── main/
    │   └── MainScreen.kt   # Main app screen with tabs
    ├── profile/
    │   └── ProfileScreen.kt # User profile display
    ├── navigation/
    │   └── RepNavigation.kt # Navigation setup
    └── theme/             # Material 3 theming
        ├── Color.kt       # Rep brand colors
        ├── Type.kt        # Typography
        └── Theme.kt       # Theme configuration
```

## Getting Started

### Prerequisites

1. **Android Studio** (Arctic Fox or later)
2. **JDK 11** or later
3. **Python Flask Backend** running (see Backend Configuration)

### Setup Instructions

1. **Clone and open the project in Android Studio**

2. **Sync Gradle dependencies**

3. **Configure Flask Backend URL**

   Update the base URL in `app/src/main/java/com/networkedcapital/rep/data/api/ApiConfig.kt`:

   ```kotlin
   // For Android Emulator accessing host machine
   const val BASE_URL = "http://10.0.2.2:5000/"
   
   // For physical device on same network (replace with your IP)
   // const val BASE_URL = "http://192.168.1.xxx:5000/"
   
   // For production
   // const val BASE_URL = "https://your-production-domain.com/"
   ```

4. **Build and run the app**

### Backend Configuration

The Android app is designed to work with your existing Python Flask backend. Here's how to configure the connection:

#### 1. Flask Backend Setup

Make sure your Flask backend is running and accessible. Common development setups:

```bash
# Start your Flask app (typically on port 5000)
cd /path/to/your/flask/backend
python app.py
```

#### 2. Network Configuration

**For Android Emulator:**
- Use `http://10.0.2.2:5000/` to access `localhost:5000` on your host machine
- The emulator maps `10.0.2.2` to the host's localhost

**For Physical Device:**
- Find your computer's IP address: `ipconfig` (Windows) or `ifconfig` (Mac/Linux)
- Use `http://YOUR_IP_ADDRESS:5000/` (e.g., `http://192.168.1.100:5000/`)
- Make sure your Flask app accepts connections from all interfaces:
  ```python
  app.run(host='0.0.0.0', port=5000, debug=True)
  ```

#### 3. API Endpoints

The Android app expects these Flask API endpoints:

**Authentication:**
- `POST /api/user/login` - User login
- `POST /api/user/register` - User registration
- `GET /api/user/profile` - Get user profile
- `PUT /api/user/edit` - Update user profile
- `POST /api/user/logout` - User logout

**Portals:**
- `GET /api/portal/list` - Get all portals
- `POST /api/portal/filter_network_portals` - Filter portals
- `GET /api/portal/details` - Get portal details
- `POST /api/portal/` - Create new portal
- `PUT /api/portal/edit` - Update portal

**Goals:**
- `GET /api/goals/list` - Get goals
- `POST /api/goals/` - Create new goal
- `PUT /api/goals/edit` - Update goal
- `POST /api/goals/progress` - Update goal progress

**Messaging:**
- `GET /api/messaging/conversations` - Get conversation list
- `GET /api/messaging/list` - Get messages
- `POST /api/messaging/send` - Send message

#### 4. CORS Configuration

Add CORS support to your Flask backend:

```python
from flask_cors import CORS

app = Flask(__name__)
CORS(app)  # This allows all origins
```

#### 5. JWT Token Format

The Android app expects JWT tokens in this format:

```json
{
  "token": "your.jwt.token.here",
  "user": {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    // ... other user fields
  }
}
```

## Development Status

### ✅ Completed Features
- Project structure and build configuration
- Authentication flow UI (Login, Register, Onboarding)
- Navigation system with bottom tabs
- Material 3 theming with Rep brand colors
- API service interfaces for all endpoints
- Repository pattern implementation
- Dagger Hilt dependency injection setup
- JWT token management with automatic header injection

### 🚧 In Progress
- API integration with Flask backend
- Room database implementation for offline storage
- Image upload functionality
- Portal creation and management UI
- Goals tracking interface
- Real-time messaging implementation

### 📋 TODO
- WebSocket integration for real-time features
- Push notifications
- Offline data synchronization
- Image caching and optimization
- Comprehensive error handling and user feedback
- Unit and integration tests

## API Integration Status

The app includes comprehensive API integration ready for your Flask backend:

- **AuthRepository**: Handles login, registration, profile management
- **PortalRepository**: Manages portal CRUD operations
- **Networking**: Retrofit with Moshi, automatic JWT token injection
- **Error Handling**: Proper error propagation and user feedback
- **Loading States**: UI loading indicators during API calls

## Building and Running

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

### Running Tests
```bash
./gradlew test
```

## Contributing

1. Follow the existing code style and architecture patterns
2. Use Kotlin coroutines for asynchronous operations
3. Implement proper error handling with sealed classes
4. Follow Material 3 design guidelines
5. Write tests for new features

## Troubleshooting

### Common Issues

1. **Network Connection Issues**
   - Verify Flask backend is running
   - Check IP address and port configuration
   - Ensure CORS is properly configured on Flask backend

2. **Build Issues**
   - Clean and rebuild: `./gradlew clean build`
   - Invalidate caches in Android Studio
   - Check Gradle and dependency versions

3. **Authentication Issues**
   - Verify JWT token format matches expected structure
   - Check token expiration handling
   - Ensure proper CORS headers for authentication endpoints

### Debugging Network Requests

Enable network logging by checking the `HttpLoggingInterceptor` in `NetworkModule.kt`. This will show all API requests and responses in the logcat.

## License

This project is part of the Rep social networking platform.
