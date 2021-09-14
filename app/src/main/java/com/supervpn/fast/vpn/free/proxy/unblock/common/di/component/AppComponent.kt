package com.supervpn.fast.vpn.free.proxy.unblock.common.di.component

import android.app.Application
import com.supervpn.fast.vpn.free.proxy.unblock.common.di.module.AppModule
import com.supervpn.fast.vpn.free.proxy.unblock.common.di.module.RepositoryModule
import com.supervpn.fast.vpn.free.proxy.unblock.presentation.auth.AuthFragment
import com.supervpn.fast.vpn.free.proxy.unblock.presentation.auth.ForgotPasswordFragment
import com.supervpn.fast.vpn.free.proxy.unblock.presentation.home.HomeFragment
import com.supervpn.fast.vpn.free.proxy.unblock.presentation.main.MainFragment
import com.supervpn.fast.vpn.free.proxy.unblock.presentation.password.ChangePasswordFragment
import com.supervpn.fast.vpn.free.proxy.unblock.presentation.other.policy.PrivatePolicyFragment
import com.supervpn.fast.vpn.free.proxy.unblock.presentation.premium.PremiumFragment
import com.supervpn.fast.vpn.free.proxy.unblock.presentation.profile.EditProfileFragment
import com.supervpn.fast.vpn.free.proxy.unblock.presentation.servers.ServerListFragment
import com.supervpn.fast.vpn.free.proxy.unblock.presentation.servers.tab.TabFragment
import com.supervpn.fast.vpn.free.proxy.unblock.presentation.splash.SplashFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        RepositoryModule::class
    ]
)
interface AppComponent {
    fun inject(splashFragment: SplashFragment)
    fun inject(editProfileFragment: EditProfileFragment)
    fun inject(mainFragment: MainFragment)
    fun inject(premiumFragment: PremiumFragment)
    fun inject(homeFragment: HomeFragment)
    fun inject(serverListFragment: ServerListFragment)
    fun inject(tabFragment: TabFragment)
    fun inject(authFragment: AuthFragment)
    fun inject(forgotPasswordFragment: ForgotPasswordFragment)
    fun inject(privatePolicyFragment: PrivatePolicyFragment)
    fun inject(changePasswordFragment: ChangePasswordFragment)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}