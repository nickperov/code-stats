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
        return runBlocking {
            calcDirectoryAsync(file, codeStatsSupplier).await()
        }
    }

    private fun calcDirectoryAsync(file: File, codeStatsSupplier: Supplier<T>): Deferred<T> {
        return GlobalScope.async {
            calcDirectorySuspend(file, codeStatsSupplier)
        }
    }

    private suspend fun calcAllDirectoryFiles(file: File, codeStatsSupplier: Supplier<T>): List<SourceCodeStats>? {
        return file.listFiles()?.mapNotNull {
            when {
                it.isDirectory -> {
                    calcDirectoryAsync(it, codeStatsSupplier)
                }
                it.isFile && checkFileName(it.name) -> {
                    calcSourceFileAsync(it)
                }
                else -> {
                    null
                }
            }
        }?.map { it.await() }
    }

    private suspend fun calcDirectorySuspend(file: File, codeStatsSupplier: Supplier<T>): T {
        val codeStats = codeStatsSupplier.get()
        return calcAllDirectoryFiles(file, codeStatsSupplier)?.let { collectDirectoryResult(it, codeStats) }
            ?: codeStats

    }

    abstract fun collectDirectoryResult(directorySourceCodeStats: List<SourceCodeStats>, codeStats: T): T

    private fun calcSourceFileAsync(file: File): Deferred<SourceCodeStats> {
        return GlobalScope.async {
            calcSourceFileSuspend(file)
        }
    }

    private suspend fun calcSourceFileSuspend(file: File): SourceCodeStats {
        return withContext(Dispatchers.IO) {
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