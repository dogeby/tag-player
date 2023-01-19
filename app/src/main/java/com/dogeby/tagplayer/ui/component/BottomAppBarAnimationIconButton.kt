package com.dogeby.tagplayer.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

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
