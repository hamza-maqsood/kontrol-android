package com.grayhatdevelopers.kontrol.ui.fragments.base

sealed class LaunchIntentCommand {
    object SelectImage : LaunchIntentCommand()
}