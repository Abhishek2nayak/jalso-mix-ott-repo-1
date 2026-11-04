package com.jalsomix.ott.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import java.io.File
import com.jalsomix.ott.data.UserSession
import coil.request.ImageRequest
import com.jalsomix.ott.R
import com.jalsomix.ott.data.Title
import com.jalsomix.ott.ui.theme.JM

@Composable
fun LogoMark(size: Dp, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.logo),
        contentDescription = null,
        modifier = modifier.size(size)
    )
}

/** The new circular brand logo – used ONLY in the Home header (and as the launcher icon). */
@Composable
fun LogoMarkNew(size: Dp, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.logo_new),
        contentDescription = null,
        modifier = modifier.size(size)
    )
}

/** Gold gradient wordmark: JALSOMIX */
@Composable
fun Wordmark(fontSize: Int = 22, modifier: Modifier = Modifier) {
    Text(
        text = "JALSOMIX",
        modifier = modifier,
        style = MaterialTheme.typography.titleLarge.copy(
            brush = JM.GoldGradient,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = (fontSize * 0.14f).sp
        )
    )
}

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier, trailing: String? = null) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            style = MaterialTheme.typography.headlineSmall,
            color = JM.Text,
            modifier = Modifier.weight(1f)
        )
        if (trailing != null) {
            Text(trailing, style = MaterialTheme.typography.labelLarge, color = JM.Gold)
        }
    }
}

@Composable
fun PremiumBadge(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(JM.GoldGradient)
            .padding(horizontal = 6.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Rounded.WorkspacePremium,
            contentDescription = stringResource(R.string.cd_premium),
            tint = JM.OnGold,
            modifier = Modifier.size(11.dp)
        )
        Spacer(Modifier.width(3.dp))
        Text(
            stringResource(R.string.tag_premium),
            style = MaterialTheme.typography.labelSmall,
            color = JM.OnGold
        )
    }
}

/** Poster with a seeded gradient behind it so the UI never looks empty while images load. */
@Composable
fun PosterArt(t: Title, url: String, modifier: Modifier = Modifier) {
    var loaded by remember(url) { mutableStateOf(false) }
    val c1 = Color.hsv((t.hue % 360).toFloat(), 0.70f, 0.60f)
    val c2 = Color.hsv(((t.hue + 40) % 360).toFloat(), 0.80f, 0.16f)
    Box(modifier.background(Brush.linearGradient(listOf(c1, c2)))) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(url).crossfade(300).build(),
            contentDescription = t.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize(),
            onSuccess = { loaded = true }
        )
        if (!loaded) {
            Text(
                t.name,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(10.dp),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun PosterCard(
    t: Title,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = Dp.Unspecified,
    aspect: Float = 0.68f,
    showTitle: Boolean = true
) {
    Column(
        modifier
            .then(if (width == Dp.Unspecified) Modifier.fillMaxWidth() else Modifier.width(width))
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(aspect)
                .clip(RoundedCornerShape(12.dp))
        ) {
            PosterArt(t, t.posterUrl, Modifier.fillMaxSize())
            Box(
                Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            0.55f to Color.Transparent,
                            1f to Color.Black.copy(alpha = 0.75f)
                        )
                    )
            )
            if (t.premium) {
                PremiumBadge(Modifier.padding(6.dp))
            }
            Text(
                "★ ${t.rating}",
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp),
                style = MaterialTheme.typography.labelSmall,
                color = JM.GoldLight
            )
        }
        if (showTitle) {
            Spacer(Modifier.height(6.dp))
            Text(
                t.name,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = JM.Text,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                t.genres.firstOrNull().orEmpty() + " • " + t.language,
                style = MaterialTheme.typography.bodySmall,
                color = JM.Muted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun GoldButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (enabled) JM.GoldGradient else Brush.horizontalGradient(listOf(JM.Outline, JM.Outline)))
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 22.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(icon, null, tint = JM.OnGold, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text,
            style = MaterialTheme.typography.titleMedium,
            color = if (enabled) JM.OnGold else JM.Muted,
            maxLines = 1
        )
    }
}

@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    Row(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.14f))
            .clickable { onClick() }
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(icon, null, tint = JM.Text, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text,
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 15.sp),
            color = JM.Text,
            maxLines = 1
        )
    }
}

@Composable
fun JmChip(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier = modifier
            .clip(shape)
            .then(
                if (selected) Modifier.background(JM.GoldGradient)
                else Modifier
                    .background(JM.SurfaceHigh)
                    .border(1.dp, JM.Outline, shape)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) JM.OnGold else JM.Text,
            maxLines = 1
        )
    }
}

@Composable
fun RoundIconButton(icon: ImageVector, contentDescription: String?, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.45f))
            .border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription, tint = JM.Text, modifier = Modifier.size(22.dp))
    }
}

/** Localised label for a stored content-language key ("Hindi" / "Gujarati"). */
@Composable
fun langLabel(lang: String): String = when (lang) {
    "Hindi" -> stringResource(R.string.content_hindi)
    "Gujarati" -> stringResource(R.string.content_gujarati)
    else -> lang
}

private val avatarBrushes = listOf(
    JM.FireGradient,
    Brush.linearGradient(listOf(JM.Blue, JM.Indigo)),
    Brush.linearGradient(listOf(JM.Green, JM.Teal)),
    Brush.linearGradient(listOf(JM.Purple, JM.Pink)),
    Brush.linearGradient(listOf(JM.GoldLight, JM.GoldDeep)),
    Brush.linearGradient(listOf(JM.Gray, JM.SurfaceHigh))
)
val avatarColorCount get() = avatarBrushes.size

fun avatarBrush(index: Int): Brush = avatarBrushes[index.coerceIn(0, avatarBrushes.lastIndex)]

/** Round profile picture: photo if set, otherwise the user's initial on a gradient. */
@Composable
fun Avatar(session: UserSession, size: Dp, modifier: Modifier = Modifier) {
    val photo = session.avatarPhoto.takeIf { it.isNotEmpty() && File(it).exists() }
    Box(
        modifier
            .size(size)
            .clip(CircleShape)
            .background(avatarBrush(session.avatarColor)),
        contentAlignment = Alignment.Center
    ) {
        if (photo != null) {
            AsyncImage(
                model = File(photo),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                session.name.firstOrNull()?.uppercase() ?: "J",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = (size.value * 0.42f).sp),
                color = Color.White
            )
        }
    }
}

/** iOS segmented control. */
@Composable
fun IosSegmented(options: List<String>, selected: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(JM.SurfaceHigh)
            .padding(2.dp)
    ) {
        options.forEachIndexed { i, label ->
            val on = i == selected
            Box(
                Modifier
                    .weight(1f)
                    .height(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (on) JM.Gray else Color.Transparent)
                    .clickable { onSelect(i) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp),
                    color = if (on) Color.White else JM.Text.copy(alpha = 0.85f),
                    maxLines = 1
                )
            }
        }
    }
}
