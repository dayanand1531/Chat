package com.example.chat.di

import android.content.ContentResolver
import android.content.Context
import androidx.room.Room
import com.example.chat.api.ApiService
import com.example.chat.chatui.ChatRepository
import com.example.chat.room.ChatDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        this.level = HttpLoggingInterceptor.Level.BODY // Set the logging level you prefer
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
    @Singleton
    @Provides
    fun retrofitBuilder(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.mesibo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
    }

    @Singleton
    @Provides
    fun provideApiService(): ApiService {
        return retrofitBuilder().create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver {
        return context.contentResolver
    }

    @Singleton
    @Provides
    fun appDataBase(@ApplicationContext context: Context): ChatDB {
        return Room.databaseBuilder(context, ChatDB::class.java, "ChatDataBase").addMigrations(
        ).build()
    }

    @Provides
    fun provideChatDao(appDataBase:ChatDB) = ChatRepository(appDataBase)

}