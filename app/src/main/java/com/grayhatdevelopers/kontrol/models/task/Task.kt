package com.grayhatdevelopers.kontrol.models.task

import java.io.Serializable

data class Task(
    val taskID: String, /* server generated unique identifier */
    val rider: String, /* rider to whom this very task is assigned */
    val debit: Double, /* amount that the rider is supposed to take from the client */
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
) : Serializable