package com.grayhatdevelopers.kontrol.ui.fragments.login

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.observe
import com.grayhatdevelopers.kontrol.utils.toast
import com.grayhatdevelopers.kontrol.R
import com.grayhatdevelopers.kontrol.databinding.FragmentLoginBinding
import com.grayhatdevelopers.kontrol.ui.fragments.base.BaseFragment
import com.grayhatdevelopers.kontrol.utils.InjectorUtils
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : BaseFragment() {

    private lateinit var mViewModel: LoginViewModel
    private lateinit var loginFragmentBinding: FragmentLoginBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loginFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_login,
            container,
            false
        )
        loginFragmentBinding.apply {
            lifecycleOwner = this@LoginFragment
            mViewModel = InjectorUtils.provideLoginViewModel()
            viewModel = mViewModel
        }
        return loginFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        startLoginAnimation()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // observe login states
        observeLoginActions()
    }

    private fun startLoginAnimation() {
        // startup animation
        ObjectAnimator.ofFloat(
            loginFragmentBinding.logoImg,
            "translationY",
            (LOGO_ANIMATION_TRANSLATION).toFloat()
        ).apply {
            duration = LOGO_ANIMATION_DURATION
            start()
        }.doOnEnd {
            // notify view model to check for login status
            mViewModel.checkLoginStatus()
        }
    }

    private fun observeLoginActions() {
        mViewModel.loginActions.observe(viewLifecycleOwner) {
            dismissDialog()
            when (it) {
                LoginActions.NotLoggedIn -> {
                    Log.d(TAG, "User not logged in")
                    animateLoginFields()
                }

                LoginActions.AlreadyLoggedIn -> {
                    Log.d(TAG, "User already logged in")
                    baseViewModel.navigate(LoginFragmentDirections.actionLoginFragmentToDashboardFragment())
                }
                LoginActions.ProceedLogin -> {
                    showLoadingDialog()
                    Log.d(TAG, "Proceeding login")
                }

                LoginActions.LoginSucceeded -> {
                    Log.d(TAG, "Login was successful")
                    // open the Dashboard fragment
                    baseViewModel.navigate(LoginFragmentDirections.actionLoginFragmentToDashboardFragment())
                }

                LoginActions.MissingInformation -> {
                    Log.d(TAG, "Not proper information was provided")
                    context?.toast("Username/Password missing!")
                }

                is LoginActions.LoginFailed -> {
                    Log.d(TAG, "Login failed for reason: ${it.error}")
                    context?.toast(it.error)
                }
            }
        }
    }

    private fun animateLoginFields() {
        username_et.animate().alpha(1.0f).duration = ANIMATION_DURATION
        password_et.animate().alpha(1.0f).duration = ANIMATION_DURATION
        login_btn.animate().alpha(1.0f).duration = ANIMATION_DURATION
    }

    companion object {
        private const val TAG = "LoginFragment"
        private const val ANIMATION_DURATION = 700.toLong()
        private const val LOGO_ANIMATION_DURATION = 900.toLong()
        private const val LOGO_ANIMATION_TRANSLATION = (-200).toLong()
    }
}