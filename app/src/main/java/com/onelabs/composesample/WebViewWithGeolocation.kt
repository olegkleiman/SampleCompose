package com.onelabs.composesample

import android.Manifest
import android.net.Uri
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.GeolocationPermissions
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat

object GeoJSCallback {
    var geoCallback : GeolocationPermissions.Callback? = null
    var geoOrigin: String? = null
}

@SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
@Composable
fun WebViewWithGeolocation(url: String) {
    var hasLocationPermission by remember { mutableStateOf(false) }
    val androidContext = LocalContext.current

    // Request location permission
    val requestLocationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // 2
        hasLocationPermission = isGranted
        GeoJSCallback.geoCallback?.invoke(GeoJSCallback.geoOrigin, true, false)
    }

    Column {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.cacheMode = WebSettings.LOAD_NO_CACHE
                    settings.javaScriptCanOpenWindowsAutomatically = true
                    settings.setGeolocationEnabled(true)

                    webChromeClient = object : WebChromeClient() {

                        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                            consoleMessage?.message()?.let {
                                Log.d("WebView", it)
                            }
                            return true
                        }

                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            super.onProgressChanged(view, newProgress)
                        }

                        override fun onGeolocationPermissionsShowPrompt(
                            origin: String?,
                            callback: GeolocationPermissions.Callback?
                        ) {
                            // 1
                            if (ContextCompat.checkSelfPermission(
                                    androidContext,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                callback?.invoke(origin, true, false)
                            }
                            else {
                                // Store the callback and origin in static variables to be used later
                                GeoJSCallback.geoCallback = callback
                                GeoJSCallback.geoOrigin = origin
                                // Trigger the permission request
                                requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            }
                        }
                    }

                    loadUrl(url)
                    evaluateJavascript(
                        """"
                        receiveAccessToken('at')
                     """
                    ) { result ->
                        Log.d("WebView", result)
                    }
                }
            },

        )
        Button(onClick = {
            //webView.reload()
        }) {
            Text("Speak")
        }

    }
}

@Preview(showBackground = true)
@Composable
fun WebViewComponentPreview()
{
    WebViewWithGeolocation("https://tlvboxchat-bndxdmh7fxbkg6a7.northeurope-01.azurewebsites.net/chat.html")
}