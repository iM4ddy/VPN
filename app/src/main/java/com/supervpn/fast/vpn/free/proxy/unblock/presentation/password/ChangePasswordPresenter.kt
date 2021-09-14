package com.supervpn.fast.vpn.free.proxy.unblock.presentation.password

import com.supervpn.fast.vpn.free.proxy.unblock.common.base.BasePresenter
import com.supervpn.fast.vpn.free.proxy.unblock.domain.repos.UserRepository
import com.steve.utilities.core.extensions.addToCompositeDisposable
import com.steve.utilities.core.extensions.completableTransformer
import timber.log.Timber
import javax.inject.Inject

class ChangePasswordPresenter @Inject constructor() : BasePresenter<ChangePasswordView>() {
    @Inject
    lateinit var userRepository: UserRepository

    fun changePassword(newPassword: String) {
        userRepository.changePassword(newPassword)
            .compose(completableTransformer())
            .subscribe({
                view?.onChangePasswordSuccess()
            }, Timber::e)
            .addToCompositeDisposable(disposable)
    }
}