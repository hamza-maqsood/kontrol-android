package com.grayhatdevelopers.kontrol.utils


/**
 * Unique id is composed of:
 * current time stamp - 47 bits (millisecond precision w/a custom epoch gives as 69 years)
 * sequence number - 17 bits - rolls over every 65536 with protection to avoid rollover in the same ms
 */

object UniqueIdGenerator {
    private const val twepoch = 1288834974657L
    private const val sequenceBits: Long = 11
    private const val sequenceMax: Long = 65536
    @Volatile
    private var lastTimestamp = -1L
    @Volatile
    private var sequence = 0L


    fun getUniqueId() = generateLongId()

    @Synchronized
    private fun generateLongId(): Long? {
        var timestamp = System.currentTimeMillis()
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) % sequenceMax
            if (sequence == 0L) {
                timestamp = tilNextMillis(lastTimestamp)
            }
        } else {
            sequence = 0
        }
        lastTimestamp = timestamp
        return timestamp - twepoch shl sequenceBits.toInt() or sequence
    }

    private fun tilNextMillis(lastTimestamp: Long): Long {
        var timestamp = System.currentTimeMillis()
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis()
        }
        return timestamp
    }
}