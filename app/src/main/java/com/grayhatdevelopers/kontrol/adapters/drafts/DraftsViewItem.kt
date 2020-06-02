package com.grayhatdevelopers.kontrol.adapters.drafts

import com.grayhatdevelopers.kontrol.R

sealed class DraftsViewItem(val resource: Int) {
    object DraftView: DraftsViewItem(R.layout.item_active_task)
    object DateView: DraftsViewItem(R.layout.item_date)
}