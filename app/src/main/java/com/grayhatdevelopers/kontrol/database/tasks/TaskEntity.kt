package com.grayhatdevelopers.kontrol.database.tasks

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.grayhatdevelopers.kontrol.database.tasks.TaskEntity.Companion.COLUMN_TASK_ID
import com.grayhatdevelopers.kontrol.database.tasks.TaskEntity.Companion.TABLE_NAME
import java.io.Serializable


@Entity(
    tableName = TABLE_NAME,
    indices = [Index(value = [COLUMN_TASK_ID], unique = true)]
)
data class TaskEntity(

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = COLUMN_TASK_ID)
    val taskID: String,

    @ColumnInfo(name = COLUMN_RIDER)
    var rider: String,

    @ColumnInfo(name = COLUMN_DEBIT)
    var debit: String,

    @ColumnInfo(name = COLUMN_TASK_MODEL)
    var taskModel: String,

    @ColumnInfo(name = COLUMN_SHOP_NAME)
    var shopName: String,

    @ColumnInfo(name = COLUMN_DATE)
    var date: String,

    @ColumnInfo(name = COLUMN_CREATED_AT)
    var createdAt: String,

    @ColumnInfo(name = COLUMN_LAST_UPDATED_AT)
    var lastUpdatedAt: String,

    @ColumnInfo(name = COLUMN_TASK_STATUS)
    var taskStatus: String,

    @ColumnInfo(name = COLUMN_TRANSFERENCE)
    var transference: String,

    @ColumnInfo(name = COLUMN_TASK_TYPE)
    var taskType: String,

    @ColumnInfo(name = COLUMN_ASSIGNED_TO)
    var assignedTo: String,

    @ColumnInfo(name = COLUMN_COMPANY)
    var company: String,

    @ColumnInfo(name = COLUMN_PAYMENT_ID)
    var paymentID: String?

) : Serializable {

    companion object {
        const val TABLE_NAME = "Tasks_Table"
        const val COLUMN_TASK_ID = "taskID"
        const val COLUMN_RIDER = "rider"
        const val COLUMN_DEBIT = "debit"
        const val COLUMN_TASK_MODEL = "taskModel"
        const val COLUMN_SHOP_NAME = "shopName"
        const val COLUMN_DATE = "date"
        const val COLUMN_CREATED_AT = "createdAt"
        const val COLUMN_LAST_UPDATED_AT = "lastUpdatedAt"
        const val COLUMN_TASK_STATUS = "taskStatus"
        const val COLUMN_TRANSFERENCE = "transference"
        const val COLUMN_TASK_TYPE = "taskType"
        const val COLUMN_ASSIGNED_TO = "assignedTo"
        const val COLUMN_COMPANY = "company"
        const val COLUMN_PAYMENT_ID = "paymentID"
    }
}
