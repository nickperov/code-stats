package com.nickperov.labs.codestats.calc.coroutines

import com.nickperov.labs.codestats.calc.base.CodeStatsCalculatorTest
import org.junit.Test

class CodeStatsCoroutinesCalculatorTest : CodeStatsCalculatorTest() {
    @Test
    fun testCoroutinesImmutableCodeStatsCalc() {
        testCodeStatsCalculator(CoroutinesImmutableCodeStatsCalculator())
    }

    @Test
    fun testCoroutinesMutableCodeStatsCalc() {
        testCodeStatsCalculator(CoroutinesMutableCodeStatsCalculator())
    }
}

