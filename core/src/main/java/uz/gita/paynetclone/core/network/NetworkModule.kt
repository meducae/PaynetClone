package uz.gita.paynetclone.core.network

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uz.gita.paynetclone.core.common.TokenManager
import javax.inject.Singleton
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenManager: TokenManager): Interceptor {
        return object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                val request = chain.request().newBuilder()
                val token = runBlocking { tokenManager.getAccessToken() }
                if (!token.isNullOrEmpty()) {
                    request.addHeader("Authorization", "Bearer $token")
                }
                return chain.proceed(request.build())
            }
        }
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        authInterceptor: Interceptor,
        @ApplicationContext context: Context
    ): Retrofit {
        val logging = HttpLoggingInterceptor().apply { 
            level = HttpLoggingInterceptor.Level.BODY 
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .addInterceptor(authInterceptor)
            .addInterceptor(ChuckerInterceptor(context))
            .build()

        return Retrofit.Builder()
            .baseUrl("https://banking-api.zokirov-mob-dev.uz/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
