package com.supervpn.fast.vpn.free.proxy.unblock.common.di.module

import com.supervpn.fast.vpn.free.proxy.unblock.data.repos.BillingRepositoryImpl
import com.supervpn.fast.vpn.free.proxy.unblock.data.repos.ConfigRepositoryImpl
import com.supervpn.fast.vpn.free.proxy.unblock.data.repos.ServerRepositoryImpl
import com.supervpn.fast.vpn.free.proxy.unblock.data.repos.UserRepositoryImpl
import com.supervpn.fast.vpn.free.proxy.unblock.domain.repos.BillingRepository
import com.supervpn.fast.vpn.free.proxy.unblock.domain.repos.ConfigRepository
import com.supervpn.fast.vpn.free.proxy.unblock.domain.repos.ServerRepository
import com.supervpn.fast.vpn.free.proxy.unblock.domain.repos.UserRepository
import dagger.Binds
import dagger.Module

@Module
abstract class RepositoryModule {
    @Binds
    abstract fun bindBillingRepository(billingRepositoryImpl: BillingRepositoryImpl): BillingRepository

    @Binds
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Binds
    abstract fun bindServerRepository(serverRepositoryImpl: ServerRepositoryImpl): ServerRepository

    @Binds
    abstract fun bindConfigRepository(configRepositoryImpl: ConfigRepositoryImpl): ConfigRepository
}