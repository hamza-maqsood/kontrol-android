package com.grayhatdevelopers.kontrol.ui.fragments.createpayment

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroupOverlay
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.grayhatdevelopers.kontrol.R
import com.grayhatdevelopers.kontrol.databinding.FragmentCreateNewPaymentBinding
import com.grayhatdevelopers.kontrol.di.kodeinViewModel
import com.grayhatdevelopers.kontrol.extensions.setSpinnerEntries
import com.grayhatdevelopers.kontrol.ui.fragments.executetasks.ExecutePaymentsBaseViewModel
import com.grayhatdevelopers.kontrol.ui.fragments.executetasks.ExecuteTaskFragment
import timber.log.Timber

@Suppress("LateinitVarOverridesLateinitVar")
class CreatePaymentFragment : ExecuteTaskFragment() {

    private lateinit var fragmentCreateNewPaymentBinding: FragmentCreateNewPaymentBinding
    override val mViewModel: ExecutePaymentsBaseViewModel by kodeinViewModel()
    override lateinit var validationOptionsBottomSheet: BottomSheetBehavior<FrameLayout>
    override lateinit var otpVerificationBottomSheet: BottomSheetBehavior<FrameLayout>
    override lateinit var bottomSheetBehaviourCallback: BottomSheetBehavior.BottomSheetCallback

    private val args: CreatePaymentFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentCreateNewPaymentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_create_new_payment,
            container,
            false
        )
        fragmentCreateNewPaymentBinding.apply {
            lifecycleOwner = this@CreatePaymentFragment
            viewModel = mViewModel
        }
        return fragmentCreateNewPaymentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        validationOptionsBottomSheet =
            BottomSheetBehavior.from(fragmentCreateNewPaymentBinding.paymentStandardBottomSheet)
        otpVerificationBottomSheet =
            BottomSheetBehavior.from(fragmentCreateNewPaymentBinding.paymentVerificationStandardBottomSheet)
        setBottomSheetStateChangeListener()
        setBottomSheetsStateChangeListener()
        initSpinners()
        initShopSelector()
    }

    private fun initShopSelector() {
        val shops: MutableList<String> = mutableListOf()
        args.clients.apply {
            forEach {
                shops.add(it.name)
            }
        }
        fragmentCreateNewPaymentBinding.autoCompleteTextView.apply {
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                shops
            )
            this.setAdapter(adapter)
            this.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, position, _ ->
                    // automatically set the balance of the client when selected from the list
                    val balance = args.clients[position].balance.toString()
                    Timber.d("Balance for client: $balance")
                    fragmentCreateNewPaymentBinding.debitEt.setText(balance)
                }
        }
    }

    private fun initSpinners() {
        mViewModel.taskModels.observe(viewLifecycleOwner) {
            it?.let {
                fragmentCreateNewPaymentBinding.taskModelsSpinner.setSpinnerEntries(it)
            }
        }
        mViewModel.taskTypes.observe(viewLifecycleOwner) {
            it?.let {
                fragmentCreateNewPaymentBinding.taskTypesSpinner.setSpinnerEntries(it)
            }
        }
    }

    private fun dimScreen() {
        fragmentCreateNewPaymentBinding.rootContainer.apply {
            val dim: Drawable = ColorDrawable(Color.BLACK)
            dim.setBounds(0, 0, width, height)
            dim.alpha = (255 * 0.5.toFloat()).toInt()

            val overlay: ViewGroupOverlay = overlay
            overlay.add(dim)
            isClickable = false
        }
        fragmentCreateNewPaymentBinding.includeLayout.apply {
            remarksEt.isClickable = true
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

    private fun removeDimEffect() {
        fragmentCreateNewPaymentBinding.rootContainer.apply {
            overlay.clear()
            isClickable = true
            fragmentCreateNewPaymentBinding.includeLayout.apply {
                remarksEt.isClickable = true
            }
        }
    }


    override fun hideVerificationBottomSheet() {
        removeDimEffect()
        otpVerificationBottomSheet.apply {
            isHideable = true
            state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun setBottomSheetStateChangeListener() {
            bottomSheetBehaviourCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    fragmentCreateNewPaymentBinding.rootContainer.apply {
                        val dim: Drawable = ColorDrawable(Color.BLACK)
                        dim.setBounds(0, 0, width, height)
                        dim.alpha = (255 * 0.5.toFloat()).toInt()

                        val overlay: ViewGroupOverlay = overlay
                        overlay.add(dim)
                        isClickable = false
                    }
                    fragmentCreateNewPaymentBinding.includeLayout.apply {
                        remarksEt.isClickable = true
                    }
                } else {
                    fragmentCreateNewPaymentBinding.rootContainer.apply {
                        overlay.clear()
                        isClickable = true
                        fragmentCreateNewPaymentBinding.includeLayout.apply {
                            remarksEt.isClickable = true
                        }
                    }
                }
            }
        }
        validationOptionsBottomSheet.setBottomSheetCallback(bottomSheetBehaviourCallback)
        otpVerificationBottomSheet.setBottomSheetCallback(bottomSheetBehaviourCallback)
    }

}