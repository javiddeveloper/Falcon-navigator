package xyz.javidsattar.falcon.navigator.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import xyz.javidsattar.falcon.navigator.data.remote.MockSocketService
import xyz.javidsattar.falcon.navigator.data.remote.SocketService
import xyz.javidsattar.falcon.navigator.data.repository.CameraRepository
import xyz.javidsattar.falcon.navigator.data.repository.CameraRepositoryImpl
import xyz.javidsattar.falcon.navigator.data.repository.ControllerRepository
import xyz.javidsattar.falcon.navigator.data.repository.ControllerRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindSocketService(
        mockSocketService: MockSocketService
    ): SocketService

    @Binds
    @Singleton
    abstract fun bindCameraRepository(
        cameraRepositoryImpl: CameraRepositoryImpl
    ): CameraRepository

    @Binds
    @Singleton
    abstract fun bindControllerRepository(
        controllerRepositoryImpl: ControllerRepositoryImpl
    ): ControllerRepository
}
