package com.grayhatdevelopers.kontrol.ui.fragments.executetasks

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroupOverlay
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.grayhatdevelopers.kontrol.R
import com.grayhatdevelopers.kontrol.databinding.FragmentExecuteTaskBinding
import com.grayhatdevelopers.kontrol.models.task.Task
import com.grayhatdevelopers.kontrol.ui.activity.AppActivity
import com.grayhatdevelopers.kontrol.ui.fragments.base.BaseFragment
import com.grayhatdevelopers.kontrol.ui.fragments.base.LaunchIntentCommand
import com.grayhatdevelopers.kontrol.utils.InjectorUtils
import com.grayhatdevelopers.kontrol.utils.PhotoChooser
import com.grayhatdevelopers.kontrol.utils.sendSMS
import com.grayhatdevelopers.kontrol.utils.toast


open class ExecuteTaskFragment : BaseFragment(), AppActivity.Companion.OnPhotoAddedListener {

    open lateinit var mViewModel: ExecutePaymentsBaseViewModel
    private lateinit var executeTaskFragmentBinding: FragmentExecuteTaskBinding
    private lateinit var taskToExecute: Task
    open lateinit var bottomSheet: BottomSheetBehavior<FrameLayout>

    private val args: ExecuteTaskFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        executeTaskFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_execute_task,
            container,
            false
        )
        executeTaskFragmentBinding.apply {
            lifecycleOwner = this@ExecuteTaskFragment
            mViewModel = InjectorUtils.provideCreateNewPaymentViewModel()
            viewModel = mViewModel
        }
        return executeTaskFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        taskToExecute = args.task
        mViewModel.task = args.task
        executeTaskFragmentBinding.task = taskToExecute
        bottomSheet = BottomSheetBehavior.from(executeTaskFragmentBinding.taskStandardBottomSheet)
        setBottomSheetStateChangeListener()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeExecuteTaskActions()
        observeAttachmentPhotoStates()
        observeLaunchIntents()
        observeExecuteTaskErrors()
    }

    private fun observeExecuteTaskErrors() {
        mViewModel.executeTaskErrors.observe(viewLifecycleOwner) {
            when (it) {
                ExecuteTaskErrors.ReceivedAmountNotEntered -> {
                    Log.d(TAG, "Received Amount Not Entered")
                    context?.toast("Enter received amount!")
                }
                ExecuteTaskErrors.NetworkError -> {
                    Log.d(TAG, "Make sure you've got a stable internet connection!")
                    dismissDialog()
                    context?.toast("Make sure you've got a stable internet connection")
                }
                ExecuteTaskErrors.MediaPermissionsNotGranted -> {
                    Log.d(TAG, "Media Permissions not granted")
                }
                ExecuteTaskErrors.VerificationMethodNotSelected -> {
                    Log.d(TAG, "VerificationMethodNotSelected")
                    context?.toast("Select a verification method")
                }
            }
        }
    }

    private fun setBottomSheetStateChangeListener() {
        bottomSheet.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {
                Log.d(TAG, "Bottom sheet slided")
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    executeTaskFragmentBinding.rootContainer.apply {
                        val dim: Drawable = ColorDrawable(Color.BLACK)
                        dim.setBounds(0, 0, width, height)
                        dim.alpha = (255 * 0.5.toFloat()).toInt()

                        val overlay: ViewGroupOverlay = overlay
                        overlay.add(dim)
                        isClickable = false
                    }
                    executeTaskFragmentBinding.includeLayout.apply {
                        remarksEt.isClickable = true
                    }
                } else {
                    executeTaskFragmentBinding.rootContainer.apply {
                        overlay.clear()
                        isClickable = true
                        executeTaskFragmentBinding.includeLayout.apply {
                            remarksEt.isClickable = true
                        }
                    }
                }
            }

        })
    }

    private fun observeLaunchIntents() {
        mViewModel.launchIntentCommand.observe(viewLifecycleOwner) {
            when (it) {
                LaunchIntentCommand.SelectImage -> {
                    Log.d(TAG, "Launch Intent Gallery selected")
                    PhotoChooser(activity!!).openAttachmentBottomSheet()
                }
            }
        }
    }

    private fun observeAttachmentPhotoStates() {

        mViewModel.attachmentPhotoState.observe(viewLifecycleOwner) {
            when (it!!) {
                AttachmentPhotoStates.NotSelected -> {
                    Log.d(TAG, "NO Photo Selected")
                }
                is AttachmentPhotoStates.Selected -> {
                    Log.d(TAG, "Photo Added")
                }
                AttachmentPhotoStates.Uploading -> {
                    Log.d(TAG, "Uploading Photo")
                }
                AttachmentPhotoStates.Uploaded -> {
                    Log.d(TAG, "Photo uploaded!")
                }
            }
        }
    }

    private fun observeExecuteTaskActions() {
        mViewModel.executeTaskActions.observe(viewLifecycleOwner) {
            when (it) {
                ExecuteTaskActions.ShowVerifyOptions -> {
                    Log.d(TAG, "Show Verify Options")
                    bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
                }
                ExecuteTaskActions.VerifyFromClient -> {
                    Log.d(TAG, "Verify From Client")
                }
                ExecuteTaskActions.VerifyFromAdmin -> {
                    Log.d(TAG, "Verify From Admin")
                }
                ExecuteTaskActions.SaveToDraft -> {
                    Log.d(TAG, "Save To Draft")
                }
                ExecuteTaskActions.GoBack -> {
                    Log.d(TAG, "Go back")
                    baseViewModel.popBack()
                }
                ExecuteTaskActions.VerificationSuccessful -> {
                    Log.d(TAG, "Verification was successful")
                    // dismiss the previous dialog
                    dismissDialog()
                    // show success Dialog
                    showSuccessDialog(object : SuccessDialogClickListener {
                        override fun onSuccessDialogButtonClick() {
                            baseViewModel.popBack()
                        }
                    })
                }
                ExecuteTaskActions.Verifying -> {
                    Log.d(TAG, "Verification in process...")
                    showLoadingDialog(message = "Please Wait")
                }
                is ExecuteTaskActions.SendSMS -> {
                    Log.d(
                        TAG,
                        "Sending SMS, Message: ${it.message}\n phoneNumber: ${it.phoneNumber}"
                    )
                    context?.sendSMS(it.message, it.phoneNumber)
                    showLoadingDialog(message = "Waiting for Response...")
                }
            }
        }
    }

    override fun onPhotoAdded(uri: Uri) {
        Log.d(TAG, "On Photo Added: $uri")
    }

    companion object {
        private const val TAG = "ExecuteTaskFragment"
    }

}