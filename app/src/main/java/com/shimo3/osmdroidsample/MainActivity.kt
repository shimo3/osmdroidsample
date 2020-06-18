package com.shimo3.osmdroidsample

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.PreferenceManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // permission
        checkPermission()

        // config
        Configuration.getInstance().let {
            it.load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))
        }

        setContentView(R.layout.activity_main)

        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.setBuiltInZoomControls(false)

        map.controller.setZoom(18.0)
        val center = GeoPoint(37.3985569,140.3884023)
        map.controller.setCenter(center)

        // map pin
        val marker = Marker(map)
        marker.position = center
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = "Koriyama-station"
        map.overlays.add(marker)

        val myhome = MyLocationNewOverlay(GpsMyLocationProvider(this), map)
        myhome.enableMyLocation()
        map.overlays.add(myhome)
        myhome.enableFollowLocation()

        btn.setOnClickListener {
//            if (myhome.isFollowLocationEnabled) {
            myhome.disableFollowLocation()
            myhome.enableFollowLocation()
//            }
        }
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    private fun checkPermission() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val requestPermissions = ArrayList<String>()

        // check
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions.add(permission)
            }
        }

        if (requestPermissions.size > 0) {
            // request-permission
//            @Suppress("UNCHECKED_CAST")
            ActivityCompat.requestPermissions(this, permissions.toList().toTypedArray(), 1)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder(this).apply {
                    title = "ダメです"
                    setMessage("権限を付与しないとダメでうs")
                    setIcon(R.drawable.ic_baseline_cancel_24)
                }.show()

                finish()
            }
        }
    }
}