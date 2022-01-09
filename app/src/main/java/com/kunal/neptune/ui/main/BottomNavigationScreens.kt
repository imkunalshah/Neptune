package com.kunal.neptune.ui.main

import androidx.annotation.StringRes
import com.kunal.neptune.R

sealed class BottomNavigationScreens(val route: String, @StringRes val resourceId: Int, val icon: Int) {
    object Home : BottomNavigationScreens("Home", R.string.home, R.drawable.ic_home)
    object Categories : BottomNavigationScreens("Categories",
        R.string.categories,
        R.drawable.ic_categories
    )
    object Messages : BottomNavigationScreens("Messages", R.string.messages, R.drawable.ic_message)
    object Notification : BottomNavigationScreens("Notifications",
        R.string.notifications,
        R.drawable.ic_notification
    )

}