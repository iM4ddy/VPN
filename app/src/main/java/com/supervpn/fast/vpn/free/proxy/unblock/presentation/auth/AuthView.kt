package com.supervpn.fast.vpn.free.proxy.unblock.presentation.auth

import com.supervpn.fast.vpn.free.proxy.unblock.common.base.BaseView
import com.supervpn.fast.vpn.free.proxy.unblock.domain.model.User

interface AuthView : BaseView {
    fun onSignInSuccess(user: User?)
    fun onSignUpSuccess(user: User)
}