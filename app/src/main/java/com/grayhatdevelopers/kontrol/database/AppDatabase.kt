package com.grayhatdevelopers.kontrol.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.grayhatdevelopers.kontrol.database.AppDatabase.Companion.DATABASE_VERSION
import com.grayhatdevelopers.kontrol.database.messages.MessageEntity
import com.grayhatdevelopers.kontrol.database.messages.MessagesDAO
import com.grayhatdevelopers.kontrol.database.payments.PaymentEntity
import com.grayhatdevelopers.kontrol.database.payments.PaymentsDAO
import com.grayhatdevelopers.kontrol.database.tasks.TaskEntity
import com.grayhatdevelopers.kontrol.database.tasks.TasksDAO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

@Database(
    entities = [
        TaskEntity::class,
        PaymentEntity::class,
        MessageEntity::class
    ],
    version = DATABASE_VERSION,
    exportSchema = false)
@TypeConverters(com.grayhatdevelopers.kontrol.database.TypeConverters::class)
abstract class AppDatabase : RoomDatabase(), CoroutineScope {

    private var mJob: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.IO

    /**
     * DAOs
     */
    abstract fun messagesDAO() : MessagesDAO
    abstract fun paymentsDAO() : PaymentsDAO
    abstract fun tasksDAO() : TasksDAO

    companion object {
        private const val DATABASE_NAME = "kontrol_database.db"
        const val DATABASE_VERSION = 1

        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                DATABASE_NAME
            ).fallbackToDestructiveMigration()
            .build()

    }

    fun finalize() {
        mJob.cancel()
    }
}