package com.quickmeds24.app.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * RiderLocationSyncWorker
 * Android WorkManager CoroutineWorker for OS-level background periodic updates (e.g. 15m or when app is killed)
 */
class RiderLocationSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val orderId = inputData.getString("ORDER_ID") ?: "QM-8847"
            // Fetch rider coordinate update in background thread without waking UI
            // ...
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

