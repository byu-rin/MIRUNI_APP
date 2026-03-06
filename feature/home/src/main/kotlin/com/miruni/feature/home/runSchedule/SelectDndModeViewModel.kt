package com.miruni.feature.home.runSchedule

import com.miruni.core.common.BaseViewModel
import com.miruni.feature.home.presentation.DndContract

class SelectDndModeViewModel :
    BaseViewModel<DndContract.Event, DndContract.State, DndContract.Effect>() {

    override fun setInitialState() = DndContract.State()

    override fun handleEvents(event: DndContract.Event) { }
}