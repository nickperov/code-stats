package com.nickperov.labs.codestats.calc

import com.nickperov.labs.codestats.calc.model.SourceCodeStats
import com.nickperov.labs.codestats.calc.model.ImmutableSourceCodeStats
import com.nickperov.labs.codestats.calc.model.MutableSourceCodeStats
import java.io.File
import java.util.function.Supplier

abstract class AbstractRecursiveCodeStatsCalculator<T : SourceCodeStats> : AbstractCodeStatsCalculator<T>() {

    override fun calcDirectory(file: File, codeStatsSupplier: Supplier<T>): SourceCodeStats {
        return calcDirectory(file, codeStatsSupplier.get())
    }

    abstract fun calcDirectory(file: File, codeStats: T): SourceCodeStats
}

class RecursiveMutableCodeStatsCalculator : AbstractRecursiveCodeStatsCalculator<MutableSourceCodeStats>() {

    override fun calcDirectory(file: File, codeStats: MutableSourceCodeStats): SourceCodeStats {
        if (file.isFile && checkFileName(file.name)) {
            codeStats.append(calcSourceFile(file))
        } else if (file.isDirectory) {
            file.listFiles()?.forEach { calcDirectory(it, codeStats) }
        }

        return codeStats
    }

    override fun initCodeStats() = MutableSourceCodeStats()
}

class RecursiveImmutableCodeStatsCalculator : AbstractRecursiveCodeStatsCalculator<ImmutableSourceCodeStats>() {

    override fun calcDirectory(file: File, codeStats: ImmutableSourceCodeStats): SourceCodeStats {
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

    override fun initCodeStats() = ImmutableSourceCodeStats(0, 0L, 0L)
}


