package by.tigre.samples.data.platform

import android.content.Context
import android.text.format.DateUtils
import androidx.annotation.StringRes

interface Resources {

    fun string(@StringRes id: Int): String
    fun formatDate(timestamp: Long): String

    class Impl(private val context: Context) : Resources {

        private val resources = context.resources

        override fun string(id: Int) = resources.getString(id)

        override fun formatDate(timestamp: Long): String =
            DateUtils.formatDateTime(
                context,
                timestamp,
                DateUtils.FORMAT_SHOW_YEAR or DateUtils.FORMAT_NUMERIC_DATE or DateUtils.FORMAT_SHOW_DATE
            )
    }
}
