package com.onelabs.composesample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
//                    Sample()
            WebViewWithGeolocation(url = "https://tlvboxchat-bndxdmh7fxbkg6a7.northeurope-01.azurewebsites.net/chat.html")
        }
    }
}


