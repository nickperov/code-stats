package com.nickperov.labs.codestats.calc

import com.nickperov.labs.codestats.calc.model.CodeStats
import com.nickperov.labs.codestats.calc.model.ImmutableCodeStats
import com.nickperov.labs.codestats.calc.model.MutableCodeStats
import java.io.File
import java.util.function.Supplier

abstract class AbstractRecursiveCodeStatsCalculator<T : CodeStats> : AbstractCodeStatsCalculator<T>() {

    override fun calcDirectory(file: File, codeStatsSupplier: Supplier<T>): CodeStats {
        return calcDirectory(file, codeStatsSupplier.get())
    }

    abstract fun calcDirectory(file: File, codeStats: T): CodeStats
}

class RecursiveMutableCodeStatsCalculator : AbstractRecursiveCodeStatsCalculator<MutableCodeStats>() {

    override fun calcDirectory(file: File, codeStats: MutableCodeStats): CodeStats {
        if (file.isFile && checkFileName(file.name)) {
            codeStats.append(calcSourceFile(file))
        } else if (file.isDirectory) {
            file.listFiles()?.forEach { calcDirectory(it, codeStats) }
        }

        return codeStats
    }

    override fun initCodeStats() = MutableCodeStats()
}

class RecursiveImmutableCodeStatsCalculator : AbstractRecursiveCodeStatsCalculator<ImmutableCodeStats>() {

    override fun calcDirectory(file: File, codeStats: ImmutableCodeStats): CodeStats {
        return when {
            file.isFile -> {
                if (checkFileName(file.name))
                    codeStats.append(calcSourceFile(file))
                else
                    codeStats
            }
            file.isDirectory -> {
                file.listFiles()?.map { calcDirectory(it, codeStats) }
                    ?.fold(initCodeStats()) { left, right -> left.append(right) } ?: codeStats
            }
            else -> {
                codeStats
            }
        }
    }

    override fun initCodeStats() = ImmutableCodeStats(0, 0L, 0L)
}


