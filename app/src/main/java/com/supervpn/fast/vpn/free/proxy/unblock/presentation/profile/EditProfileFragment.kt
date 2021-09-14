package com.supervpn.fast.vpn.free.proxy.unblock.presentation.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import com.supervpn.fast.vpn.free.proxy.unblock.R
import com.supervpn.fast.vpn.free.proxy.unblock.common.base.BaseFragment
import com.supervpn.fast.vpn.free.proxy.unblock.common.base.BasePresenter
import com.supervpn.fast.vpn.free.proxy.unblock.common.di.component.AppComponent
import com.supervpn.fast.vpn.free.proxy.unblock.common.dialog.ConfirmDialogFragment
import com.supervpn.fast.vpn.free.proxy.unblock.common.eventbus.ChangeTabEvent
import com.supervpn.fast.vpn.free.proxy.unblock.common.utils.*
import com.supervpn.fast.vpn.free.proxy.unblock.common.widget.bottomnav.BottomNavBar
import com.supervpn.fast.vpn.free.proxy.unblock.domain.model.User
import com.supervpn.fast.vpn.free.proxy.unblock.presentation.main.MainActivity
import com.supervpn.fast.vpn.free.proxy.unblock.presentation.main.MainFragment
import com.supervpn.fast.vpn.free.proxy.unblock.presentation.password.ChangePasswordFragment
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class EditProfileFragment : BaseFragment<EditProfileView, EditProfilePresenter>(), EditProfileView, MainFragment.OnTabChanged {

    @Inject
    lateinit var presenter: EditProfilePresenter

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun presenter(): BasePresenter<EditProfileView>? {
        return presenter
    }

    override fun viewIF(): EditProfileView? {
        return this
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_edit_profile
    }

    override fun initView() {
        tvLogout.setOnClickListener { presenter.logout() }
        tvUpgrade.setOnClickListener { postEvent(ChangeTabEvent.premium) }
        tvViewPackage.setOnClickListener { }
        tvCancelPremium.setOnClickListener { presenter.getAccessToken(this) }
        tvEdit.setOnClickListener {
            context?.startActivity(ChangePasswordFragment::class.java)
        }

        User.getDraft()
            ?.apply {
                updateUI(this)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.onActivityResult(requestCode, resultCode, data)
    }

    //-----------------------Private method ----------------------------
    private fun showConfirmDialog() {
        context?.let {
            ConfirmDialogFragment.cancelPremium(it, childFragmentManager) {
                view_package.visibility = View.GONE
                tvUpgrade.visibility = View.VISIBLE
                showSnackbarInMain(getString(R.string.cancel_premium_success))
            }
        }
    }

    private fun updateUI(user: User?) {
        tvEmail.text = user?.email
        if (getBooleanPref(SharePrefs.KEY_PREMIUM)) {
            tvUpgrade.visibility = View.GONE
            if (Feature.FEATURE_CANCEL_SUBSCRIPTION) {
                view_package.visibility = View.VISIBLE
            }
            val calendar = Calendar.getInstance()
                .apply {
                    timeInMillis = user?.purchaseTime ?: 0L
                }
            tvAccountType.text = if (user?.productId == ProductSku.GOLD_MONTHLY) {
                calendar.add(Calendar.MONTH, 1)
                getString(R.string.gold_monthly_package, calendar.toStringWithPattern())
            } else {
                calendar.add(Calendar.YEAR, 1)
                getString(R.string.gold_yearly_package, calendar.toStringWithPattern())
            }
        } else {
            tvUpgrade.visibility = View.VISIBLE
            view_package.visibility = View.GONE
            tvAccountType.text = getString(R.string.free)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun Calendar.toStringWithPattern(pattern: String = "dd/MM/yyyy"): String {
        val sdf = SimpleDateFormat(pattern)
        return sdf.format(this.time)
    }


    /**
     *  MainFragment.OnTabChanged
     */
    override fun onChange(tabIndex: Int) {
        if (tabIndex != BottomNavBar.TAB_PROFILE) {
            return
        }
        activity?.updateColorStatusBar(R.color.colorPrimary)
    }

    //-----------------------EditProfileView ----------------------------

    override fun onUpgradeSuccess() {
        tvUpgrade.visibility = View.GONE
        view_package.visibility = View.VISIBLE
    }

    override fun onLogoutSuccess() {
        clearAllPref()
        MainActivity.start(context)
    }

    override fun onGetCurrentUserSuccess(user: User?) {
        user?.saveDraft()
        updateUI(user)
    }
}
