package com.dapoi.pitjarustest.di

import android.content.Context
import androidx.room.Room
import com.dapoi.pitjarustest.data.source.ShopRepository
import com.dapoi.pitjarustest.data.source.ShopRepositoryImpl
import com.dapoi.pitjarustest.data.source.local.ShopDao
import com.dapoi.pitjarustest.data.source.local.ShopDatabase
import com.dapoi.pitjarustest.data.source.remote.network.ApiService
import com.facebook.shimmer.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        val baseUrl = "http://dev.pitjarus.co/"
        val loggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        if (!BuildConfig.DEBUG) {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE)
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ShopDatabase {
        return Room.databaseBuilder(
            context,
            ShopDatabase::class.java,
            "shop_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideShopDao(database: ShopDatabase): ShopDao {
        return database.shopDao()
    }

    @Provides
    @Singleton
    fun provideShopRepository(apiService: ApiService, shopDao: ShopDao): ShopRepository {
        return ShopRepositoryImpl(apiService, shopDao)
    }
}