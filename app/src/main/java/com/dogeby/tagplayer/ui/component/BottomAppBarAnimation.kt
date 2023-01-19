package com.dogeby.tagplayer.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

@Composable
fun BottomAppBarAnimation(
    shown: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    AnimatedVisibility(
        visible = shown,
        enter = slideInVertically(
            animationSpec = tween(
                durationMillis = 250,
            ),
            initialOffsetY = { it },
        ) +
            expandVertically() +
            fadeIn(initialAlpha = 0.3f),
        exit = slideOutVertically(
            animationSpec = tween(
                durationMillis = 200,
            ),
            targetOffsetY = { it },
        ) +
            shrinkVertically() +
            fadeOut(),
        content = content
    )
}

@Composable
fun BottomAppBarAnimationIconButton(
    @DrawableRes iconResId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    isShowAnimation: Boolean = false,
    stiffness: Float = Spring.StiffnessMedium,
) {
    val transitionState = remember {
        MutableTransitionState(isShowAnimation.not()).apply {
            targetState = true
        }
    }

    AnimatedVisibility(
        modifier = modifier,
        visibleState = transitionState,
        enter = slideInVertically(
            animationSpec = spring(stiffness = stiffness),
            initialOffsetY = { it },
        )
    ) {
        IconButton(onClick = onClick) {
            Icon(painter = painterResource(id = iconResId), contentDescription = contentDescription)
        }
    }
}
