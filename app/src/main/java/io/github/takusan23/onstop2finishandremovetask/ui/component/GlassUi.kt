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
import androidx.compose.ui.text.style.TextOverflow
import io.github.takusan23.onstop2finishandremovetask.R

/** Static gradients and translucent surfaces: no live blur or continuous animation. */
@Composable
fun GlassBackdrop(content: @Composable BoxScope.() -> Unit) {
    val colors = MaterialTheme.colorScheme
    CompositionLocalProvider(LocalContentColor provides colors.onSurface) {
        Box(Modifier.fillMaxSize().background(colors.background).drawWithCache {
            val topLight = Offset(size.width * .90f, size.height * .06f)
            val sideLight = Offset(size.width * -.10f, size.height * .46f)
            val bottomLight = Offset(size.width * .92f, size.height * .93f)
            val radius = size.width * 1.05f
            val blue = Brush.radialGradient(listOf(colors.primary.copy(alpha = .20f), Color.Transparent), topLight, radius)
            val aqua = Brush.radialGradient(listOf(colors.secondary.copy(alpha = .16f), Color.Transparent), sideLight, radius)
            val violet = Brush.radialGradient(listOf(colors.tertiary.copy(alpha = .12f), Color.Transparent), bottomLight, radius)
            onDrawBehind {
                drawCircle(blue, radius, topLight)
                drawCircle(aqua, radius, sideLight)
                drawCircle(violet, radius, bottomLight)
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
        if (highlighted) colors.primaryContainer.copy(alpha = .88f) else colors.surface.copy(alpha = .78f),
        tween(180), label = "glassTint"
    )
    val shape = RoundedCornerShape(24.dp)
    Surface(
        modifier = modifier.fillMaxWidth(), shape = shape, color = tint, contentColor = colors.onSurface,
        border = BorderStroke(1.dp, Brush.linearGradient(listOf(
            colors.surfaceBright.copy(alpha = .95f), colors.outlineVariant.copy(alpha = .55f), colors.primary.copy(alpha = .20f)
        )))
    ) {
        Column(Modifier.clip(shape).background(Brush.verticalGradient(listOf(
            colors.surfaceBright.copy(alpha = .14f), Color.Transparent, colors.primary.copy(alpha = .025f)
        )))
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
    Surface(
        modifier = Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.statusBars)
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = .72f),
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = .55f))
    ) {
        Row(
            Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(
                MaterialTheme.colorScheme.surfaceBright.copy(alpha = .15f), Color.Transparent
            ))).padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (onBack != null) IconButton(onClick = onBack) {
                Icon(painterResource(R.drawable.back_24px), contentDescription = stringResource(R.string.glass_back))
            } else TimerMark()
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleLarge, maxLines = 1)
                if (subtitle != null) Text(subtitle, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            actions()
        }
    }
}
