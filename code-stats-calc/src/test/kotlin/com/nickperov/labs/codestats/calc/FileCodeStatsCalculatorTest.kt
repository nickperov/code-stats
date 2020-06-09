package com.nickperov.labs.codestats.calc

import com.nickperov.labs.codestats.calc.model.ImmutableSourceCodeStats
import com.nickperov.labs.codestats.calc.model.LineCodeStats
import com.nickperov.labs.codestats.calc.model.SourceCodeStats
import org.junit.Test
import java.io.File
import java.lang.RuntimeException
import java.util.function.Supplier
import kotlin.test.assertEquals

class FileCodeStatsCalculatorTest {

    class TestCodeStatsCalculator : AbstractCodeStatsCalculator<ImmutableSourceCodeStats>() {
        override fun initCodeStats(): ImmutableSourceCodeStats {
            throw RuntimeException("Not implemented")
        }

        override fun calcDirectory(
            file: File?,
            codeStatsSupplier: Supplier<ImmutableSourceCodeStats>?
        ): SourceCodeStats {
            throw RuntimeException("Not implemented")
        }
    }

    private val codeStatsCalculator = TestCodeStatsCalculator()
    private val lineCalcMethod =
        AbstractCodeStatsCalculator::class.java.getDeclaredMethod("calcSourceFile", File::class.java)
    private val calcFile = { file: File -> lineCalcMethod.invoke(codeStatsCalculator, file) as SourceCodeStats }

    init {
        lineCalcMethod.trySetAccessible()
    }

    @Test
    fun calcJavaFile() {
        val codeStats = calcFile.invoke(File("src/test/resources/SampleJavaClass.java"))
        assertEquals(codeStats.numberOfFiles(), 1)
        assertEquals(codeStats.numberOfCodeLines(), 6)
        assertEquals(codeStats.numberOfCommentLines(), 7)
    }

    @Test
    fun calcKotlinFile() {
        val codeStats = calcFile.invoke(File("src/test/resources/SampleKotlinClass.kt"))
        assertEquals(codeStats.numberOfFiles(), 1)
        assertEquals(codeStats.numberOfCodeLines(), 6)
        assertEquals(codeStats.numberOfCommentLines(), 5)
    }
}

