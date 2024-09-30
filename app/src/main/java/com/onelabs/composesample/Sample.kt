package com.onelabs.composesample

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.onelabs.composesample.ui.theme.ComposeSampleTheme

@Composable
fun Sample() {

    LaunchedEffect(Unit) {

    }

    val (count, setCount) = remember { mutableIntStateOf(0) }

    Column {
        Text(text = "You clicked $count times")
        Button(onClick = {
            setCount(count + 1)
        }) {
            Text("Click me")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SamplePreview()
{
    Sample()
}