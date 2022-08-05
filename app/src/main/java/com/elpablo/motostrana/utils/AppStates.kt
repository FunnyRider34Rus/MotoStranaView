package com.elpablo.motostrana.utils

import com.elpablo.motostrana.App.Companion.USER

enum class AppStates(val state: String) {
    ONLINE("в сети"),
    OFFLINE("не в сети"),
    TYPING("печатает");

    companion object {
        fun updateState(appStates: AppStates) {
            USER.state = appStates.toString()
        }
    }
}