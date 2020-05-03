package com.nickperov.labs.codestats.calc.model

import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

interface CodeStats {
    fun numberOfCodeLines(): Long
    fun numberOfCommentLines(): Long
    fun numberOfFiles(): Int
}

interface CodeStatsCalculator {
    fun calcDirectory(directory: File): CodeStats
}

class MutableCodeStats(
    private var numberOfFiles: Int = 0,
    private var numberOfCodeLines: Long = 0L,
    private var numberOfCommentLines: Long = 0L
) : CodeStats {

    fun append(stats: CodeStats) {
        numberOfCodeLines += stats.numberOfCodeLines()
        numberOfCommentLines += stats.numberOfCommentLines()
        numberOfFiles += stats.numberOfFiles()
    }

    override fun numberOfCodeLines() = numberOfCodeLines
    override fun numberOfCommentLines() = numberOfCommentLines
    override fun numberOfFiles() = numberOfFiles
}

class MutableAtomicCodeStats(numOfFiles: Int, numberOfCodeLines: Long, numberOfCommentLines: Long) : CodeStats {

    private val numberOfFiles: AtomicInteger = AtomicInteger(numOfFiles)
    private val numberOfCodeLines: AtomicLong = AtomicLong(numberOfCodeLines)
    private val numberOfCommentLines: AtomicLong = AtomicLong(numberOfCommentLines)

    @Synchronized
    fun append(stats: CodeStats) {
        numberOfFiles.addAndGet(stats.numberOfFiles())
        numberOfCodeLines.addAndGet(stats.numberOfCodeLines())
        numberOfCommentLines.addAndGet(stats.numberOfCommentLines())
    }

    override fun numberOfCodeLines() = numberOfCodeLines.get()
    override fun numberOfCommentLines() = numberOfCommentLines.get()
    override fun numberOfFiles() = numberOfFiles.get()
}

class ImmutableCodeStats(
    private val numberOfFiles: Int,
    private val numberOfCodeLines: Long,
    private val numberOfCommentLines: Long
) : CodeStats {

    override fun numberOfCodeLines() = numberOfCodeLines
    override fun numberOfCommentLines() = numberOfCommentLines
    override fun numberOfFiles() = numberOfFiles

    fun append(stats: CodeStats): ImmutableCodeStats {
        return ImmutableCodeStats(
            numberOfFiles + stats.numberOfFiles(),
            numberOfCodeLines + stats.numberOfCodeLines(),
            numberOfCommentLines + stats.numberOfCommentLines()
        )
    }
}
