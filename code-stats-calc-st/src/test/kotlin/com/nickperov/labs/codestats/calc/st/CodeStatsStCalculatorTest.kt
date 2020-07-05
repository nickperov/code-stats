package com.nickperov.labs.codestats.calc.st

import com.nickperov.labs.codestats.calc.base.CodeStatsCalculatorTest
import org.junit.Test

class CodeStatsStCalculatorTest : CodeStatsCalculatorTest() {

    @Test
    fun testStreamSeqMutableCodeStatsCalc() {
        testCodeStatsCalculator(com.nickperov.labs.codestats.calc.st.StreamSeqMutableCodeStatsCalculator())
    }

    @Test
    fun testStreamSeqImmutableCodeStatsCalc() {
        testCodeStatsCalculator(com.nickperov.labs.codestats.calc.st.StreamSeqImmutableCodeStatsCalculator())
    }

    @Test
    fun testSTRecursiveMutableCodeStatsCalc() {
        testCodeStatsCalculator(RecursiveMutableCodeStatsCalculator())
    }

    @Test
    fun testSTRecursiveImmutableCodeStatsCalc() {
        testCodeStatsCalculator(RecursiveImmutableCodeStatsCalculator())
    }
}