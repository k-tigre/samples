package by.tigre.samples.presentation.base

import android.view.View
import android.view.ViewGroup
import by.tigre.samples.extensions.*
import io.reactivex.rxjava3.core.Observable

interface DestroyableView {
    fun destroy()

    enum class Animation { Fade, FadeAndTranslation, None }

    open class Impl(
        private val parent: ViewGroup,
        protected val view: View,
        private val inAnimation: Animation = Animation.FadeAndTranslation,
        private val outAnimation: Animation = Animation.FadeAndTranslation
    ) : DestroyableView {
        constructor(
            parent: ViewGroup,
            layoutId: Int,
            inAnimation: Animation = Animation.None,
            outAnimation: Animation = Animation.None
        ) : this(parent, parent.inflate(layoutId), inAnimation, outAnimation)

        init {
            when (inAnimation) {
                Animation.Fade -> parent.addWithFade(view)
                Animation.FadeAndTranslation -> parent.addWithFadeAndTranslation(view)
                Animation.None -> parent.addView(view)
            }
        }

        override fun destroy() {
            when (outAnimation) {
                Animation.Fade -> parent.removeWithFade(view)
                Animation.FadeAndTranslation -> parent.removeWithFadeAndTranslation(view)
                Animation.None -> parent.removeView(view)
            }
        }
    }
}

interface HandleBackView {
    val onBackClicked: Observable<Unit>
}
