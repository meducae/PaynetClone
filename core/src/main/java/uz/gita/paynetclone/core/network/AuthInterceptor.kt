package uz.gita.paynetclone.core.network

import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import uz.gita.paynetclone.core.common.AuthEventBus
import uz.gita.paynetclone.core.common.TokenManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
    private val authEventBus: AuthEventBus
) : Interceptor {

    private val gson = Gson()

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        if (originalRequest.url.toString().contains("api/v1/auth/refresh")) {
            return chain.proceed(originalRequest)
        }

        val accessToken = runBlocking { tokenManager.getAccessToken() }

        val requestBuilder = originalRequest.newBuilder()
        if (!accessToken.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $accessToken")
        }

        val response = chain.proceed(requestBuilder.build())

        if (response.code == 401) {
            synchronized(this) {
                val newAccessToken = runBlocking { tokenManager.getAccessToken() }

                if (newAccessToken != accessToken) {
                    val newRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer $newAccessToken")
                        .build()
                    response.close()
                    return chain.proceed(newRequest)
                }

                val refreshToken = runBlocking { tokenManager.getRefreshToken() }
                if (refreshToken != null) {
                    val refreshResponse = refreshAccessToken(refreshToken)
                    if (refreshResponse != null && refreshResponse.success) {
                        val newData = refreshResponse.data
                        if (newData != null) {
                            runBlocking {
                                tokenManager.saveTokens(
                                    newData.accessToken,
                                    newData.refreshToken
                                )
                            }
                            val newRequest = originalRequest.newBuilder()
                                .header("Authorization", "Bearer ${newData.accessToken}")
                                .build()
                            response.close()
                            return chain.proceed(newRequest)
                        }
                    } else {
                        runBlocking { 
                            tokenManager.clearTokens()
                            authEventBus.logout()
                        }
                    }
                } else {
                    runBlocking { authEventBus.logout() }
                }
            }
        }

        return response
    }

    private fun refreshAccessToken(refreshToken: String): RefreshResponse? {
        val client = okhttp3.OkHttpClient()
        val body = RefreshRequest(refreshToken)
        val requestBody = gson.toJson(body).toRequestBody("application/json".toMediaType())
        
        val request = Request.Builder()
            .url("https://banking-api.zokirov-mob-dev.uz/api/v1/auth/refresh")
            .post(requestBody)
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    gson.fromJson(response.body?.string(), RefreshResponse::class.java)
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    private data class RefreshRequest(val refreshToken: String)
    private data class RefreshResponse(
        val success: Boolean,
        val data: RefreshData?
    )
    private data class RefreshData(
        val accessToken: String,
        val refreshToken: String
    )
}
