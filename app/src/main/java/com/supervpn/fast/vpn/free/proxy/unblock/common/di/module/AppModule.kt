package com.supervpn.fast.vpn.free.proxy.unblock.common.di.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class AppModule {
    @Provides
    fun provideContextApp(application: Application): Context = application.applicationContext

}