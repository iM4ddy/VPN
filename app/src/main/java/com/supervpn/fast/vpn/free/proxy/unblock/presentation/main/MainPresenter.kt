package com.supervpn.fast.vpn.free.proxy.unblock.presentation.main

import com.supervpn.fast.vpn.free.proxy.unblock.common.base.BasePresenter
import com.supervpn.fast.vpn.free.proxy.unblock.domain.repos.ConfigRepository
import com.supervpn.fast.vpn.free.proxy.unblock.domain.repos.UserRepository
import com.steve.utilities.core.extensions.addToCompositeDisposable
import com.steve.utilities.core.extensions.completableTransformer
import com.steve.utilities.core.extensions.observableTransformer
import timber.log.Timber
import javax.inject.Inject

class MainPresenter @Inject constructor() : BasePresenter<MainView>() {
    @Inject
    lateinit var configRepository: ConfigRepository

    @Inject
    lateinit var userRepository: UserRepository

    fun getConfigs() {
        configRepository.getConfig()
            .compose(observableTransformer())
            .subscribe({
                view?.onGetConfigsSuccess(it)
            }, Timber::e)
            .addToCompositeDisposable(disposable)
    }

    fun syncLogin() {
        userRepository.syncUserLogin()
            .compose(completableTransformer())
            .subscribe({},Timber::e)
            .addToCompositeDisposable(disposable)
    }
}