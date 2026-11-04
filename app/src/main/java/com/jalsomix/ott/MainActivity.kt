package com.jalsomix.ott

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.jalsomix.ott.ui.JalsomixRoot
import com.jalsomix.ott.ui.theme.JalsomixTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JalsomixTheme {
                JalsomixRoot()
            }
        }
    }
}
