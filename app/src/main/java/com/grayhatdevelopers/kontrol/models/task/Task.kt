package com.grayhatdevelopers.kontrol.models.task

import androidx.annotation.NonNull
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = Task.TABLE_NAME
)
data class Task(
    val taskID: String, /* server generated unique identifier */
    val rider: String, /* rider to whom this very task is assigned */
    val debit: Long, /* amount that the rider is supposed to take from the client */
    val taskModel: String, /* task model */
    val shopName: String, /* shop, for whom this very task is */
    val date: String, /* assignment date */
    val createdAt: String, /* time when the task was created */
    val lastUpdatedAt: String, /* time of last update */
    var taskStatus: TaskStatus, /* current status of task */
    val transference: Int, /* transference number */
    val taskType: String, /* task type */
    val assignedTo: String, /* rider to whom this task was actually assigned */
    val company: String, /* products company */
    val paymentId: String? /* payment for this very task */
) : Serializable, BaseObservable() {

    constructor() : this("", "", 0, "", "", "", "", "", TaskStatus.ACTIVE, 0, "", "", "", "")

//    @NonNull
//    @PrimaryKey
//    @get:Bindable
//    @ColumnInfo(name = COLUMN_TASK_ID)
//    var _task_ID: String = taskID
//
//    @ColumnInfo(name = COLUMN_RIDER)
//    @get:Bindable
//    var _rider: String = rider
//        set(value) {
//            field = value
////            notifyPropertyChanged(BR.rider)
//        }
//
//    @ColumnInfo(name = COLUMN_DEBIT)
//    @get:Bindable
//    var _debit: Long = debit
//        set(value) {
//            field = value
////            notifyPropertyChanged(BR.debit)
//        }
//
//    @ColumnInfo(name = COLUMN_TASK_MODEL)
//    @get:Bindable
//    var _taskModel: String = taskModel
//        set(value) {
//            field = value
////            notifyPropertyChanged(BR.taskModel)
//        }
//
//    @ColumnInfo(name = COLUMN_SHOP_NAME)
//    @get:Bindable
//    var _shopName: String = shopName
//        set(value) {
//            field = value
////            notifyPropertyChanged(BR.shopName)
//        }
//
//    @ColumnInfo(name = COLUMN_DATE)
//    @get:Bindable
//    var _date: String = date
//        set(value) {
//            field = value
////            notifyPropertyChanged(BR.date)
//        }
//
//    @ColumnInfo(name = COLUMN_CREATED_AT)
//    @get:Bindable
//    var _createdAt: String = createdAt
//        set(value) {
//            field = value
////            notifyPropertyChanged(BR.categoryID)
//        }
//
//    @ColumnInfo(name = COLUMN_LAST_UPDATED_AT)
//    @get:Bindable
//    var _lastUpdatedAt: String = lastUpdatedAt
//        set(value) {
//            field = value
////            notifyPropertyChanged(BR.categoryID)
//        }
//
//    @ColumnInfo(name = COLUMN_TASK_STATUS)
//    @get:Bindable
//    var _taskStatus: TaskStatus = taskStatus
//        set(value) {
//            field = value
////            notifyPropertyChanged(BR.categoryID)
//        }
//
//    @ColumnInfo(name = COLUMN_TRANSFERENCE)
//    @get:Bindable
//    var _transference: Int = transference
//        set(value) {
//            field = value
////            notifyPropertyChanged(BR.categoryID)
//        }
//
//    @ColumnInfo(name = COLUMN_TASK_TYPE)
//    @get:Bindable
//    var _taskType: String = taskType
//        set(value) {
//            field = value
////            notifyPropertyChanged(BR.categoryID)
//        }
//
//    @ColumnInfo(name = COLUMN_ASSIGNED_TO)
//    @get:Bindable
//    var _assignedTo: String = assignedTo
//        set(value) {
//            field = value
////            notifyPropertyChanged(BR.categoryID)
//        }
//
//    @ColumnInfo(name = COLUMN_COMPANY)
//    @get:Bindable
//    var _company: String = company
//        set(value) {
//            field = value
////            notifyPropertyChanged(BR.categoryID)
//        }
//
//    @ColumnInfo(name = COLUMN_PAYMENT_ID)
//    @get:Bindable
//    var _paymentID: String? = paymentId
//        set(value) {
//            field = value
////            notifyPropertyChanged(BR.categoryID)
//        }


    companion object {
        const val TABLE_NAME = "Tasks_Table"
        const val COLUMN_TASK_ID = "TASK_ID"
        const val COLUMN_RIDER = "RIDER"
        const val COLUMN_DEBIT = "DEBIT"
        const val COLUMN_TASK_MODEL = "TASK_MODEL"
        const val COLUMN_SHOP_NAME = "TASK_MODEL"
        const val COLUMN_DATE = "TASK_MODEL"
        const val COLUMN_CREATED_AT = "TASK_MODEL"
        const val COLUMN_LAST_UPDATED_AT = "TASK_MODEL"
        const val COLUMN_TASK_STATUS = "TASK_MODEL"
        const val COLUMN_TRANSFERENCE = "TASK_MODEL"
        const val COLUMN_TASK_TYPE = "TASK_MODEL"
        const val COLUMN_ASSIGNED_TO = "TASK_MODEL"
        const val COLUMN_COMPANY = "TASK_MODEL"
        const val COLUMN_PAYMENT_ID = "TASK_MODEL"
    }
}

enum class TaskStatus {
    ACTIVE, COMPLETED, WAITING
}

enum class TaskType {
    OFF_SCHEDULE, REGULAR, EMERGENCY
}

enum class TaskModel {
    INVOICE, PAYMENT, RETURN
}