package com.nickperov.labs.codestats.calc.base.model

import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

interface SourceCodeStats {
    fun numberOfCodeLines(): Long
    fun numberOfCommentLines(): Long
    fun numberOfFiles(): Int
}

interface CodeStatsCalculator {
    fun calcDirectory(directory: File): SourceCodeStats
}

class MutableSourceCodeStats(
    private var numberOfFiles: Int = 0,
    private var numberOfCodeLines: Long = 0L,
    private var numberOfCommentLines: Long = 0L
) : SourceCodeStats {

    fun append(stats: SourceCodeStats) {
        numberOfCodeLines += stats.numberOfCodeLines()
        numberOfCommentLines += stats.numberOfCommentLines()
        numberOfFiles += stats.numberOfFiles()
    }

    override fun numberOfCodeLines() = numberOfCodeLines
    override fun numberOfCommentLines() = numberOfCommentLines
    override fun numberOfFiles() = numberOfFiles
}

class MutableAtomicSourceCodeStats(numOfFiles: Int, numberOfCodeLines: Long, numberOfCommentLines: Long) :
    SourceCodeStats {

    private val numberOfFiles: AtomicInteger = AtomicInteger(numOfFiles)
    private val numberOfCodeLines: AtomicLong = AtomicLong(numberOfCodeLines)
    private val numberOfCommentLines: AtomicLong = AtomicLong(numberOfCommentLines)

    @Synchronized
    fun append(stats: SourceCodeStats) {
        numberOfFiles.addAndGet(stats.numberOfFiles())
        numberOfCodeLines.addAndGet(stats.numberOfCodeLines())
        numberOfCommentLines.addAndGet(stats.numberOfCommentLines())
    }

    override fun numberOfCodeLines() = numberOfCodeLines.get()
    override fun numberOfCommentLines() = numberOfCommentLines.get()
    override fun numberOfFiles() = numberOfFiles.get()
}

data class ImmutableSourceCodeStats(
    private val numberOfFiles: Int,
    private val numberOfCodeLines: Long,
    private val numberOfCommentLines: Long
) : SourceCodeStats {

    override fun numberOfCodeLines() = numberOfCodeLines
    override fun numberOfCommentLines() = numberOfCommentLines
    override fun numberOfFiles() = numberOfFiles

    fun append(stats: SourceCodeStats): ImmutableSourceCodeStats {
        return ImmutableSourceCodeStats(
            numberOfFiles + stats.numberOfFiles(),
            numberOfCodeLines + stats.numberOfCodeLines(),
            numberOfCommentLines + stats.numberOfCommentLines()
        )
    }
}

data class LineCodeStats(
    private val numberOfCodeLines: Int,
    private val numberOfCommentLines: Int,
    private val commentBlock: Boolean
) {
    fun numberOfCodeLines() = numberOfCodeLines
    fun numberOfCommentLines() = numberOfCommentLines
    fun isOpenCommentBlock() = commentBlock
}

