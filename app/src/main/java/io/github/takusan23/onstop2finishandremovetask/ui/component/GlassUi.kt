package io.github.takusan23.onstop2finishandremovetask.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.takusan23.onstop2finishandremovetask.R

/** Static gradients and translucent surfaces: no live blur or continuous animation. */
@Composable
fun GlassBackdrop(content: @Composable BoxScope.() -> Unit) {
    val colors = MaterialTheme.colorScheme
    CompositionLocalProvider(LocalContentColor provides colors.onSurface) {
        Box(Modifier.fillMaxSize().background(colors.background).drawWithCache {
            val blueCenter = Offset(size.width * .9f, size.height * .12f)
            val tealCenter = Offset(0f, size.height * .6f)
            val blueRadius = size.width * .9f
            val tealRadius = size.width * .85f
            val blue = Brush.radialGradient(listOf(colors.primary.copy(alpha = .13f), Color.Transparent), blueCenter, blueRadius)
            val teal = Brush.radialGradient(listOf(colors.secondary.copy(alpha = .10f), Color.Transparent), tealCenter, tealRadius)
            onDrawBehind {
                drawCircle(blue, blueRadius, blueCenter)
                drawCircle(teal, tealRadius, tealCenter)
            }
        }, content = content)
    }
}

@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val tint by animateColorAsState(
        if (highlighted) colors.primaryContainer.copy(alpha = .90f) else colors.surface.copy(alpha = .86f),
        tween(180), label = "glassTint"
    )
    val shape = RoundedCornerShape(24.dp)
    Surface(
        modifier = modifier.fillMaxWidth(), shape = shape, color = tint, contentColor = colors.onSurface,
        border = BorderStroke(1.dp, Brush.linearGradient(listOf(
            colors.outlineVariant.copy(alpha = .9f), colors.surface.copy(alpha = .2f), colors.outlineVariant.copy(alpha = .65f)
        )))
    ) {
        Column(Modifier.clip(shape).background(Brush.linearGradient(listOf(Color.White.copy(alpha = .045f), Color.Transparent)))
            .padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
    }
}

@Composable
fun TimerMark(modifier: Modifier = Modifier) {
    val primary = MaterialTheme.colorScheme.primary
    Canvas(modifier.size(40.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        drawCircle(primary.copy(alpha = .12f))
        drawCircle(primary, radius = size.minDimension * .27f, style = Stroke(2.dp.toPx()))
        drawLine(primary, center, center + Offset(0f, -size.height * .16f), 2.dp.toPx(), StrokeCap.Round)
        drawLine(primary, center, center + Offset(size.width * .13f, size.height * .07f), 2.dp.toPx(), StrokeCap.Round)
    }
}

@Composable
fun GlassTopBar(title: String, subtitle: String? = null, onBack: (() -> Unit)? = null, actions: @Composable RowScope.() -> Unit = {}) {
    Row(
        Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.statusBars).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (onBack != null) IconButton(onClick = onBack) {
            Icon(painterResource(R.drawable.back_24px), contentDescription = stringResource(R.string.glass_back))
        } else TimerMark()
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.headlineSmall)
            if (subtitle != null) Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        actions()
    }
}
