package com.grayhatdevelopers.kontrol.ui.fragments.dashboard

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.observe
import com.grayhatdevelopers.kontrol.R
import com.grayhatdevelopers.kontrol.databinding.FragmentDashboardBinding
import com.grayhatdevelopers.kontrol.di.kodeinViewModel
import com.grayhatdevelopers.kontrol.ui.activity.AppActivity
import com.grayhatdevelopers.kontrol.ui.dialogs.Dialog
import com.grayhatdevelopers.kontrol.ui.fragments.base.BaseFragment
import com.grayhatdevelopers.kontrol.utils.*
import com.grayhatdevelopers.kontrol.viewutils.OnSwipeTouchListener
import com.grayhatdevelopers.kontrol.viewutils.ViewWeightAnimationWrapper
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import kotlinx.android.synthetic.main.fragment_dashboard.*
import timber.log.Timber

class DashboardFragment : BaseFragment(), AppActivity.OnLocationEnabledListener {

    private val mViewModel: DashboardViewModel by kodeinViewModel()
    private lateinit var mapboxMap: MapboxMap

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
                    Timber.d("Downloading Tasks...")
                    // todo show loading at task counter
                }
                DashboardActions.DownloadClientsData -> {
                    Timber.d("Downloading Clients Data")
                    showLoadingDialog("Getting Data...")
                }
                DashboardActions.DismissDialog -> {
                    Timber.d("Dismissing Dialog")
                    dismissDialog()
                }
                DashboardActions.LoginSessionExpired -> {
                    Timber.d("Login Session Expired")
                    dismissDialog()
                    logoutRider()
                }
                DashboardActions.NetworkError -> {
                    Timber.d("Network Error")
                    context?.toast("Make sure you've got a stable internet connection!")
                    dismissDialog()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeGetTasksResults() {
        mViewModel.getTaskResults.observe(viewLifecycleOwner) {
            when (it) {
                is GetTaskResults.Failed -> {
                    Timber.d("GetTasksResult Failed: ${it.error}")
                    context?.toast(it.error)
                }

                is GetTaskResults.Succeeded -> {
                    Timber.d("Get Task Result Successful: $it")
                    // update the text view from
                    tasks_counter.apply {
                        text = "${it.done} / ${it.total}"
                    }
                    // todo here: move to view model
                    // hide the progress bar
                    tasks_pb.hide()
                }
            }
        }
    }

    private fun logoutRider() {
        Dialog(requireContext(), Dialog.DialogType.ERROR).apply {
            title = "Session Expired!"
            message = "Your login session has expired. \nLogin again."
            positiveButtonText = "Logout"
            setCancelable(false)
            setOnPositiveButtonClickedListener(object : Dialog.OnPositiveButtonClicked {
                override fun onPositiveButtonClicked() {
                    dismiss()
                    navigateTo(DashboardFragmentDirections.actionDashboardFragmentToLoginFragment())
                }
            })
            show()
        }
    }

    private fun observeDashboardNavigation() {
        mViewModel.dashboardNavigation.observe(viewLifecycleOwner) {
            when (it) {
                DashboardNavigation.Chat -> {
                    Timber.d("Opening Chat Fragment")
                    navigateTo(DashboardFragmentDirections.actionDashboardFragmentToChatFragment())
                }
                DashboardNavigation.ActiveTasks -> {
                    Timber.d("Opening Active Tasks Fragment")
                    navigateTo(DashboardFragmentDirections.actionDashboardFragmentToActiveTasksFragment())
                }
                DashboardNavigation.TasksHistory -> {
                    Timber.d("Opening Task History Fragment")
                    navigateTo(DashboardFragmentDirections.actionDashboardFragmentToTasksHistoryFragment())
                }
                DashboardNavigation.SignOut -> {
                    Timber.d("Signing Out User")
                    navigateTo(DashboardFragmentDirections.actionDashboardFragmentToLoginFragment())
                }
                is DashboardNavigation.CreateNewPayment -> {
                    Timber.d("Opening Create New Payment Fragment")
                    dismissDialog()
                    val bundle = bundleOf("clients" to it.clients)
                    navigateTo(
                        DashboardFragmentDirections.actionDashboardFragmentToCreatePaymentFragment(
                            it.clients
                        ), bundle
                    )
                }
            }
        }
    }

    private fun initMapView(savedInstanceState: Bundle?) {
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync { mapboxMap ->
            this.mapboxMap = mapboxMap
            var lastZoom = mapboxMap.cameraPosition.zoom
            mapboxMap.setStyle(Style.MAPBOX_STREETS) {
                // Map is set up and the style has loaded. Now you can add data or make other map adjustments.
                // todo add task location points
                // todo: uncomment code and make it work
//                AppActivity.bindLocationEnabledListener(this)
//                launch {
//                    withContext(Dispatchers.Main) {
//                        if (LocationHandler.checkIfLocationIsEnabled(requireActivity())) {
//                            enableLocationComponent(it)
//                            AppActivity.bindLocationEnabledListener(null)
//                        }
//                    }
//                }
            }
            // Map is set up and the style has loaded. Now you can add data or make other map adjustments
            mapView.addOnCameraDidChangeListener {
//                if (it) {
                val currentZoom = mapboxMap.cameraPosition.zoom
                if (currentZoom < lastZoom) {
                    // zoom in
//                        context?.toast("I'm zoom in")
                    Timber.d("ZoomZOom")
                }
                lastZoom = currentZoom
//                    Log.d("ZoomZoom", "Current zoom: $currentZoom -- lastZoom: $lastZoom")
            }
//            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) = runWithPermissions(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) {

        // Create and customize the LocationComponent's options
        val customLocationComponentOptions = LocationComponentOptions.builder(requireContext())
            .trackingGesturesManagement(true)
            .accuracyColor(ContextCompat.getColor(requireContext(), R.color.button_green))
            .build()

        val locationComponentActivationOptions =
            LocationComponentActivationOptions.builder(requireContext(), loadedMapStyle)
                .locationComponentOptions(customLocationComponentOptions)
                .build()

        // Get an instance of the LocationComponent and then adjust its settings
        mapboxMap.locationComponent.apply {

            // Activate the LocationComponent with options
            activateLocationComponent(locationComponentActivationOptions)

            // Enable to make the LocationComponent visible
            isLocationComponentEnabled = true

            // Set the LocationComponent's camera mode
            cameraMode = CameraMode.TRACKING

            // Set the LocationComponent's render mode
            renderMode = RenderMode.COMPASS
        }
    }

    private fun initUI() {
        options_container.setOnTouchListener(object :
            OnSwipeTouchListener(context = requireContext()) {
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

        val animationWrapper =
            ViewWeightAnimationWrapper(
                map_container
            )
        val animMap = ObjectAnimator.ofFloat(
            animationWrapper,
            "weight",
            animationWrapper.weight,
            if (left) 5.toFloat() else 2.toFloat()
        ).apply {
            duration = 50
            start()
        }

        val optionsWrapper =
            ViewWeightAnimationWrapper(
                options_container
            )
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

    override fun onLocationEnabledListener(enabled: Boolean) {
        if (enabled) {
            Timber.d("Location was enabled by the user")
            enableLocationComponent(mapboxMap.style!!)
        } else {
            Timber.d("Location was not enabled by the user")
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
}