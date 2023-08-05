package com.hfad.ismlarmanosi2023.data

import android.content.Context
import androidx.room.Room
import com.hfad.ismlarmanosi2023.language.MyPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context,
        myPreference: MyPreference,
    ): NamesDatabase {
        val lang = myPreference.getLoginCount()
        val databaseFileName = if (lang == "en") "all_names.db" else "all_names_uzz.db"

        return Room.databaseBuilder(
            context,
            NamesDatabase::class.java,
            databaseFileName
        ).createFromAsset("databases/$databaseFileName").build()
    }

    @Singleton
    @Provides
    fun provideDao(database: NamesDatabase) = database.namesDao()

    @Singleton
    @Provides
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }
}