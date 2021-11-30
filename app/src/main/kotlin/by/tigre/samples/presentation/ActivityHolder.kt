package by.tigre.samples.presentation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import by.tigre.samples.presentation.base.HandleBackView
import io.reactivex.rxjava3.core.Observable

interface ActivityHolder {
    val activity: Activity
    val systemBackPressed: Observable<Unit>

    fun close()

    fun processBackButton(
        view: HandleBackView,
        canHandleSource: Observable<Boolean>
    ): Observable<Unit>

    fun startActivityForResult(action: String, uri: Uri, requestCode: Int)

    class Impl(
        override val activity: Activity,
        override val systemBackPressed: Observable<Unit>,
    ) : ActivityHolder {

        override fun close() {
            activity.finish()
        }

        override fun processBackButton(
            view: HandleBackView,
            canHandleSource: Observable<Boolean>
        ): Observable<Unit> = Observable.merge(view.onBackClicked, systemBackPressed)
            .withLatestFrom(canHandleSource) { _, canHandle -> canHandle }
            .filter { it }
            .map {  }

        override fun startActivityForResult(action: String, uri: Uri, requestCode: Int) {
            activity.startActivityForResult(Intent(action, uri), requestCode)
        }
    }
}
