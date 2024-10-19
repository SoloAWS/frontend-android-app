package com.misw.abcalls.di

import android.content.Context
import com.misw.abcalls.data.api.IncidentApiService
import com.misw.abcalls.data.repository.IncidentRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://Infras-MyALB-RudTS2suuSx9-1916035575.us-east-1.elb.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideIncidentApiService(retrofit: Retrofit): IncidentApiService {
        return retrofit.create(IncidentApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideIncidentRepository(
        apiService: IncidentApiService,
        @ApplicationContext context: Context
    ): IncidentRepository {
        return IncidentRepository(apiService, context)
    }
}