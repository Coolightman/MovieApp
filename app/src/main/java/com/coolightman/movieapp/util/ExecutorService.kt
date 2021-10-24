package com.coolightman.movieapp.util

import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

object ExecutorService {
    private val NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors()

    // Instantiates the queue of Threads as a LinkedBlockingQueue
    private val workQueue: BlockingQueue<Runnable> =
        LinkedBlockingQueue()

    // Sets the amount of time an idle thread waits before terminating
    private val KEEP_ALIVE_TIME = 1L

    // Sets the Time Unit to seconds
    private val KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS
    private val executorService: ThreadPoolExecutor = ThreadPoolExecutor(
        NUMBER_OF_CORES,       // Initial pool size
        NUMBER_OF_CORES,       // Max pool size
        KEEP_ALIVE_TIME,
        KEEP_ALIVE_TIME_UNIT,
        workQueue
    )

    fun getExecutor(): ThreadPoolExecutor{
        return executorService
    }
}