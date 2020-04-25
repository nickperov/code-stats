package com.nickperov.labs.codestats.calc

import com.nickperov.labs.codestats.calc.model.CodeStats
import com.nickperov.labs.codestats.calc.model.ImmutableCodeStats
import com.nickperov.labs.codestats.calc.model.MutableAtomicCodeStats
import kotlinx.coroutines.*
import java.io.File
import java.util.function.Supplier


abstract class AbstractCoroutinesCodeStatsCalculator<T : CodeStats> : AbstractCodeStatsCalculator<T>() {

    override fun calcDirectory(file: File, codeStatsSupplier: Supplier<T>): CodeStats {
        return runBlocking {
            calcDirectoryAsync(file, codeStatsSupplier).await()
        }
    }

    private fun calcDirectoryAsync(file: File, codeStatsSupplier: Supplier<T>): Deferred<T> {
        return GlobalScope.async {
            calcDirectorySuspend(file, codeStatsSupplier)
        }
    }

    private suspend fun calcAllDirectoryFiles(file: File, codeStatsSupplier: Supplier<T>): List<CodeStats>? {
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

    abstract fun collectDirectoryResult(directoryCodeStats: List<CodeStats>, codeStats: T): T

    private fun calcSourceFileAsync(file: File): Deferred<CodeStats> {
        return GlobalScope.async {
            calcSourceFileSuspend(file)
        }
    }

    private suspend fun calcSourceFileSuspend(file: File): CodeStats {
        return withContext(Dispatchers.IO) {
            calcSourceFile(file)
        }
    }
}

class CoroutinesImmutableCodeStatsCalculator : AbstractCoroutinesCodeStatsCalculator<ImmutableCodeStats>() {

    override fun initCodeStats(): ImmutableCodeStats {
        return buildCodeStats(0, 0L)
    }

    override fun buildCodeStats(numOfFiles: Int, numOfLines: Long): ImmutableCodeStats {
        return ImmutableCodeStats(numOfFiles, numOfLines)
    }

    override fun collectDirectoryResult(
        directoryCodeStats: List<CodeStats>,
        codeStats: ImmutableCodeStats
    ): ImmutableCodeStats {
        return directoryCodeStats.fold(codeStats) { left, right -> left.append(right) }
    }
}

class CoroutinesMutableCodeStatsCalculator : AbstractCoroutinesCodeStatsCalculator<MutableAtomicCodeStats>() {

    override fun initCodeStats(): MutableAtomicCodeStats {
        return buildCodeStats(0, 0L)
    }

    override fun buildCodeStats(numOfFiles: Int, numOfLines: Long): MutableAtomicCodeStats {
        return MutableAtomicCodeStats(numOfFiles, numOfLines)
    }

    override fun collectDirectoryResult(
        directoryCodeStats: List<CodeStats>,
        codeStats: MutableAtomicCodeStats
    ): MutableAtomicCodeStats {
        directoryCodeStats.forEach { codeStats.append(it) }
        return codeStats
    }
}