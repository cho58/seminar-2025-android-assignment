package com.example.seminar_assignment_2025.core.di

import android.content.Context
import com.example.seminar_assignment_2025.search.data.local.RecentSearchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    // RecentSearchRepository 를 싱글톤으로 제공
    @Provides
    @Singleton
    fun provideRecentSearchRepository(
        @ApplicationContext context: Context
    ): RecentSearchRepository {
        return RecentSearchRepository(context)
    }
}
