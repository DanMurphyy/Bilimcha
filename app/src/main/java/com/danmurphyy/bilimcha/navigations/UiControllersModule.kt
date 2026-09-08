package com.danmurphyy.bilimcha.navigations

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UiControllersModule {

    @Provides
    @Singleton
    fun provideSheetController(): SheetController = SheetController()

    @Provides
    @Singleton
    fun provideBackStackController(): BackStackController = BackStackController()
}