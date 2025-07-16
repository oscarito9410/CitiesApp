package com.oscarp.citiesapp.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight

const val AnimationDurationMillis = 300
const val PercentTextTag = "PercentTextTag"

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedPercentText(percent: Int) {
    AnimatedContent(
        targetState = percent,
        label = "PercentChange",
        transitionSpec = {
            fadeIn(tween(AnimationDurationMillis)) togetherWith
                fadeOut(tween(AnimationDurationMillis))
        }
    ) { value ->
        Text(
            text = "$value%",
            modifier = Modifier.testTag(PercentTextTag),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
    }
}
