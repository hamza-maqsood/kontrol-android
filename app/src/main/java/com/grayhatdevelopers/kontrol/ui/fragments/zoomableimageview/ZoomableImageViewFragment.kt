package com.grayhatdevelopers.kontrol.ui.fragments.zoomableimageview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import com.grayhatdevelopers.kontrol.R
import com.grayhatdevelopers.kontrol.databinding.FragmentZoomableImageViewBinding
import com.grayhatdevelopers.kontrol.di.kodeinViewModel
import com.grayhatdevelopers.kontrol.ui.fragments.base.BaseFragment
import timber.log.Timber

class ZoomableImageViewFragment : BaseFragment() {

    private lateinit var fragmentZoomableImageViewBinding: FragmentZoomableImageViewBinding
    private val mViewModel: ZoomableImageViewViewModel by kodeinViewModel()

    private val args: ZoomableImageViewFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentZoomableImageViewBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_zoomable_image_view,
            container,
            false
        )
        fragmentZoomableImageViewBinding.apply {
            lifecycleOwner = this@ZoomableImageViewFragment
            viewModel = mViewModel
            mViewModel.initImage(args.imageURI)
        }
        return fragmentZoomableImageViewBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeZoomableImageViewActions()
    }

    private fun observeZoomableImageViewActions() {
        mViewModel.zoomableImageViewActions.observe(viewLifecycleOwner) {
            when (it) {
                ZoomableImageViewActions.GoBack -> {
                    Timber.d("Going back")
                    popBackToPrevious()
                }
            }
        }
    }
}