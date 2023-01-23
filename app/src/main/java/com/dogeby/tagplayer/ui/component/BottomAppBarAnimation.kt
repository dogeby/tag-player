package com.dogeby.tagplayer.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.dogeby.tagplayer.ui.theme.EmphasizedAccelerateEasing
import com.dogeby.tagplayer.ui.theme.EmphasizedDecelerateEasing
import com.dogeby.tagplayer.ui.theme.MediumDuration4
import com.dogeby.tagplayer.ui.theme.ShortDuration4
import com.dogeby.tagplayer.ui.theme.TagPlayerTheme

@Composable
fun BottomAppBarAnimation(
    shown: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    AnimatedVisibility(
        visible = shown,
        enter = slideInVertically(
            animationSpec = tween(
                durationMillis = MediumDuration4,
                easing = EmphasizedDecelerateEasing,
            ),
            initialOffsetY = { it },
        ) +
            expandVertically() +
            fadeIn(initialAlpha = 0.3f),
        exit = slideOutVertically(
            animationSpec = tween(
                durationMillis = ShortDuration4,
                easing = EmphasizedAccelerateEasing,
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
    delayMillis: Int = 0,
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
            tween(
                durationMillis = MediumDuration4,
                delayMillis = delayMillis,
                easing = EmphasizedDecelerateEasing,
            ),
            initialOffsetY = { it },
        )
    ) {
        IconButton(onClick = onClick) {
            Icon(painter = painterResource(id = iconResId), contentDescription = contentDescription)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomAppBarAnimationPreview() {
    TagPlayerTheme {
        BottomAppBarAnimation(shown = true) {
            BottomAppBar {}
        }
    }
}
