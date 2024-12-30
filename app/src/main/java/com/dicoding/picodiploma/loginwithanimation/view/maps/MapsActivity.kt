package com.dicoding.picodiploma.loginwithanimation.view.maps
import android.content.ContentValues.TAG
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMapsBinding
import com.dicoding.picodiploma.loginwithanimation.utils.Result
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val boundsBuilder = LatLngBounds.Builder()
    private val viewModel: MapsViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private val markerClusters = mutableMapOf<LatLng, Int>()
    private val CLUSTER_RADIUS = 0.1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            binding = ActivityMapsBinding.inflate(layoutInflater)
            setContentView(binding.root)

            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)

            setupObservers()
        } catch (e: Exception) {
            Log.e("MapsActivity", "Error in onCreate", e)
        }
    }

    private fun setupObservers() {
        viewModel.getSession().observe(this) { user ->
            if (user.isLogin) {
                viewModel.getStoriesWithLocation(user.token)
            }
        }

        viewModel.storiesWithLocation.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    getStoriesWithLocation(result.data)
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getStoriesWithLocation(stories: List<ListStoryItem>) {
        stories.forEach { story ->
            val latLng = LatLng(story.lat!!, story.lon!!)

            updateCluster(latLng)

            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(story.name)
                    .snippet(story.description)
            )
            boundsBuilder.include(latLng)
        }

        val mostDenseCluster = markerClusters.maxByOrNull { it.value }

        if (mostDenseCluster != null) {
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    mostDenseCluster.key,
                    15f
                )
            )
        } else {
            val bounds = boundsBuilder.build()
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    resources.displayMetrics.widthPixels,
                    resources.displayMetrics.heightPixels,
                    300
                )
            )
        }
    }

    private fun updateCluster(latLng: LatLng) {
        var foundCluster = false

        for ((center, count) in markerClusters) {
            if (isWithinRadius(latLng, center)) {
                markerClusters[center] = count + 1
                foundCluster = true
                break
            }
        }

        if (!foundCluster) {
            markerClusters[latLng] = 1
        }
    }

    private fun isWithinRadius(point1: LatLng, point2: LatLng): Boolean {
        val results = FloatArray(1)
        Location.distanceBetween(
            point1.latitude, point1.longitude,
            point2.latitude, point2.longitude,
            results
        )
        return results[0] <= CLUSTER_RADIUS * 111000
    }

    override fun onMapReady(googleMap: GoogleMap) {
        try {
            mMap = googleMap

            mMap.uiSettings.isZoomControlsEnabled = true
            mMap.uiSettings.isIndoorLevelPickerEnabled = true
            mMap.uiSettings.isCompassEnabled = true
            mMap.uiSettings.isMapToolbarEnabled = true


        } catch (e: Exception) {
            Log.e("MapsActivity", "Error in onMapReady", e)
        }
        setMapStyle()
    }
    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }
    companion object {
        private const val TAG = "MapsActivity"
    }

}
