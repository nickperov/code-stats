package com.nickperov.labs.codestats.calc.mt

import com.nickperov.labs.codestats.calc.base.CodeStatsCalculatorTest
import org.junit.Test


internal class CodeStatsMtCalculatorTest : CodeStatsCalculatorTest() {

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
}
