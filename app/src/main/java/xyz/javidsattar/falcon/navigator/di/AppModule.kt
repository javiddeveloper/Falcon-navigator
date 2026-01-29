package xyz.javidsattar.falcon.navigator.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import xyz.javidsattar.falcon.navigator.data.remote.MockWebSocketService
import xyz.javidsattar.falcon.navigator.data.remote.WebSocketService
import xyz.javidsattar.falcon.navigator.data.repository.CameraRepository
import xyz.javidsattar.falcon.navigator.data.repository.CameraRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindWebSocketService(
        mockWebSocketService: MockWebSocketService
    ): WebSocketService

    @Binds
    @Singleton
    abstract fun bindCameraRepository(
        cameraRepositoryImpl: CameraRepositoryImpl
    ): CameraRepository
}
