package com.lexora.app.ai

import android.content.Context
import com.lexora.app.ai.model.AiProviderSettings
import com.lexora.app.ai.provider.AiSettingsManager
import com.lexora.app.ai.provider.AiServiceProvider
import com.lexora.app.ai.provider.DeepSeekFreeProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiModule {

    @Provides
    @Singleton
    fun provideAiSettingsManager(@ApplicationContext context: Context): AiSettingsManager {
        return AiSettingsManager(context)
    }

    @Provides
    @Singleton
    fun provideAiProviderSettings(settingsManager: AiSettingsManager): AiProviderSettings {
        return settingsManager.getCurrentSettings()
    }

    @Provides
    @Singleton
    fun provideDeepSeekFreeProvider(
        @ApplicationContext context: Context,
        settingsManager: AiSettingsManager
    ): DeepSeekFreeProvider {
        return DeepSeekFreeProvider(context, settingsManager)
    }

    @Provides
    @Singleton
    fun provideAiServiceProvider(
        deepSeekProvider: DeepSeekFreeProvider,
        settingsManager: AiSettingsManager
    ): AiServiceProvider {
        return AiServiceProvider(deepSeekProvider, settingsManager)
    }
}
