package com.onelabs.composesample

import android.Manifest
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat


@SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
@Composable
fun WebViewWithGeolocation(url: String) {
    var hasLocationPermission by remember { mutableStateOf(false) }
    val androidContext = LocalContext.current

    // Request location permission
    val requestLocationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // 3
        hasLocationPermission = isGranted
    }

//    LaunchedEffect(Unit) {
//        // 2
//        if (ContextCompat.checkSelfPermission(
//                androidContext,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            hasLocationPermission = true
//        } else {
//            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
//        }
//    }

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
                                    ) == PackageManager.PERMISSION_GRANTED )
                            {
                                callback?.invoke(origin, true, false)
                            }
                            else {
                                // 2
                                // Trigger the permission request
                                requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            }
                        }

                        //                    override fun onGeolocationPermissionsHidePrompt() {
                        //                        // 3a
                        //                        reload()
                        //                    }
                    }

                    val noCacheHeaders: MutableMap<String, String> = HashMap(2)
                    noCacheHeaders["Pragma"] = "no-cache"
                    noCacheHeaders["Cache-Control"] = "no-cache"
                    loadUrl(url, noCacheHeaders)
                    evaluateJavascript(
                        """"
                        receiveAccessToken('at')
                     """
                    ) { result ->
                        Log.d("WebView", result)
                    }
                }
            },
            update = { webView ->
                if (hasLocationPermission) {
                    // 4
                    //webView.reload() // Reload if permission is granted
                    webView.clearCache(true)
                    webView.clearHistory()
//                    val noCacheHeaders: MutableMap<String, String> = HashMap(2)
//                    noCacheHeaders["Pragma"] = "no-cache"
//                    noCacheHeaders["Cache-Control"] = "no-cache"
                    webView.loadUrl(url) //, noCacheHeaders)
                }
            }
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