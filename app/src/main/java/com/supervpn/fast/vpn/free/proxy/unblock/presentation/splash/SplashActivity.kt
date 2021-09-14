package com.supervpn.fast.vpn.free.proxy.unblock.presentation.splash;

import com.supervpn.fast.vpn.free.proxy.unblock.common.base.BaseActivity
import com.supervpn.fast.vpn.free.proxy.unblock.common.base.BaseFragment
import com.steve.utilities.core.connectivity.base.ConnectivityProvider

class SplashActivity : BaseActivity() {

    override fun injectFragment(): BaseFragment<*, *> {
        return SplashFragment()
    }

    override fun onStateChange(state: ConnectivityProvider.NetworkState) {
        //nothing
    }
}
