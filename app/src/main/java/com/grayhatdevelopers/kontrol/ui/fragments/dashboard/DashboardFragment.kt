package com.grayhatdevelopers.kontrol.ui.fragments.dashboard

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.lifecycle.observe
import com.grayhatdevelopers.kontrol.utils.remove
import com.grayhatdevelopers.kontrol.utils.show
import com.grayhatdevelopers.kontrol.databinding.FragmentDashboardBinding
import com.grayhatdevelopers.kontrol.ui.fragments.base.BaseFragment
import com.grayhatdevelopers.kontrol.utils.*
import com.mapbox.mapboxsdk.maps.Style
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DashboardFragment : BaseFragment() {

    private lateinit var mViewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentDashboardBinding.inflate(
            inflater,
            container,
            false
        ).apply {
            lifecycleOwner = this@DashboardFragment
            mViewModel = InjectorUtils.provideDashboardViewModel()
            viewModel = mViewModel
            rider = mViewModel.getRider()
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initMapView(savedInstanceState)
        initUI()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeDashboardNavigation()
        observeDashboardActions()
        observeGetTasksResults()
    }

    private fun observeDashboardActions() {
        mViewModel.dashboardActions.observe(viewLifecycleOwner) {
            when (it) {
                DashboardActions.DownloadTask -> {
                    Log.d(TAG, "Downloading Tasks...")
                    // todo show loading at task counter
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeGetTasksResults() {
        mViewModel.getTaskResults.observe(viewLifecycleOwner) { it ->
            when (it) {
                is GetTaskResults.Failed -> {
                    Log.d(TAG, "GetTasksResult Failed: ${it.error}")
                    context?.toast(it.error)
                }

                is GetTaskResults.Succeeded -> {
                    Log.d(TAG, "Get Task Result Successful: $it")
                    // update the text view from
                    tasks_counter.apply {
                        text = "${it.done} / ${it.total}"
                    }
                    // hide the progress bar
                    tasks_pb.hide()
                }
            }
        }
    }

    private fun observeDashboardNavigation() {
        mViewModel.dashboardNavigation.observe(viewLifecycleOwner) {
            when (it) {
                DashboardNavigation.Chat -> {
                    context?.toast("Coming soon...")
                    Log.d(TAG, "Opening Chat Fragment")
//                    baseViewModel.navigate(DashboardFragmentDirections.actionDashboardFragmentToChatFragment())
                }
                DashboardNavigation.ActiveTasks -> {
                    Log.d(TAG, "Opening Active Tasks Fragment")
                    baseViewModel.navigate(DashboardFragmentDirections.actionDashboardFragmentToActiveTasksFragment())
                }
                DashboardNavigation.CreateNewPayment -> {
                    Log.d(TAG, "Opening Create New Payment Fragment")
                    baseViewModel.navigate(DashboardFragmentDirections.actionDashboardFragmentToCreatePaymentFragment())
                }
                DashboardNavigation.TasksHistory -> {
                    Log.d(TAG, "Opening Task History Fragment")
//                    baseViewModel.navigate(DashboardFragmentDirections.actionDashboardFragmentToTasksHistoryFragment())
                    context?.toast("Coming soon...")
                }
                DashboardNavigation.SignOut -> {
                    Log.d(TAG, "Signing Out User")
                    baseViewModel.navigate(DashboardFragmentDirections.actionDashboardFragmentToLoginFragment())
                }
            }
        }
    }

    private fun initMapView(savedInstanceState: Bundle?) {
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync { mapboxMap ->
            var lastZoom = mapboxMap.cameraPosition.zoom
            mapboxMap.setStyle(Style.MAPBOX_STREETS) {
                // Map is set up and the style has loaded. Now you can add data or make other map adjustments.
                // todo add task location points
            }
            mapView.addOnCameraDidChangeListener {
//                if (it) {
                val currentZoom = mapboxMap.cameraPosition.zoom
                if (currentZoom < lastZoom) {
                    // zoom in
//                        context?.toast("I'm zoom in")
                    Log.d("ZoomZoom", "ZoomZOom")
                }
                lastZoom = currentZoom
//                    Log.d("ZoomZoom", "Current zoom: $currentZoom -- lastZoom: $lastZoom")
            }
//            }
        }
    }

    private fun initUI() {
        options_container.setOnTouchListener(object : OnSwipeTouchListener(context = context!!) {
            override fun onSwipeLeft() {
                animateShift(left = true)
            }

            override fun onSwipeRight() {
                animateShift(left = false)
            }
        })
    }

    private fun animateShift(left: Boolean) {

        mapView?.remove()

        val animationWrapper = ViewWeightAnimationWrapper(map_container)
        val animMap = ObjectAnimator.ofFloat(
            animationWrapper,
            "weight",
            animationWrapper.weight,
            if (left) 5.toFloat() else 2.toFloat()
        ).apply {
            duration = 50
            start()
        }

        val optionsWrapper = ViewWeightAnimationWrapper(options_container)
        val animOptions = ObjectAnimator.ofFloat(
            optionsWrapper,
            "weight",
            optionsWrapper.weight,
            if (left) 2.toFloat() else 5.toFloat()
        ).apply {
            duration = 50
            start()
        }

        if (left) toggleLabels(left = true)
        AnimatorSet().apply {
            interpolator = AccelerateDecelerateInterpolator()
            playTogether(animMap, animOptions)
            start()
        }.doOnEnd {
            if (!left) toggleLabels(left = false)
            mapView.show()
        }
    }

    private fun toggleLabels(left: Boolean) {
        new_task_tv.apply {
            text = if (left) AppConstants.HIDE_LABEL else AppConstants.CREATE_NEW_TASKS
        }
        active_tasks_tv.apply {
            text = if (left) AppConstants.HIDE_LABEL else AppConstants.ACTIVE_TASKS
        }
        task_history_tv.apply {
            text = if (left) AppConstants.HIDE_LABEL else AppConstants.TASK_HISTORY
        }
        chat_mode_tv.apply {
            text = if (left) AppConstants.HIDE_LABEL else AppConstants.CHAT_MODE
        }
        sign_out_tv.apply {
            text = if (left) AppConstants.HIDE_LABEL else AppConstants.SIGN_OUT
        }
    }

    // Add the mapView lifecycle to the activity's lifecycle methods
    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    companion object {
        private const val TAG = "DashboardFragment"
    }
}