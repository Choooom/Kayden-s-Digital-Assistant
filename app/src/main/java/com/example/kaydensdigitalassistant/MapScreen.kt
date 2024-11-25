package com.example.kaydensdigitalassistant

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.databinding.library.BuildConfig
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.kaydensdigitalassistant.data.CustomerLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.api.IGeoPoint
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.views.overlay.Polyline
import java.util.Locale

@Composable
fun MapScreen(customerId: Long) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val viewModel = LocalLocationViewModel.current
    var currentLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var customerMarker by remember { mutableStateOf<GeoPoint?>(null) }
    var distance by remember { mutableStateOf<Double?>(null) }
    var mapView by remember { mutableStateOf<MapView?>(null) }
    val defaultLocation = GeoPoint(14.5995, 120.9842) // Manila coordinates

    var routePoints by remember { mutableStateOf<List<GeoPoint>?>(null) }
    val roadManager = remember { RoadManager.buildRoadManager(context) }

    val geocoder = remember { Geocoder(context, Locale.getDefault()) }

    LaunchedEffect(mapView) {
        mapView?.controller?.setCenter(defaultLocation)
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView?.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView?.onPause()
                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (checkLocationPermission(context)) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0L,
                0f
            ) { location ->
                currentLocation = GeoPoint(location.latitude, location.longitude)
                mapView?.controller?.animateTo(currentLocation)
            }
        }
    }

    AndroidView(
        factory = { context ->
            MapView(context).apply {
                mapView = this
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(18.0)

                val zoomControls = CompassOverlay(context, this)
                zoomControls.enableCompass()
                overlays.add(zoomControls)

                val routeOverlay = Polyline(this).apply {
                    outlinePaint.color = Color.BLUE
                    outlinePaint.strokeWidth = 10f
                }
                overlays.add(routeOverlay)

                val myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), this)
                myLocationOverlay.enableMyLocation()
                myLocationOverlay.enableFollowLocation()
                overlays.add(myLocationOverlay)

                myLocationOverlay.runOnFirstFix {
                    val location = myLocationOverlay.myLocation
                    currentLocation = location
                    controller.animateTo(location)
                }

                val markers = MarkerOverlay(context)
                overlays.add(markers)

                // In MapScreen, update the onSingleTapConfirmed handler:
                overlays.add(object : Overlay() {
                    override fun onSingleTapConfirmed(e: MotionEvent, mapView: MapView): Boolean {
                        val point = projection.fromPixels(e.x.toInt(), e.y.toInt())

                        customerMarker = point as GeoPoint?
                        markers.clearMarkers()
                        markers.addMarker(point, mapView)

                        // Update the selected location in ViewModel
                        viewModel.updateLocation(point.latitude, point.longitude)

                        currentLocation?.let { current ->
                            scope.launch(Dispatchers.IO) {
                                try {
                                    val waypoints = ArrayList<GeoPoint>()
                                    waypoints.add(current)
                                    waypoints.add(point)
                                    val road = roadManager.getRoad(waypoints)

                                    withContext(Dispatchers.Main) {
                                        val routePoints = road.mRouteHigh
                                        routeOverlay.setPoints(routePoints)
                                        invalidate()

                                        distance = calculateDistance(
                                            current.latitude, current.longitude,
                                            point.latitude, point.longitude
                                        )
                                    }
                                } catch (e: Exception) {
                                    Log.e("MapScreen", "Route calculation failed", e)
                                }
                            }
                        }
                        return true
                    }
                })
            }
        },
        modifier = Modifier.fillMaxSize()
    )

    distance?.let { dist ->
        Text(
            text = "Distance: ${String.format("%.2f", dist)} km",
            modifier = Modifier.padding(16.dp)
        )
    }
}

private object RoadManager {
    fun buildRoadManager(context: Context): OSRMRoadManager {
        return OSRMRoadManager(context, "com.example.kaydensdigitalassistant").apply {
            setMean(OSRMRoadManager.MEAN_BY_CAR)
        }
    }
}


// Distance calculation function using Haversine formula
fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371 // Earth's radius in kilometers
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat/2) * sin(dLat/2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon/2) * sin(dLon/2)
    val c = 2 * atan2(sqrt(a), sqrt(1-a))
    return r * c
}

class MarkerOverlay(private val context: Context) : Overlay() {
    private val markers = mutableListOf<Marker>()

    fun clearMarkers() {
        markers.clear()
    }

    fun addMarker(point: IGeoPoint, mapView: MapView) {
        val marker = Marker(mapView).apply {
            position = point as GeoPoint
            icon = ContextCompat.getDrawable(context, R.drawable.map_marker)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }
        markers.add(marker)
    }

    override fun draw(canvas: Canvas, mapView: MapView, shadow: Boolean) {
        markers.forEach { it.draw(canvas, mapView, shadow) }
    }
}


private fun getCurrentLocation(context: Context, onLocation: (Location) -> Unit) {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    if (checkLocationPermission(context)) {
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0L,
            0f
        ) { location ->
            onLocation(location)
        }
    }
}

private fun checkLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}
