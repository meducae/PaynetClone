package uz.gita.paynetclone.data.repository

import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import uz.gita.paynetclone.data.mapper.toEntity
import uz.gita.paynetclone.data.remote.api.UserApi
import uz.gita.paynetclone.entity.user.User
import uz.gita.paynetclone.usecase.user.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi
) : UserRepository {

    override suspend fun getProfile(): Result<User> {
        return try {
            val response = userApi.getProfile()
            val body = response.body()

            if (response.isSuccessful && body != null && body.success) {
                val data = body.data
                if (data != null) {
                    Result.success(data.toEntity())
                } else {
                    Result.failure(Exception("Empty user data"))
                }
            } else {
                val errorMessage = parseErrorBody(response.errorBody()) ?: "Failed to get profile"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseErrorBody(errorBody: ResponseBody?): String? {
        return try {
            val raw = errorBody?.string() ?: return null
            val json = Gson().fromJson(raw, JsonObject::class.java)
            json?.getAsJsonObject("error")?.get("message")?.asString
                ?: json?.get("message")?.asString
                ?: if (raw.length < 100) raw else null
        } catch (_: Exception) {
            null
        }
    }
}
