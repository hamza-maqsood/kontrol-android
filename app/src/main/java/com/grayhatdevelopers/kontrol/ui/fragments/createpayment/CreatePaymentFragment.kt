package com.grayhatdevelopers.kontrol.ui.fragments.createpayment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroupOverlay
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.observe
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.grayhatdevelopers.kontrol.R
import com.grayhatdevelopers.kontrol.databinding.FragmentCreateNewPaymentBinding
import com.grayhatdevelopers.kontrol.extensions.setSpinnerEntries
import com.grayhatdevelopers.kontrol.ui.fragments.executetasks.ExecutePaymentsBaseViewModel
import com.grayhatdevelopers.kontrol.ui.fragments.executetasks.ExecuteTaskFragment
import com.grayhatdevelopers.kontrol.utils.InjectorUtils

@Suppress("LateinitVarOverridesLateinitVar")
class CreatePaymentFragment : ExecuteTaskFragment() {

    private lateinit var fragmentCreateNewPaymentBinding: FragmentCreateNewPaymentBinding
    override lateinit var mViewModel: ExecutePaymentsBaseViewModel
    override lateinit var bottomSheet: BottomSheetBehavior<FrameLayout>

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
            mViewModel = InjectorUtils.provideCreateNewPaymentViewModel()
            viewModel = mViewModel
        }
        return fragmentCreateNewPaymentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bottomSheet =
            BottomSheetBehavior.from(fragmentCreateNewPaymentBinding.paymentStandardBottomSheet)
        setBottomSheetStateChangeListener()
        initSpinners()
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

    // todo add an adapter to the select shop

    private fun setBottomSheetStateChangeListener() {
        bottomSheet.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
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

        })
    }

}