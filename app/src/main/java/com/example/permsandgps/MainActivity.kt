package com.example.permsandgps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.permsandgps.ui.theme.PermsAndGPSTheme
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class LatLon(val lat: Double, val lon: Double)

class LatLonViewModel: ViewModel() {
    var latLon = LatLon(51.05, -0.72)
        set(newValue) {
            field = newValue
            latLonLiveData.value = newValue
        }
    var latLonLiveData = MutableLiveData<LatLon>()
}


class MainActivity : ComponentActivity(), LocationListener {
    val viewModel : LatLonViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermissions()
        setContent { DisplayCoords() }
    }

fun checkPermissions() {
    val requiredPermission = Manifest.permission.ACCESS_FINE_LOCATION
    if(checkSelfPermission(requiredPermission) == PackageManager.PERMISSION_GRANTED) {
        startGPS()
    } else {
        val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if(isGranted) {
                startGPS()
            } else {
                Toast.makeText(this, "GPS permission not granted", Toast.LENGTH_LONG).show()
            }
        }
        permissionLauncher.launch(requiredPermission)
    }
}
@SuppressLint("MissingPermission")
fun startGPS() {
    val mgr = getSystemService(LOCATION_SERVICE) as LocationManager
    mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this )
}

override fun onLocationChanged(location: Location) {
    Toast.makeText(this, "Latitude: ${location.latitude}, Longitude: ${location.longitude}", Toast.LENGTH_LONG).show()
    viewModel.latLon = LatLon(location.latitude, location.longitude)
}

override fun onProviderEnabled(provider: String) {
    Toast.makeText(this, "GPS enabled", Toast.LENGTH_LONG).show()

}

override fun onProviderDisabled(provider: String) {
    Toast.makeText(this, "GPS disabled", Toast.LENGTH_LONG).show()
}

override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {


    }
    @Composable
    fun DisplayCoords(){
        viewModel.latLonLiveData.observe(this){}
        var obsLat = viewModel.latLonLiveData.value?.lat
        var obslon = viewModel.latLonLiveData.value?.lon
        Text("Latitude: $obsLat Longitude: $obslon")
    }
}