package com.supervpn.fast.vpn.free.proxy.unblock.presentation.splash

import com.supervpn.fast.vpn.free.proxy.unblock.common.base.BasePresenter
import com.steve.utilities.core.extensions.addToCompositeDisposable
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SplashPresenter @Inject constructor() : BasePresenter<SplashView>() {
    fun sync() {
        Observable.just(true)
            .delay(500, TimeUnit.MILLISECONDS)
            .subscribe(this::handleSyncSuccess, this::handleError)
            .addToCompositeDisposable(disposable)
    }

    private fun handleSyncSuccess(boolean: Boolean) {
        view?.goToMainScreen()
    }
}