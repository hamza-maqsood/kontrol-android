package com.grayhatdevelopers.kontrol.ui.fragments.base

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.grayhatdevelopers.kontrol.R
import com.grayhatdevelopers.kontrol.utils.InjectorUtils

abstract class BaseFragment : Fragment() {

    lateinit var baseViewModel: BaseViewModel
    private lateinit var mLoadingDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseViewModel = InjectorUtils.provideBaseViewModel()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        baseViewModel.navigationCommand.observe(activity!!) { command: NavigationCommand? ->
            when (command!!) {
                is NavigationCommand.To -> {
                    findNavController().navigate(
                        (command as NavigationCommand.To).directions.actionId,
                        command.bundle
                    )
                }

                is NavigationCommand.Back -> {
                    findNavController().popBackStack()
                }

                is NavigationCommand.BackTo -> {
                    findNavController().navigate((command as NavigationCommand.BackTo).destinationId)
                }
            }
        }
    }

    protected fun showLoadingDialog(message: String = "Please Wait") {
        mLoadingDialog = Dialog(context!!)
        mLoadingDialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(false)
            setContentView(R.layout.dialog_loading_layout)
            this.findViewById<TextView>(R.id.message_tv)?.let { tv ->
                tv.text = message
            }
            show()
        }
    }

    protected fun showSuccessDialog(listener: SuccessDialogClickListener) {
        val dialog = Dialog(context!!)
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(false)
            setContentView(R.layout.dialog_success)
            this.findViewById<TextView>(R.id.okay_btn)?.setOnClickListener {
                dismiss()
                listener.onSuccessDialogButtonClick()
            }
            show()
        }
    }

    protected fun dismissDialog() {
        if (::mLoadingDialog.isInitialized) {
            if (mLoadingDialog.isShowing) {
                mLoadingDialog.dismiss()
            }
        }
    }

    interface SuccessDialogClickListener {
        fun onSuccessDialogButtonClick()
    }

}