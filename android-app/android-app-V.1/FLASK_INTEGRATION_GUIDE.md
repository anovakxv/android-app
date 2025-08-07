# Flask Backend Integration Guide

This guide shows how to connect your Android app to the existing Flask backend.

## Backend Analysis Summary

Based on your Flask backend in `Python_Backend/your-app/`, here's what the Android app has been configured for:

### 1. Authentication Endpoints

**Login**: `POST /api/user/login`
- Request: `{"email": "user@example.com", "password": "password123"}`
- Response: `{"result": {...user object...}, "token": "jwt.token.here"}`

**Register**: `POST /api/user/register` (Multipart form data)
- Fields: email, password, fname, lname, users_types_id, phone, about, manual_city
- Optional: image file for profile picture
- Response: `{"result": {...user object...}, "token": "jwt.token.here"}`

**Logout**: `POST /api/user/logout`
- Response: `{"result": "ok"}`

### 2. Portal Endpoints

**Get Portals**: `GET /api/portal/portals`
- Returns list of portals with mainImageUrl

**Portal Details**: `GET /api/portal/details?portal_id=123`

### 3. Messaging Endpoints

**Get Messages**: `GET /api/message/get_messages?users_id=123`
**Send Message**: `POST /api/message/send_direct_message`

### 4. Goals Endpoints

**Get Goals**: `GET /api/goals/list`
**Goal Details**: `GET /api/goals/details?goal_id=123`

## Running the Flask Backend

1. Navigate to your Flask backend:
```bash
cd "Python_Backend/your-app"
```

2. Activate virtual environment (if using one):
```bash
# Windows
venv\Scripts\activate
# macOS/Linux
source venv/bin/activate
```

3. Install dependencies:
```bash
pip install -r requirements.txt
```

4. Set up environment variables:
Create a `.env` file with:
```
PASS_SALT=your_password_salt_here
JWT_SECRET=your_jwt_secret_here
DATABASE_URL=sqlite:///instance/test.db
```

5. Run Flask app:
```bash
python run.py
```

The Flask app should start on `http://localhost:5000`

## Android App Configuration

### 1. Update API Base URL

In `ApiConfig.kt`, the base URL is already set for local development:

```kotlin
const val BASE_URL = "http://10.0.2.2:5000/"  // For Android emulator
```

For a physical device, change to your computer's IP:
```kotlin
const val BASE_URL = "http://192.168.1.XXX:5000/"  // Replace XXX with your IP
```

### 2. Ensure CORS is Enabled

Your Flask app needs CORS enabled for Android requests. In your Flask `__init__.py`:

```python
from flask_cors import CORS
CORS(app, origins=["*"])  # Allow all origins for development
```

### 3. Test the Connection

1. Build and run the Android app
2. Try to register a new user
3. Check Flask console for incoming requests
4. Check Android logcat for API responses

## API Integration Status

The Android app includes:

✅ **AuthRepository** - Handles login/register with JWT tokens
✅ **API Services** - Retrofit interfaces matching Flask endpoints  
✅ **Request/Response Models** - Data classes matching Flask JSON format
✅ **Error Handling** - Proper error propagation from Flask to UI
✅ **Token Management** - Automatic JWT header injection

## Data Models Mapping

### User Model (Flask → Android)

Flask User model fields mapped to Android:
- `id` → `id`
- `email` → `email`
- `fname` → `fname`
- `lname` → `lname`
- `about` → `about`
- `profile_picture_url` → `profile_picture_url`
- `users_types_id` → `users_types_id`
- `manual_city` → `manual_city`

## Testing the Integration

### 1. Test Authentication

```kotlin
// Login test
authViewModel.login("test@example.com", "password123")

// Register test  
authViewModel.register(
    firstName = "John",
    lastName = "Doe", 
    email = "john@example.com",
    password = "password123",
    userTypeId = 1 // Lead
)
```

### 2. Monitor Network Traffic

Enable network logging in `NetworkModule.kt`:
```kotlin
HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}
```

### 3. Check Flask Logs

Monitor your Flask console for incoming requests:
```
POST /api/user/login
GET /api/portal/portals  
POST /api/message/send_direct_message
```

## Common Issues & Solutions

### 1. Connection Refused
- Ensure Flask is running on port 5000
- Check firewall settings
- Verify IP address in ApiConfig.kt

### 2. CORS Errors
- Add CORS support to Flask app
- Allow Android app origin in CORS settings

### 3. Authentication Errors
- Check JWT secret key matches between Flask and Android
- Verify token format in Flask response
- Check AuthInterceptor is adding Bearer token correctly

### 4. Database Errors
- Ensure Flask database is migrated: `flask db upgrade`
- Check database permissions
- Verify models are properly imported in Flask `__init__.py`

## Development Workflow

1. **Start Flask Backend**
   ```bash
   cd Python_Backend/your-app
   python run.py
   ```

2. **Run Android App**
   - Open in Android Studio
   - Build and run on emulator or device

3. **Test API Endpoints**
   - Use Android app to make requests
   - Monitor Flask console for requests
   - Check Android logcat for responses

4. **Debug Issues**
   - Check network logs in Android
   - Verify Flask route definitions
   - Test endpoints with Postman/curl

## Next Steps

1. **Complete Portal Management** - Add CRUD operations for portals
2. **Implement Goals Tracking** - Add goal creation and progress updates  
3. **Real-time Messaging** - Integrate WebSocket support
4. **Image Upload** - Implement profile and portal image uploads
5. **Offline Support** - Add Room database for local storage

The Android app architecture is ready for these features with the repository pattern and Retrofit networking already configured to match your Flask backend exactly.
