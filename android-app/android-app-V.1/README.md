# Rep Android App

An Android social networking application for representatives, converted from the original Swift iOS app. The app helps "Reps" connect, set goals, create portals for collaboration, and communicate with their network.

## Features

- **User Authentication**: Registration, login, and onboarding flow
- **Profile Management**: Complete user profiles with skills, location, and bio
- **Portals**: Group collaboration spaces with goals and content sharing
- **Goals Tracking**: Individual and team goal management with progress visualization
- **Real-time Messaging**: Direct communication between users
- **Content Sharing**: Image uploads and story creation
- **Network Building**: Connect with other representatives

## Architecture

The app follows modern Android development practices:

- **MVVM Architecture** with Repository pattern
- **Jetpack Compose** for modern, declarative UI
- **Dagger Hilt** for dependency injection
- **Room Database** for local data persistence
- **Retrofit** with **Moshi** for API communication
- **Coil** for efficient image loading
- **Material 3** design system
- **Navigation Compose** for type-safe navigation

## Tech Stack

### Core
- Kotlin
- Jetpack Compose
- Android Architecture Components (ViewModel, LiveData, Room)
- Kotlin Coroutines & Flow

### Networking
- Retrofit 2
- OkHttp
- Moshi (JSON parsing)

### Dependency Injection
- Dagger Hilt

### Image Loading
- Coil

### Local Storage
- Room Database
- DataStore (for preferences)

## Project Structure

```
app/src/main/java/com/networkedcapital/rep/
├── data/
│   ├── api/           # API interfaces and configuration
│   ├── database/      # Room database entities and DAOs
│   └── repository/    # Repository implementations
├── domain/
│   ├── model/         # Domain models
│   ├── repository/    # Repository interfaces
│   └── usecase/       # Business logic use cases
├── presentation/
│   ├── auth/          # Authentication screens
│   ├── main/          # Main dashboard
│   ├── profile/       # User profiles
│   ├── portal/        # Portal management
│   ├── goals/         # Goal tracking
│   ├── messaging/     # Chat functionality
│   ├── navigation/    # Navigation setup
│   └── theme/         # UI theme and styling
└── di/                # Dependency injection modules
```

## Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 11 or later
- Android SDK 24 (minimum) to 34 (target)

### Setup

1. Clone the repository
2. Open the project in Android Studio
3. Update the API base URL in `ApiConfig.kt`
4. Sync the project and install dependencies
5. Run the app on an emulator or physical device

### Backend Configuration

Update the base URL in `app/src/main/java/com/networkedcapital/rep/data/api/ApiConfig.kt`:

```kotlin
const val BASE_URL = "https://your-backend-url.com/"
```

## Key Screens

### Authentication Flow
- **Registration**: User signup with email verification
- **Login**: Email/password authentication with JWT tokens
- **Onboarding**: Initial setup and profile creation

### Main Features
- **Dashboard**: Toggle between Portals and People views
- **Profile**: User profiles with skills, goals, and content
- **Portal Details**: Collaborative spaces with goals and media
- **Goal Management**: Progress tracking with charts and team collaboration
- **Messaging**: Real-time chat interface

## Development Status

This is the foundational Android project structure converted from the Swift iOS app. The following components are implemented:

✅ **Completed**
- Project structure and build configuration
- Basic navigation between screens
- Authentication UI flows (Login, Register, Onboarding)
- Main dashboard layout
- Profile screen layout
- Material 3 theming with Rep brand colors

🚧 **In Progress**
- API integration with backend
- Database implementation
- Image handling and uploads
- Real-time messaging
- Goal management features
- Portal creation and management

📋 **Planned**
- Push notifications
- Offline support
- Advanced image editing
- Search functionality
- Network management features

## Contributing

When implementing new features, please follow:

1. MVVM architecture patterns
2. Compose-first UI development
3. Repository pattern for data access
4. Proper error handling and loading states
5. Material 3 design guidelines

## API Integration

The app is designed to work with the existing Python Flask backend. Key endpoints include:

- Authentication: `/api/user/login`, `/api/user/register`
- Profiles: `/api/user/profile`, `/api/user/edit`
- Portals: `/api/portal/filter_network_portals`, `/api/portal/details`
- Goals: `/api/goals/list`, `/api/goals/create`
- Messaging: `/api/message/get_messages`, `/api/message/send_message`

## License

Copyright (c) 2025 Networked Capital Inc. All rights reserved.
