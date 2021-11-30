package by.tigre.samples.presentation.activity.base

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import by.tigre.samples.Application
import by.tigre.samples.R
import by.tigre.samples.di.ApplicationGraph
import by.tigre.samples.extensions.MainDisposable
import by.tigre.samples.extensions.find
import by.tigre.samples.extensions.inflate
import by.tigre.samples.presentation.ActivityHolder
import by.tigre.samples.presentation.base.BasePresenter
import io.reactivex.rxjava3.subjects.PublishSubject

abstract class BaseActivity(private val layout: Int) : AppCompatActivity() {

    private val systemBackPressedEmitter = PublishSubject.create<Unit>()
    private val rootDisposable = MainDisposable()

    private val activityHolder: ActivityHolder by lazy {
        ActivityHolder.Impl(
            this,
            systemBackPressedEmitter
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val graph = (applicationContext as Application).graph

        setContentView(R.layout.screen_base_container)
        val screenView =
            find<ViewGroup>(R.id.screen_container).also { it.inflate(layout, attachToRoot = true) }

        setupScreen(screenView, savedInstanceState, graph)
    }

    private fun setupScreen(
        screenView: ViewGroup,
        savedInstanceState: Bundle?,
        graph: ApplicationGraph
    ) {
        onInit(screenView, savedInstanceState, activityHolder, graph).let { model ->
            if (model != null) {
                rootDisposable += model
            } else {
                finish()
            }
        }
    }

    abstract fun onInit(
        screenView: ViewGroup,
        savedInstanceState: Bundle?,
        activityHolder: ActivityHolder,
        graph: ApplicationGraph
    ): BasePresenter?

    override fun finish() {
        cleanUpDisposables()
        super.finish()
    }

    override fun onDestroy() {
        cleanUpDisposables()
        super.onDestroy()
    }

    override fun onBackPressed() {
        systemBackPressedEmitter.onNext(Unit)
    }

    private fun cleanUpDisposables() {
        rootDisposable.dispose()
    }
}
