package com.nickperov.labs.codestats.calc.base

import com.nickperov.labs.codestats.calc.base.model.CodeStatsCalculator
import junit.framework.Assert.assertEquals
import java.io.File

abstract class CodeStatsCalculatorTest {

    private val path = "../"
    private val numOfFiles = 48
    private val numOfCodeLines = 3746L
    private val numOfCommentLines = 165L

    private fun getDirectory(): File {
        val dir = File(path)
        println("Scan directory: " + dir.toPath().toAbsolutePath().normalize())
        return dir
    }

    protected fun testCodeStatsCalculator(codeCalculator: CodeStatsCalculator) {
        val codeStats = codeCalculator.calcDirectory(getDirectory())
        println("${codeStats.numberOfFiles()} - ${codeStats.numberOfCodeLines()} - ${codeStats.numberOfCommentLines()}")
        assertEquals(numOfFiles, codeStats.numberOfFiles())
        assertEquals(numOfCodeLines, codeStats.numberOfCodeLines())
        assertEquals(numOfCommentLines, codeStats.numberOfCommentLines())
    }
}