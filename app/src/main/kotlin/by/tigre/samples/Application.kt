package by.tigre.samples

import by.tigre.samples.di.ApplicationGraph
import by.tigre.samples.extensions.MainDisposable
import com.squareup.picasso.Picasso
import android.app.Application as AndroidApplication

class Application : AndroidApplication() {

    lateinit var graph: ApplicationGraph
        private set

    private var mainDisposable: MainDisposable = MainDisposable()

    override fun onCreate() {
        super.onCreate()

        graph = ApplicationGraph.create(this, mainDisposable)

        initPicasso()
    }

    private fun initPicasso() {
        if (BuildConfig.DEBUG) {
            Picasso.get().isLoggingEnabled = true
        }
    }
}
