package com.grayhatdevelopers.kontrol.ui.fragments.login

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.observe
import com.grayhatdevelopers.kontrol.R
import com.grayhatdevelopers.kontrol.databinding.FragmentLoginBinding
import com.grayhatdevelopers.kontrol.di.kodeinViewModel
import com.grayhatdevelopers.kontrol.ui.fragments.base.BaseFragment
import com.grayhatdevelopers.kontrol.utils.toast
import timber.log.Timber

class LoginFragment : BaseFragment() {

    private val mViewModel: LoginViewModel by kodeinViewModel()
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
                    Timber.d("User not logged in")
                    animateLoginFields()
                }

                LoginActions.AlreadyLoggedIn -> {
                    Timber.d("User already logged in")
                    navigateTo(LoginFragmentDirections.actionLoginFragmentToDashboardFragment())
                }
                LoginActions.ProceedLogin -> {
                    showLoadingDialog()
                    Timber.d("Proceeding login")
                }

                LoginActions.LoginSucceeded -> {
                    Timber.d("Login was successful")
                    // open the Dashboard fragment
                    navigateTo(LoginFragmentDirections.actionLoginFragmentToDashboardFragment())
                }

                LoginActions.MissingInformation -> {
                    Timber.d("Not proper information was provided")
                    context?.toast("Username/Password missing!")
                }

                is LoginActions.LoginFailed -> {
                    Timber.d("Login failed for reason: ${it.error}")
                    context?.toast(it.error)
                    dismissDialog()
                }
            }
        }
    }

    private fun animateLoginFields() {
        loginFragmentBinding.usernameContainer.animate().alpha(1.0f).duration = ANIMATION_DURATION
        loginFragmentBinding.passwordContainer.animate().alpha(1.0f).duration = ANIMATION_DURATION
        loginFragmentBinding.buttonContainer.animate().alpha(1.0f).duration = ANIMATION_DURATION
    }

    companion object {
        private const val ANIMATION_DURATION = 700.toLong()
        private const val LOGO_ANIMATION_DURATION = 900.toLong()
        private const val LOGO_ANIMATION_TRANSLATION = (-200).toLong()
    }
}