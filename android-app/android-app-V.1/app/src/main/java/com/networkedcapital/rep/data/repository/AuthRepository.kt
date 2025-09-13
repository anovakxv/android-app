package com.networkedcapital.rep.data.repository

import com.networkedcapital.rep.data.api.*
import com.networkedcapital.rep.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.RequestBody.Companion.toRequestBody

@Singleton
class AuthRepository @Inject constructor(
    private val authApiService: AuthApiService,
    private val authInterceptor: AuthInterceptor
) {

    suspend fun updateProfile(
        firstName: String,
        lastName: String,
        email: String,
        broadcast: String,
        repType: String,
        city: String,
        about: String,
        otherSkill: String,
        skills: List<String>,
        imageUrl: String?
    ): Flow<Result<User>> = flow {
        val user = User(
            id = 0,
            fname = firstName,
            lname = lastName,
            email = email,
            broadcast = broadcast,
            userType_string = repType,
            manual_city = city,
            about = about,
            other_skill = otherSkill,
            skills = skills.mapIndexed { idx, title -> com.networkedcapital.rep.domain.model.Skill(idx, title) },
            profile_picture_url = imageUrl
        )
        updateProfile(user).collect { emit(it) }
    }

    suspend fun uploadProfileImage(profileImageUri: String): Flow<Result<String>> = flow {
        // TODO: Use content resolver to get file from URI and upload as MultipartBody.Part
        // For now, just emit a dummy URL
        emit(Result.success("https://dummyimage.com/200x200"))
    }

    suspend fun login(email: String, password: String): Flow<Result<User>> = flow {
        try {
            val response = authApiService.login(LoginRequest(email = email, password = password))
            if (response.isSuccessful) {
                val loginResponse = response.body()
                if (loginResponse != null) {
                    // Save token
                    authInterceptor.saveToken(loginResponse.token)
                    emit(Result.success(loginResponse.result))
                } else {
                    emit(Result.failure(Exception("Invalid response")))
                }
            } else {
                emit(Result.failure(Exception("Login failed: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        userTypeId: Int = 1, // Default to Lead (1)
        phone: String? = null,
        about: String? = null,
        city: String? = null
    ): Flow<Result<User>> = flow {
        try {
            val emailBody = email.toRequestBody()
            val passwordBody = password.toRequestBody()
            val firstNameBody = firstName.toRequestBody()
            val lastNameBody = lastName.toRequestBody()
            val userTypeIdBody = userTypeId.toString().toRequestBody()
            val phoneBody = phone?.toRequestBody()
            val aboutBody = about?.toRequestBody()
            val cityBody = city?.toRequestBody()

            val response = authApiService.register(
                email = emailBody,
                password = passwordBody,
                firstName = firstNameBody,
                lastName = lastNameBody,
                userTypeId = userTypeIdBody,
                phone = phoneBody,
                about = aboutBody,
                city = cityBody
            )

            // Debug logging: print raw response
            println("[AuthRepository] register raw response: ${response.raw()}\nBody: ${response.body()}")

            if (response.isSuccessful) {
                val registerResponse = response.body()
                if (registerResponse != null) {
                    // Debug: print nested skills mapping
                    println("[AuthRepository] registerResponse.result.skills: ${registerResponse.result.skills}")
                    // Save token
                    authInterceptor.saveToken(registerResponse.token)
                    emit(Result.success(registerResponse.result))
                } else {
                    emit(Result.failure(Exception("Invalid response")))
                }
            } else {
                emit(Result.failure(Exception("Registration failed: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun logout(): Flow<Result<Unit>> = flow {
        try {
            val response = authApiService.logout()
            authInterceptor.clearToken()
            if (response.isSuccessful) {
                emit(Result.success(Unit))
            } else {
                emit(Result.failure(Exception("Logout failed: ${response.message()}")))
            }
        } catch (e: Exception) {
            // Clear token even if API call fails
            authInterceptor.clearToken()
            emit(Result.success(Unit))
        }
    }

    suspend fun getProfile(): Flow<Result<User>> = flow {
        try {
            val response = authApiService.getProfile()
            if (response.isSuccessful) {
                val user = response.body()
                if (user != null) {
                    emit(Result.success(user))
                } else {
                    emit(Result.failure(Exception("User not found")))
                }
            } else {
                emit(Result.failure(Exception("Failed to get profile: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun getCurrentUser(): Flow<Result<User>> = getProfile()

    suspend fun updateProfile(user: User): Flow<Result<User>> = flow {
        try {
            val response = authApiService.updateProfile(user)
            if (response.isSuccessful) {
                val updatedUser = response.body()
                if (updatedUser != null) {
                    emit(Result.success(updatedUser))
                } else {
                    emit(Result.failure(Exception("Update failed")))
                }
            } else {
                emit(Result.failure(Exception("Profile update failed: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun uploadProfileImage(image: MultipartBody.Part): Flow<Result<String>> = flow {
        try {
            val response = authApiService.uploadProfileImage(image)
            if (response.isSuccessful) {
                val uploadResponse = response.body()
                if (uploadResponse != null) {
                    emit(Result.success(uploadResponse.imageUrl))
                } else {
                    emit(Result.failure(Exception("Upload failed")))
                }
            } else {
                emit(Result.failure(Exception("Image upload failed: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun isLoggedIn(): Boolean {
        return authInterceptor.getToken() != null
    }

    suspend fun deleteProfile(): Flow<Result<Unit>> = flow {
        try {
            val response = authApiService.deleteProfile()
            if (response.isSuccessful) {
                authInterceptor.clearToken()
                emit(Result.success(Unit))
            } else {
                emit(Result.failure(Exception("Delete failed: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}

