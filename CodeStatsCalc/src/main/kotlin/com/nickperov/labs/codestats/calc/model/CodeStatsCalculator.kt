package com.nickperov.labs.codestats.calc.model

import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

interface CodeStats {
    fun numberOfLines(): Long
    fun numberOfFiles(): Int
}

interface CodeStatsCalculator {

    fun calcDirectory(directory: File): CodeStats
}

class MutableCodeStats(private var numberOfFiles: Int = 0, private var numberOfLines: Long = 0L) : CodeStats {

    fun append(stats: CodeStats) {
        numberOfLines += stats.numberOfLines()
        numberOfFiles += stats.numberOfFiles()
    }

    override fun numberOfLines() = numberOfLines


    override fun numberOfFiles() = numberOfFiles
}

class MutableAtomicCodeStats(numOfFiles: Int, numOfLines: Long) : CodeStats {

    private val numberOfFiles: AtomicInteger = AtomicInteger(numOfFiles)
    private val numberOfLines: AtomicLong = AtomicLong(numOfLines)

    @Synchronized
    fun append(stats: CodeStats) {
        numberOfFiles.addAndGet(stats.numberOfFiles())
        numberOfLines.addAndGet(stats.numberOfLines())
    }

    override fun numberOfLines() = numberOfLines.get()

    override fun numberOfFiles() = numberOfFiles.get()
}

class ImmutableCodeStats(
    private val numberOfFiles: Int,
    private val numberOfLines: Long
) : CodeStats {

    override fun numberOfLines() = numberOfLines

    override fun numberOfFiles() = numberOfFiles

    fun append(stats: CodeStats): ImmutableCodeStats {
        return ImmutableCodeStats(numberOfFiles + stats.numberOfFiles(), numberOfLines + stats.numberOfLines())
    }
}
