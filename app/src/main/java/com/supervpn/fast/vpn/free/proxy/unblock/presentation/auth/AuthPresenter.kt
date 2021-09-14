package com.supervpn.fast.vpn.free.proxy.unblock.presentation.auth

import com.supervpn.fast.vpn.free.proxy.unblock.common.base.BasePresenter
import com.supervpn.fast.vpn.free.proxy.unblock.common.utils.completableTransformer
import com.supervpn.fast.vpn.free.proxy.unblock.common.utils.observableTransformer
import com.supervpn.fast.vpn.free.proxy.unblock.domain.model.User
import com.supervpn.fast.vpn.free.proxy.unblock.domain.repos.UserRepository
import com.steve.utilities.core.extensions.addToCompositeDisposable
import timber.log.Timber
import javax.inject.Inject

class AuthPresenter @Inject constructor() : BasePresenter<AuthView>() {
    @Inject
    lateinit var userRepository: UserRepository

    fun signIn(email: String, password: String) {
        userRepository.signIn(email, password)
            .compose(observableTransformer(view))
            .subscribe({
                view?.onSignInSuccess(it)
            }, this::handleError)
            .addToCompositeDisposable(disposable)
    }

    fun signUp(email: String, password: String) {
        userRepository.signUp(email, password)
            .compose(completableTransformer(view))
            .subscribe({
                Timber.e("sigunUp successfully")
                val user = User(email, password)
                view?.onSignUpSuccess(user)
            }, this::handleError)
            .addToCompositeDisposable(disposable)
    }

}