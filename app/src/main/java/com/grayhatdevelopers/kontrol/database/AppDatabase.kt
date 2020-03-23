package com.grayhatdevelopers.kontrol.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.grayhatdevelopers.kontrol.models.payment.Payment
import com.grayhatdevelopers.kontrol.models.task.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

//@Database(entities = [Task::class, Payment::class], version = DATABASE_VERSION)
//abstract class AppDatabase : RoomDatabase(), CoroutineScope {
//
//    private var mJob: Job = Job()
//
//    override val coroutineContext: CoroutineContext
//        get() = mJob + Dispatchers.IO
//
//    companion object {
//        private const val DATABASE_NAME = "kontrol_database.db"
//        const val DATABASE_VERSION = 1
//
//        @Volatile
//        private var instance: AppDatabase? = null
//        private val LOCK = Any()
//
//        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
//            instance ?: buildDatabase(context).also { instance = it }
//        }
//
//        private fun buildDatabase(context: Context) = Room.databaseBuilder(
//                context,
//                AppDatabase::class.java,
//                DATABASE_NAME
//            ).fallbackToDestructiveMigration()
//            .build()
//
//    }
//
//    fun finalize() {
//        mJob.cancel()
//    }
//}