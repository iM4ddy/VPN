package com.supervpn.fast.vpn.free.proxy.unblock.presentation.profile

import com.supervpn.fast.vpn.free.proxy.unblock.common.base.BaseView
import com.supervpn.fast.vpn.free.proxy.unblock.domain.model.User

interface EditProfileView : BaseView {
    fun onUpgradeSuccess()
    fun onLogoutSuccess()
    fun onGetCurrentUserSuccess(user: User?)
}