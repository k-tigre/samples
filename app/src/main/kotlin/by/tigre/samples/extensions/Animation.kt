package by.tigre.samples.extensions

import android.view.View
import android.view.ViewGroup
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import by.tigre.samples.extensions.Animation.FAST_LAYOUT_ANIMATION_DURATION
import by.tigre.samples.extensions.Animation.LAYOUT_ANIMATION_DURATION

object Animation {
    const val LAYOUT_ANIMATION_DURATION = 300L
    const val FAST_LAYOUT_ANIMATION_DURATION = 150L
}

val actionViewGone: View.() -> Unit = { visibility = View.GONE }
val actionViewInvisible: View.() -> Unit = { visibility = View.INVISIBLE }
val actionDoNothing: View.() -> Unit = {}

fun View.fadeOut(duration: Long = FAST_LAYOUT_ANIMATION_DURATION, withEndAction: (View) -> Unit = actionViewGone) {
    if (isVisible()) {
        animate().cancel()
        animate()
            .setDuration(duration)
            .setInterpolator(FastOutLinearInInterpolator())
            .alpha(0f)
            .withEndAction { withEndAction(this) }
            .start()
    }
}

fun View.fadeIn(duration: Long = FAST_LAYOUT_ANIMATION_DURATION, withEndAction: (View) -> Unit = actionDoNothing) {
    animate().cancel()
    if (!isVisible()) {
        visibility = View.VISIBLE
        alpha = 0f
    }
    animate()
        .setDuration(duration)
        .setInterpolator(FastOutLinearInInterpolator())
        .alpha(1f)
        .withEndAction { withEndAction(this) }
        .start()
}

fun View.fadeOutWithTranslation(
    duration: Long = FAST_LAYOUT_ANIMATION_DURATION,
    translation: Float = 0f,
    withEndAction: (View) -> Unit = actionViewGone
) {
    if (isVisible()) {
        animate().cancel()
        animate()
            .setDuration(duration)
            .setInterpolator(FastOutLinearInInterpolator())
            .alpha(0f)
            .translationX(translation)
            .withEndAction { withEndAction(this) }
            .start()
    }
}

fun View.fadeInWithTranslation(
    duration: Long = FAST_LAYOUT_ANIMATION_DURATION,
    translation: Float = 0f,
    withEndAction: (View) -> Unit = actionDoNothing
) {
    animate().cancel()
    if (!isVisible()) {
        visibility = View.VISIBLE
        alpha = 0f
        translationX = translation
    }
    animate()
        .setDuration(duration)
        .setInterpolator(FastOutLinearInInterpolator())
        .alpha(1f)
        .translationX(0f)
        .withEndAction { withEndAction(this) }
        .start()
}

fun ViewGroup.removeWithFade(child: View, duration: Long = LAYOUT_ANIMATION_DURATION) =
    child.fadeOut(duration) { removeView(child) }

fun ViewGroup.addWithFade(child: View, duration: Long = LAYOUT_ANIMATION_DURATION) {
    addView(child)
    child.hide()
    child.fadeIn(duration)
}

fun ViewGroup.addWithFadeAndTranslation(child: View, duration: Long = LAYOUT_ANIMATION_DURATION) {
    addView(child)
    child.hide()
    child.fadeInWithTranslation(duration, translation = width.toFloat())
}

fun ViewGroup.removeWithFadeAndTranslation(child: View, duration: Long = LAYOUT_ANIMATION_DURATION) =
    child.fadeOutWithTranslation(duration, translation = width.toFloat()) { removeView(child) }
