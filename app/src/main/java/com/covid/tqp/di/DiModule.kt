package com.covid.tqp.di

import android.content.Context
import com.covid.tqp.data.repository.DataRepositoryImpl
import com.covid.tqp.data.source.local.UserPreferencesRepository
import com.covid.tqp.data.source.remote.Covid19ApiService
import com.covid.tqp.data.source.remote.RemoteDataSource
import com.covid.tqp.domain.repository.DataRepository
import com.covid.tqp.domain.usecase.GetDataUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

const val API_KEY = "TU_API_KEY_AQUI"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApiKeyInterceptor(): Interceptor {
        return object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val originalRequest = chain.request()
                val newRequest = originalRequest.newBuilder()
                    .header("X-Api-Key", API_KEY)
                    .build()
                return chain.proceed(newRequest)
            }
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(apiKeyInterceptor: Interceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
        return OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.api-ninjas.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideCovid19ApiService(retrofit: Retrofit): Covid19ApiService {
        return retrofit.create(Covid19ApiService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideRemoteDataSource(apiService: Covid19ApiService): RemoteDataSource {
        return RemoteDataSource(apiService)
    }

    @Provides
    @Singleton
    fun provideDataRepository(remoteDataSource: RemoteDataSource): DataRepository {
        return DataRepositoryImpl(remoteDataSource)
    }

    @Provides
    @Singleton
    fun provideGetDataUseCase(dataRepository: DataRepository): GetDataUseCase {
        return GetDataUseCase(dataRepository)
    }

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(@ApplicationContext context: Context): UserPreferencesRepository {
        return UserPreferencesRepository(context)
    }
}