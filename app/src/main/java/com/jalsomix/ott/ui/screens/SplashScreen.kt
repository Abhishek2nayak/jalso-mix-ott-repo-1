package com.jalsomix.ott.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jalsomix.ott.R
import com.jalsomix.ott.data.AppState
import com.jalsomix.ott.ui.LogoMark
import com.jalsomix.ott.ui.Routes
import com.jalsomix.ott.ui.theme.JM
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * Premium OTT intro: logo emerges from darkness with a slow push-in, a golden light bloom,
 * a light sweep that glides across the logo, drifting gold sparkles, and the wordmark
 * "tracking in" (letters glide together) — like a streaming-service sting.
 */
@Composable
fun SplashScreen(state: AppState, onDone: (String) -> Unit) {
    val logoAlpha = remember { Animatable(0f) }
    val logoScale = remember { Animatable(0.78f) }
    val bloom = remember { Animatable(0f) }
    val shine = remember { Animatable(0f) }
    val wordAlpha = remember { Animatable(0f) }
    val wordSpacing = remember { Animatable(26f) }
    val tagAlpha = remember { Animatable(0f) }
    var minTimeDone by remember { mutableStateOf(false) }

    val inf = rememberInfiniteTransition(label = "sparkles")
    val t by inf.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Restart),
        label = "t"
    )
    // Fixed pseudo-random sparkle field: x, phase, speed, size
    val sparkles = remember {
        List(34) { i ->
            val a = ((i * 7919) % 1000) / 1000f
            val b = ((i * 104729) % 997) / 997f
            val c = ((i * 15485863) % 991) / 991f
            floatArrayOf(a, b, 0.4f + c * 0.9f, 1.2f + c * 2.4f)
        }
    }

    LaunchedEffect(Unit) {
        launch { logoAlpha.animateTo(1f, tween(1000, easing = FastOutSlowInEasing)) }
        launch { logoScale.animateTo(1.04f, tween(3000, easing = CubicBezierEasing(0.15f, 0.85f, 0.2f, 1f))) }
        launch {
            bloom.animateTo(1f, tween(1200, easing = FastOutSlowInEasing))
            bloom.animateTo(0.6f, tween(1400, easing = LinearEasing))
        }
        launch {
            delay(900)
            shine.animateTo(1f, tween(1100, easing = FastOutSlowInEasing))
        }
        launch {
            delay(1300)
            launch { wordAlpha.animateTo(1f, tween(900)) }
            wordSpacing.animateTo(7f, tween(1300, easing = FastOutSlowInEasing))
        }
        launch {
            delay(2100)
            tagAlpha.animateTo(1f, tween(700))
        }
        delay(3200)
        minTimeDone = true
    }

    LaunchedEffect(state.loaded, minTimeDone) {
        if (state.loaded && minTimeDone) {
            onDone(
                when {
                    !state.languageChosen -> Routes.LANG_ONBOARD
                    !state.session.loggedIn -> Routes.AUTH
                    else -> Routes.MAIN
                }
            )
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // golden light bloom
        Box(
            Modifier
                .size(560.dp)
                .alpha(bloom.value)
                .background(
                    Brush.radialGradient(
                        listOf(JM.GoldDeep.copy(alpha = 0.36f), JM.Ember.copy(alpha = 0.10f), Color.Transparent)
                    )
                )
        )

        // drifting gold sparkles
        Canvas(Modifier.fillMaxSize()) {
            sparkles.forEach { s ->
                val prog = (t * s[2] + s[1]) % 1f
                val x = s[0] * size.width + sin((prog + s[0]) * 6.283f) * 14.dp.toPx()
                val y = size.height * (1.05f - prog * 1.1f)
                val twinkle = 0.35f + 0.65f * sin((t * 6.283f * 2f) + s[1] * 9f).let { it * it }
                drawCircle(
                    color = JM.GoldLight.copy(alpha = (twinkle * 0.7f * logoAlpha.value).coerceIn(0f, 1f)),
                    radius = s[3].dp.toPx(),
                    center = Offset(x, y)
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // logo with a light sweep clipped to the logo's own pixels
            LogoMark(
                size = 210.dp,
                modifier = Modifier
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value)
                    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                    .drawWithContent {
                        drawContent()
                        val w = size.width
                        val x = -w * 0.6f + shine.value * w * 2.2f
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(Color.Transparent, Color.White.copy(alpha = 0.75f), Color.Transparent),
                                start = Offset(x, 0f),
                                end = Offset(x + w * 0.55f, size.height * 0.35f)
                            ),
                            blendMode = BlendMode.SrcAtop
                        )
                    }
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "JALSOMIX",
                modifier = Modifier.alpha(wordAlpha.value),
                style = MaterialTheme.typography.displaySmall.copy(
                    brush = JM.GoldGradient,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = wordSpacing.value.sp
                )
            )
            Text(
                stringResource(R.string.splash_tagline),
                modifier = Modifier
                    .alpha(tagAlpha.value)
                    .padding(top = 10.dp),
                style = MaterialTheme.typography.bodySmall.copy(letterSpacing = 1.sp),
                color = JM.Muted
            )
        }
    }
}
