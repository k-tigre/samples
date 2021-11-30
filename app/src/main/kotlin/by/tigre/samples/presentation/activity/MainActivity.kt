package by.tigre.samples.presentation.activity

import android.os.Bundle
import android.view.ViewGroup
import by.tigre.samples.R
import by.tigre.samples.di.ApplicationGraph
import by.tigre.samples.presentation.ActivityHolder
import by.tigre.samples.presentation.activity.base.BaseActivity
import by.tigre.samples.presentation.base.BasePresenter
import by.tigre.samples.presentation.users.UsersScreen

class MainActivity : BaseActivity(R.layout.activity_main) {
    override fun onInit(
        screenView: ViewGroup,
        savedInstanceState: Bundle?,
        activityHolder: ActivityHolder,
        graph: ApplicationGraph
    ): BasePresenter = UsersScreen.PresenterFactory.Impl(
        dependencies = graph,
        activityHolder = activityHolder
    ).createScreen(UsersScreen.View.Impl(screenView))
}
