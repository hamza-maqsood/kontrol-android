package com.grayhatdevelopers.kontrol.ui.fragments.executetasks

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
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
import com.grayhatdevelopers.kontrol.di.kodeinViewModel
import com.grayhatdevelopers.kontrol.models.task.Task
import com.grayhatdevelopers.kontrol.ui.activity.AppActivity
import com.grayhatdevelopers.kontrol.ui.dialogs.Dialog
import com.grayhatdevelopers.kontrol.ui.fragments.base.BaseFragment
import com.grayhatdevelopers.kontrol.ui.fragments.base.LaunchIntentCommand
import com.grayhatdevelopers.kontrol.utils.MediaChooser
import com.grayhatdevelopers.kontrol.utils.sendSMS
import com.grayhatdevelopers.kontrol.utils.toast
import timber.log.Timber


open class ExecuteTaskFragment
    : BaseFragment(),
    AppActivity.OnMediaAddedListener {

    open val mViewModel: ExecutePaymentsBaseViewModel by kodeinViewModel()
    private lateinit var executeTaskFragmentBinding: FragmentExecuteTaskBinding
    private lateinit var taskToExecute: Task
    open lateinit var validationOptionsBottomSheet: BottomSheetBehavior<FrameLayout>
    open lateinit var otpVerificationBottomSheet: BottomSheetBehavior<FrameLayout>
    open lateinit var bottomSheetBehaviourCallback: BottomSheetBehavior.BottomSheetCallback

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
            viewModel = mViewModel
        }
        return executeTaskFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        taskToExecute = args.task
        mViewModel.task = args.task
        executeTaskFragmentBinding.task = taskToExecute
        validationOptionsBottomSheet = BottomSheetBehavior.from(executeTaskFragmentBinding.taskStandardBottomSheet)
        otpVerificationBottomSheet = BottomSheetBehavior.from(executeTaskFragmentBinding.taskVerificationStandardBottomSheet)
        setBottomSheetsStateChangeListener()
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
                    Timber.d("Received Amount Not Entered")
                    context?.toast("Enter received amount!")
                }
                ExecuteTaskErrors.NetworkError -> {
                    Timber.d("Make sure you've got a stable internet connection!")
                    hideVerificationBottomSheet()
                    context?.toast("Make sure you've got a stable internet connection")
                }
                ExecuteTaskErrors.MediaPermissionsNotGranted -> {
                    Timber.d("Media Permissions not granted")
                }
                ExecuteTaskErrors.VerificationMethodNotSelected -> {
                    Timber.d("VerificationMethodNotSelected")
                    context?.toast("Select a verification method")
                }
                ExecuteTaskErrors.InvalidTask -> {
                    Timber.d("Got BAD REQUEST response")
                    // hide that bottom sheet
                    hideVerificationBottomSheet()
                    showInvalidTaskErrorDialog()
                }
            }
        }
    }

    private fun showInvalidTaskErrorDialog() {
        Dialog(requireContext(), Dialog.DialogType.ERROR).apply {
            title = "Task Not Valid!"
            message = "There's some problem with this task. \nContact management to correct it!"
            positiveButtonText = "Dismiss"
            setCancelable(false)
            setOnPositiveButtonClickedListener(object : Dialog.OnPositiveButtonClicked {
                override fun onPositiveButtonClicked() {
                    dismiss()
                    popBackToPrevious()
                }
            })
            show()
        }
    }

    open fun hideVerificationBottomSheet() {
        removeDimEffect()
        otpVerificationBottomSheet.apply {
            isHideable = true
            state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun setBottomSheetsStateChangeListener() {
        bottomSheetBehaviourCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {
                Timber.d("Bottom sheet slided")
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    dimScreen()
                } else {
                    removeDimEffect()
                }
            }
        }

        validationOptionsBottomSheet.setBottomSheetCallback(bottomSheetBehaviourCallback)
        otpVerificationBottomSheet.isHideable = false
        otpVerificationBottomSheet.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {
                Timber.d("Bottom sheet slided")
            }

            @SuppressLint("SwitchIntDef")
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        dimScreen()
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                        otpVerificationBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        otpVerificationBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }
            }

        })
    }

    private fun dimScreen() {
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
    }

    private fun removeDimEffect() {
        executeTaskFragmentBinding.rootContainer.apply {
            overlay.clear()
            isClickable = true
            executeTaskFragmentBinding.includeLayout.apply {
                remarksEt.isClickable = true
            }
        }
    }

    private fun observeLaunchIntents() {
        mViewModel.launchIntentCommand.observe(viewLifecycleOwner) {
            when (it) {
                LaunchIntentCommand.SelectImage -> {
                    Timber.d("Launch Intent Gallery selected")
                    MediaChooser(requireActivity()).openAttachmentBottomSheet()
                }
            }
        }
    }

    private fun observeAttachmentPhotoStates() {

        mViewModel.attachmentPhotoState.observe(viewLifecycleOwner) {
            when (it!!) {
                AttachmentPhotoStates.NotSelected -> {
                    Timber.d("NO Photo Selected")
                }
                is AttachmentPhotoStates.Selected -> {
                    Timber.d("Photo Added")
                }
                AttachmentPhotoStates.Uploading -> {
                    Timber.d("Uploading Photo")
                }
                AttachmentPhotoStates.Uploaded -> {
                    Timber.d("Photo uploaded!")
                }
            }
        }
    }

    private fun observeExecuteTaskActions() {
        mViewModel.executeTaskActions.observe(viewLifecycleOwner) {
            when (it) {
                ExecuteTaskActions.ShowVerifyOptions -> {
                    Timber.d("Show Verify Options")
                    validationOptionsBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
                }
                ExecuteTaskActions.VerifyFromClient -> {
                    Timber.d("Verify From Client")
                }
                ExecuteTaskActions.VerifyFromAdmin -> {
                    Timber.d("Verify From Admin")
                }
                ExecuteTaskActions.SaveToDraft -> {
                    Timber.d("Save To Draft")
                }
                ExecuteTaskActions.GoBack -> {
                    Timber.d("Go back")
                    popBackToPrevious()
                }
                ExecuteTaskActions.VerificationSuccessful -> {
                    Timber.d("Verification was successful")
                    hideVerificationBottomSheet()
                    // dismiss the otp verification bottom sheet
                    otpVerificationBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                    // show success Dialog
                    showSuccessDialog(object : SuccessDialogClickListener {
                        override fun onSuccessDialogButtonClick() {
                            // go back to active tasks fragment
                            popBackToPrevious()
                        }
                    })
                }
                ExecuteTaskActions.Verifying -> {
                    Timber.d("Verification in process...")
                }
                is ExecuteTaskActions.SendSMS -> {
                    Timber.d("Sending SMS, Message: ${it.message}\n phoneNumber: ${it.phoneNumber}")
                    context?.sendSMS(it.message, it.phoneNumber)
                    // show the otp verification bottom sheet
                    otpVerificationBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }
    }

    override fun onMediaAdded(uri: Uri, mediaType: MediaChooser.MediaType) {
        Timber.d("On Photo Added: $uri")
    }

}