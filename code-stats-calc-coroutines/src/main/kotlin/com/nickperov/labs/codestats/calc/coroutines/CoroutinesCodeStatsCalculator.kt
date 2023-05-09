package com.nickperov.labs.codestats.calc.coroutines

import com.nickperov.labs.codestats.calc.base.AbstractCodeStatsCalculator
import com.nickperov.labs.codestats.calc.base.model.ImmutableSourceCodeStats
import com.nickperov.labs.codestats.calc.base.model.MutableAtomicSourceCodeStats
import com.nickperov.labs.codestats.calc.base.model.SourceCodeStats
import kotlinx.coroutines.*
import java.io.File
import java.util.function.Supplier

abstract class AbstractCoroutinesCodeStatsCalculator<T : SourceCodeStats> : AbstractCodeStatsCalculator<T>() {

    override fun calcDirectory(file: File, codeStatsSupplier: Supplier<T>): SourceCodeStats {
        return runBlocking(Dispatchers.IO) {
            calcDirectoryAsync(file, codeStatsSupplier, this).await()
        }
    }

    suspend fun calcDirectory(directory: File, coroutineScope: CoroutineScope): SourceCodeStats {
        return calcDirectoryAsync(directory, { initCodeStats() }, coroutineScope).await()
    }

    private fun calcDirectoryAsync(
        file: File,
        codeStatsSupplier: Supplier<T>,
        coroutineScope: CoroutineScope
    ): Deferred<T> {
        return coroutineScope.async {
            calcDirectorySuspend(file, codeStatsSupplier, coroutineScope)
        }
    }

    private suspend fun calcAllDirectoryFiles(
        file: File,
        codeStatsSupplier: Supplier<T>,
        coroutineScope: CoroutineScope
    ): List<SourceCodeStats>? {
        return file.listFiles()?.mapNotNull {
            when {
                it.isDirectory -> {
                    calcDirectoryAsync(it, codeStatsSupplier, coroutineScope)
                }

                it.isFile && checkFileName(it.name) -> {
                    calcSourceFileAsync(it, coroutineScope)
                }

                else -> {
                    null
                }
            }
        }?.map { it.await() }
    }

    private suspend fun calcDirectorySuspend(
        file: File,
        codeStatsSupplier: Supplier<T>,
        coroutineScope: CoroutineScope
    ): T {
        val codeStats = codeStatsSupplier.get()
        return calcAllDirectoryFiles(file, codeStatsSupplier, coroutineScope)?.let {
            collectDirectoryResult(it, codeStats)
        } ?: codeStats
    }

    abstract fun collectDirectoryResult(directorySourceCodeStats: List<SourceCodeStats>, codeStats: T): T

    private fun calcSourceFileAsync(file: File, coroutineScope: CoroutineScope): Deferred<SourceCodeStats> {
        return coroutineScope.async {
            calcSourceFile(file)
        }
    }
}

class CoroutinesImmutableCodeStatsCalculator : AbstractCoroutinesCodeStatsCalculator<ImmutableSourceCodeStats>() {

    override fun initCodeStats(): ImmutableSourceCodeStats {
        return buildCodeStats(0, 0L, 0L)
    }

    override fun buildCodeStats(
        numOfFiles: Int,
        numOfCodeLines: Long,
        numOfCommentLines: Long
    ): ImmutableSourceCodeStats {
        return ImmutableSourceCodeStats(numOfFiles, numOfCodeLines, numOfCommentLines)
    }

    override fun collectDirectoryResult(
        directorySourceCodeStats: List<SourceCodeStats>,
        codeStats: ImmutableSourceCodeStats
    ): ImmutableSourceCodeStats {
        return directorySourceCodeStats.fold(codeStats) { left, right -> left.append(right) }
    }
}

class CoroutinesMutableCodeStatsCalculator : AbstractCoroutinesCodeStatsCalculator<MutableAtomicSourceCodeStats>() {

    override fun initCodeStats(): MutableAtomicSourceCodeStats {
        return buildCodeStats(0, 0L, 0L)
    }

    override fun buildCodeStats(
        numOfFiles: Int,
        numOfCodeLines: Long,
        numOfCommentLines: Long
    ): MutableAtomicSourceCodeStats {
        return MutableAtomicSourceCodeStats(numOfFiles, numOfCodeLines, numOfCommentLines)
    }

    override fun collectDirectoryResult(
        directorySourceCodeStats: List<SourceCodeStats>,
        codeStats: MutableAtomicSourceCodeStats
    ): MutableAtomicSourceCodeStats {
        directorySourceCodeStats.forEach { codeStats.append(it) }
        return codeStats
    }
}