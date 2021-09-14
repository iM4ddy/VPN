package com.supervpn.fast.vpn.free.proxy.unblock.common.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import java.lang.reflect.InvocationTargetException

class SubActivity : BaseActivity() {
    companion object {
        const val EXTRA_FRAGMENT_CLASS = "EXTRA_FRAGMENT_CLASS"
        const val EXTRA_FRAGMENT_ARGS = "EXTRA_FRAGMENT_ARGS"

        private fun createIntent(context: Context?): Intent {
            return Intent(context, SubActivity::class.java)
        }

        fun start(context: Context?, fragment: Class<*>, bundle: Bundle? = null, flags: Int? = null) {
            val intent = createIntent(context)
                .apply {
                    putExtra(EXTRA_FRAGMENT_CLASS, fragment)
                    putExtra(EXTRA_FRAGMENT_ARGS, bundle)
                    flags?.let {
                        this.flags = it
                    }
                }
            context?.startActivity(intent)
        }

    }

    @Throws(
        InvocationTargetException::class,
        IllegalArgumentException::class,
        NullPointerException::class,
        NoSuchFieldException::class
    )
    override fun injectFragment(): BaseFragment<*, *> {
        val extras = intent.extras ?: throw Exception("Obs: intent.extras is null")

        val clazz = extras.getSerializable(EXTRA_FRAGMENT_CLASS) as Class<*>

        val c = clazz.getConstructor()
        val fragment: BaseFragment<*, *> = c.newInstance() as BaseFragment<*, *>
        extras.getBundle(EXTRA_FRAGMENT_ARGS)
            ?.let {
                fragment.setArguments(it)
            }
        return fragment
    }
}