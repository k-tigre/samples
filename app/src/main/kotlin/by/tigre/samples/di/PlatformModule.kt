package by.tigre.samples.di

import android.content.Context
import by.tigre.samples.data.platform.Resources

interface PlatformModule {

    val resources: Resources

    class Impl(
        context: Context,
    ) : PlatformModule {

        override val resources by lazy {
            Resources.Impl(context)
        }
    }
}
