package com.nickperov.labs.codestats.calc

import com.nickperov.labs.codestats.calc.model.CodeStatsCalculator
import org.junit.Test
import java.io.File


class CodeStatsCalculatorTest {

    private val path = "../"
    private val numOfFiles = 18
    private val numOfLines = 582L

    private fun getDirectory(): File {
        val dir = File(path)
        println("Scan directory: " + dir.toPath().toAbsolutePath().normalize())
        return dir
    }

    private fun testCodeStatsCalculator(codeCalculator: CodeStatsCalculator) {
        val codeStats = codeCalculator.calcDirectory(getDirectory())
        println("${codeStats.numberOfFiles()} - ${codeStats.numberOfLines()}")
        //     assertEquals(numOfFiles, codeStats.numberOfFiles())
        //     assertEquals(numOfLines, codeStats.numberOfLines())
    }

    @Test
    fun testSTRecursiveMutableCodeStatsCalc() {
        testCodeStatsCalculator(RecursiveMutableCodeStatsCalculator())
    }

    @Test
    fun testSTRecursiveImmutableCodeStatsCalc() {
        testCodeStatsCalculator(RecursiveImmutableCodeStatsCalculator())
    }

    @Test
    fun testStreamSeqMutableCodeStatsCalc() {
        testCodeStatsCalculator(StreamSeqMutableCodeStatsCalculator())
    }

    @Test
    fun testStreamSeqImmutableCodeStatsCalc() {
        testCodeStatsCalculator(StreamSeqImmutableCodeStatsCalculator())
    }

    @Test
    fun testStreamParallelImmutableCodeStatsCalc() {
        testCodeStatsCalculator(StreamParallelImmutableCodeStatsCalculator())
    }

    @Test
    fun testStreamParallelMutableCodeStatsCalc() {
        testCodeStatsCalculator(StreamParallelMutableCodeStatsCalculator())
    }

    @Test
    fun testForkJoinImmutableCodeStatsCalc() {
        testCodeStatsCalculator(ImmutableForkJoinCodeCalculator())
    }

    @Test
    fun testForkJoinMutableCodeStatsCalc() {
        testCodeStatsCalculator(MutableForkJoinCodeCalculator())
    }
    
    @Test
    fun testForkJoinImmutableCodeStatsThreshold() {
        testCodeStatsCalculator(ImmutableForkJoinCodeCalculator(4))
    }

    @Test
    fun testForkJoinMutableCodeStatsCalcThreshold() {
        testCodeStatsCalculator(MutableForkJoinCodeCalculator(4))
    }

    @Test
    fun testCoroutinesImmutableCodeStatsCalc() {
        testCodeStatsCalculator(CoroutinesImmutableCodeStatsCalculator())
    }

    @Test
    fun testCoroutinesMutableCodeStatsCalc() {
        testCodeStatsCalculator(CoroutinesMutableCodeStatsCalculator())
    }
}