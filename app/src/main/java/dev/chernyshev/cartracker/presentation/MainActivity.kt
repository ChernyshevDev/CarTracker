package dev.chernyshev.cartracker.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import dagger.android.AndroidInjection
import dev.chernyshev.cartracker.R

class MainActivity : AppCompatActivity() {
    private var onPermissionGranted: (() -> Unit)? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_main)
    }

    fun requestPermission(permission: String, onGranted: () -> Unit) {
        onPermissionGranted = onGranted
        ActivityCompat.requestPermissions(
            this,
            arrayOf(permission),
            1
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onPermissionGranted?.invoke()
    }
}