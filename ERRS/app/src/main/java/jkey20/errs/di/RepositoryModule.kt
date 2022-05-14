package jkey20.errs.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import jkey20.errs.repository.FirebaseRepository
import jkey20.errs.repository.ServerRepository

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {
    @Provides
    @ViewModelScoped
    fun provideFirebaseRepository() : FirebaseRepository = FirebaseRepository()

    @Provides
    @ViewModelScoped
    fun provideServerRepository() : ServerRepository = ServerRepository()
}