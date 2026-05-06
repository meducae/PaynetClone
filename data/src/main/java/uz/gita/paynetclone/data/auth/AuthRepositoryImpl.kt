package uz.gita.paynetclone.data.auth

import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import uz.gita.paynetclone.entity.auth.AuthResult
import uz.gita.paynetclone.usecase.auth.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi
) : AuthRepository {

    override suspend fun sendOtp(phone: String): Result<Unit> {
        return try {
            val formattedPhone = if (phone.startsWith("+")) phone else "+998$phone"
            val response = authApi.sendOtp(SendOtpRequest(formattedPhone))
            val body = response.body()

            if (response.isSuccessful && body != null && body.success) {
                Result.success(Unit)
            } else {
                val errorMessage = parseErrorBody(response.errorBody())
                    ?: body?.data?.message
                    ?: "HTTP ${response.code()}: ${response.message()}"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyOtp(phone: String, otp: String): Result<AuthResult> {
        return try {
            val formattedPhone = if (phone.startsWith("+")) phone else "+998$phone"
            val response = authApi.verifyOtp(VerifyOtpRequest(formattedPhone, otp))
            val body = response.body()

            if (response.isSuccessful && body != null && body.success) {
                val data = body.data
                if (data != null) {
                    Result.success(
                        AuthResult(
                            accessToken = data.accessToken,
                            refreshToken = data.refreshToken,
                            isNewUser = data.isNewUser
                        )
                    )
                } else {
                    Result.failure(Exception("Empty auth data in response"))
                }
            } else {
                // Handle 400 and other error codes by parsing the error body
                val errorMessage = parseErrorBody(response.errorBody())
                    ?: body?.error?.message
                    ?: "HTTP ${response.code()}: ${response.message()}"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun setPin(pin: String): Result<Unit> {
        return try {
            val response = authApi.setPin(SetPinRequest(pin))
            val body = response.body()

            if (response.isSuccessful && body != null && body.success) {
                Result.success(Unit)
            } else {
                val errorMessage = parseErrorBody(response.errorBody())
                    ?: body?.message
                    ?: "HTTP ${response.code()}: ${response.message()}"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Attempts to extract a human-readable message from the error response body.
     * Matches contract: { "success": false, "error": { "code": "...", "message": "..." } }
     */
    private fun parseErrorBody(errorBody: ResponseBody?): String? {
        return try {
            val raw = errorBody?.string() ?: return null
            val json = Gson().fromJson(raw, JsonObject::class.java)
            
            // Priority 1: error.message (from contract)
            json?.getAsJsonObject("error")?.get("message")?.asString
                // Priority 2: root message
                ?: json?.get("message")?.asString
                // Priority 3: root error string
                ?: json?.get("error")?.asString
                // Fallback to raw string if it's not too long
                ?: if (raw.length < 100) raw else null
        } catch (_: Exception) {
            null
        }
    }
}