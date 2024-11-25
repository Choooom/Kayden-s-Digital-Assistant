package com.example.kaydensdigitalassistant

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.compass.CompassOverlay

@Composable
fun CustomerLocationViewScreen(
    navController: NavController,
    customerId: Long
) {
    val viewModel = LocalLocationViewModel.current
    val customerLocation by viewModel.getCustomerLocation(customerId).collectAsState(initial = null)
    var mapView by remember { mutableStateOf<MapView?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            customerLocation?.let { location ->
                AndroidView(
                    factory = { context ->
                        MapView(context).apply {
                            mapView = this
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)
                            controller.setZoom(18.0)

                            val customerPoint = GeoPoint(location.latitude, location.longitude)
                            controller.setCenter(customerPoint)

                            val markers = MarkerOverlay(context)
                            markers.addMarker(customerPoint, this)
                            overlays.add(markers)

                            val compassOverlay = CompassOverlay(context, this)
                            compassOverlay.enableCompass()
                            overlays.add(compassOverlay)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            IconButton(
                onClick = { navController.navigateUp() },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(Icons.Default.ArrowBack, "Back")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            customerLocation?.let { location ->
                Text(
                    text = "Customer Location: ${location.latitude}, ${location.longitude}",
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}
