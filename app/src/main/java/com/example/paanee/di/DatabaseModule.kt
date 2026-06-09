package com.example.paanee.di

import android.content.Context
import androidx.room.Room
import com.example.paanee.data.local.AppDatabase
import com.example.paanee.data.local.HydrationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "paanee_database"
        ).build()
    }

    @Provides
    fun provideHydrationDao(appDatabase: AppDatabase): HydrationDao {
        return appDatabase.hydrationDao()
    }
}
