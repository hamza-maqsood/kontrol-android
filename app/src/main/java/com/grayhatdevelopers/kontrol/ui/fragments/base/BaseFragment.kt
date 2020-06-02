package com.grayhatdevelopers.kontrol.ui.fragments.base

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.grayhatdevelopers.kontrol.R
import com.grayhatdevelopers.kontrol.di.kodeinViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import kotlin.coroutines.CoroutineContext

abstract class BaseFragment : Fragment(), KodeinAware, CoroutineScope {

    override val kodein: Kodein by closestKodein()

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    private val baseViewModel: BaseViewModel by kodeinViewModel()
    private lateinit var mLoadingDialog: Dialog


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        baseViewModel.navigationCommand.observe(requireActivity()) { command: NavigationCommand? ->
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

    protected fun navigateTo(direction: NavDirections, bundle: Bundle? = null) {
        baseViewModel.navigate(direction, bundle)
    }

    protected fun popBackToPrevious() {
        baseViewModel.popBack()
    }

    protected fun showLoadingDialog(message: String = "Please Wait") {
        mLoadingDialog = Dialog(requireContext())
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
        val dialog = Dialog(requireContext())
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