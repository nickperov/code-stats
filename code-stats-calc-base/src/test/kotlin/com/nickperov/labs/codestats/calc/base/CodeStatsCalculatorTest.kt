package com.nickperov.labs.codestats.calc.base

import com.nickperov.labs.codestats.calc.base.model.CodeStatsCalculator
import org.junit.Assert.assertEquals
import java.io.File

abstract class CodeStatsCalculatorTest {

    private val path = "../"
    private val numOfFiles = 29
    private val numOfCodeLines = 1175L // Code statistics counts 1165 (not counting imports)
    private val numOfCommentLines = 157L
    
    private fun getDirectory(): File {
        val dir = File(path)
        println("Scan directory: " + dir.toPath().toAbsolutePath().normalize())
        return dir
    }

    protected fun testCodeStatsCalculator(codeCalculator: CodeStatsCalculator) {
        val codeStats = codeCalculator.calcDirectory(getDirectory())
        assertEquals(numOfFiles, codeStats.numberOfFiles())
        assertEquals(numOfCodeLines, codeStats.numberOfCodeLines())
        assertEquals(numOfCommentLines, codeStats.numberOfCommentLines())
    }
}