package com.nickperov.labs.codestats.calc.base

import com.nickperov.labs.codestats.calc.base.model.LineCodeStats
import org.junit.Test
import kotlin.test.assertEquals

class LineCodeStatsCalculatorTest {

    private val lineCalcMethod =
        AbstractCodeStatsCalculator::class.java.getDeclaredMethod("calcLine", Boolean::class.java, String::class.java)
    private val calcLine =
        { isComment: Boolean, line: String -> lineCalcMethod.invoke(null, isComment, line) as LineCodeStats }

    init {
        lineCalcMethod.trySetAccessible()
    }

    private fun checkEmptyLineStats(lineCodeStats: LineCodeStats) = checkMixedLineStats(lineCodeStats, 0, 0, false)

    private fun checkSingleCodeLineStats(lineCodeStats: LineCodeStats) = checkMultiCodeLineStats(lineCodeStats, 1)

    private fun checkSingleCommentLineStats(lineCodeStats: LineCodeStats) =
        checkCommentBlockLineStats(lineCodeStats, 1, false)

    private fun checkMultiCodeLineStats(lineCodeStats: LineCodeStats, numberOfCodeLines: Int) =
        checkMixedLineStats(lineCodeStats, numberOfCodeLines, 0, false)

    private fun checkCommentBlockLineStats(
        lineCodeStats: LineCodeStats,
        numberOfCommentLines: Int,
        isOpenCommentBlock: Boolean
    ) = checkMixedLineStats(lineCodeStats, 0, numberOfCommentLines, isOpenCommentBlock)

    private fun checkMixedLineStats(
        lineCodeStats: LineCodeStats,
        numberOfCodeLines: Int,
        numberOfCommentLines: Int,
        isOpenCommentBlock: Boolean
    ) {
        assertEquals(lineCodeStats.numberOfCodeLines(), numberOfCodeLines)
        assertEquals(lineCodeStats.numberOfCommentLines(), numberOfCommentLines)
        assertEquals(lineCodeStats.isOpenCommentBlock(), isOpenCommentBlock)
    }

    private fun checkArrayOfLines(lines: Array<String>, checker: (line: LineCodeStats) -> Unit) {
        lines.map { line -> calcLine(false, line) }.forEach { line -> checker.invoke(line) }
    }

    @Test
    fun zeroLineTest() {
        checkEmptyLineStats(calcLine(false, ""))
    }

    @Test
    fun emptyLineTest() {
        checkArrayOfLines(arrayOf("\t", "   ", "\t\r  \t")) { checkEmptyLineStats(it) }
    }

    @Test
    fun singleJavaCodeLineTest() {
        checkArrayOfLines(
            arrayOf(
                "Long t = getTime();",
                "\t\t\tLong t = getTime();  ",
                "  Long t = getTime();  "
            )
        ) { checkSingleCodeLineStats(it) }
    }

    @Test
    fun singleKotlinCodeLineTest() {
        checkArrayOfLines(
            arrayOf(
                "val t = getTime()",
                "val t = getTime();",
                "\t\t\t\"val t = getTime()  ",
                "  val t = getTime()  "
            )
        ) { checkSingleCodeLineStats(it) }
    }

    @Test
    fun multiJavaCodeLineTest() {
        checkArrayOfLines(
            arrayOf(
                "private final Integer i = 0; final long t = 1L; public void test() { ",
                "   private final Integer i = 0; \t\t final long t = 1L;  \t public void test() { "
            )
        ) { checkMultiCodeLineStats(it, 3) }
    }

    @Test
    fun multiKotlinCodeLineTest() {
        checkArrayOfLines(
            arrayOf(
                "val i = 0; val t = 1L; fun test() { ",
                "   val i = 0;\t\tval t = 1L;  \t  fun test() { "
            )
        ) { checkMultiCodeLineStats(it, 3) }
    }

    @Test
    fun singleCommentLineTest() {
        checkArrayOfLines(
            arrayOf(
                "// comment",
                "   // comment",
                "\t\t// comment ",
                " \t\t // comment "
            )
        ) { checkSingleCommentLineStats(it) }
    }

    @Test
    fun commentBlockLineTest() {
        checkArrayOfLines(
            arrayOf(
                " /* comment */ ",
                "\t/* comment */",
                "  /* comment */\t\t"
            )
        ) { checkSingleCommentLineStats(it) }
    }

    @Test
    fun emptySingleCommentLineTest() {
        checkArrayOfLines(
            arrayOf(
                " // ",
                "\t//",
                "  //\t\t"
            )
        ) { checkEmptyLineStats(it) }
    }

    @Test
    fun emptyCommentBlockLineTest() {
        checkArrayOfLines(
            arrayOf(
                " /**/ ",
                " /*    */ ",
                "\t/*  */",
                "  /*  */\t\t",
                " /* \t */"
            )
        ) { checkEmptyLineStats(it) }
    }

    @Test
    fun multipleCommentBlocksLineTest() {
        checkArrayOfLines(
            arrayOf(
                " /* aaa */ /* 123 */ /* 12345 */ /* 345 */ ",
                " /* aaa */ /**/ /* 123 */ /* 12345 */ /* 345 */ ",
                " \t\t/* aaa */ /*\t*/ /* 123 */ /* 12345 */ /* 345 */ ",
                " /* aaa */ /* 123 */ /*  */ /* 12345 */ /**/ /* 345 */\t\t"
            )
        ) { checkCommentBlockLineStats(it, 4, false) }

        checkArrayOfLines(
            arrayOf(
                " /* WWW */ /* test */ /* 111222555 ",
                "\t\t\t/* WWW */ /* test */ /* 111222555",
                "/* WWW */ /* test */ /* 111222555\t\t",
                "  /* WWW */ /* test */ /* 111222555   "
            )
        ) { checkCommentBlockLineStats(it, 3, true) }
    }

    @Test
    fun mixedCommentsLineTest() {
        checkArrayOfLines(
            arrayOf(
                " /* aaa */ //12213",
                " /* aaa */ //12213  ",
                " /* aaa */ //  12213  ",
                " /* aaa */ //  12213\t",
                " /* aaa */ //\t12213\t",
                " /* aaa */ //\t12213",
                " /* aaa */ //\t  12213"
            )
        ) { checkCommentBlockLineStats(it, 2, false) }

        checkArrayOfLines(
            arrayOf(
                " /* aaa */ //",
                " /* aaa */ //   ",
                " /* aaa */ //\t",
                " /* aaa */ //\t\t",
                " /* aaa */ //\t\t\t  ",
                " /* aaa */ // \t  "
            )
        ) { checkCommentBlockLineStats(it, 1, false) }

        checkArrayOfLines(
            arrayOf(
                " // /* 12345",
                " // /* 12345 */"
            )
        ) { checkCommentBlockLineStats(it, 1, false) }

        checkArrayOfLines(
            arrayOf(
                " /* 12345 //",
                " /* 12345 /////",
                " /* 12345 //\t\t\t",
                "\t/* 12345 //   ",
                "\t/* 12345\t//   "
            )
        ) { checkCommentBlockLineStats(it, 1, true) }
    }

    @Test
    fun mixedLineTest() {
        checkArrayOfLines(
            arrayOf(
                "val f = 1; /* 12345 */ val i = run();",
                "\t\tval f = 1; /* 12345 */ val i = run();\t",
                "  val f = 1; /* 12345 */ val i = run();  ",
                "  val f = 1; /* 12345 */\t\tval i = run()  ",
                "\tval f = 1; /* 12345 */ val i = run()  //",
                "val f = 1; /* 12345 */ val i = run()  //\t",
                "val f = 1; val i = run()  // 12345"
            )
        ) { checkMixedLineStats(it, 2, 1, false) }

        checkArrayOfLines(
            arrayOf(
                "val f = 1; /* 12345 */ val i = run(); /*",
                "val f = 1; /* 12345 */ val i = run(); /*\t\t\t",
                "val f = 1; /* 12345 */ val i = run(); /*   ",
                "   val f = 1; /* 12345 */ val i = run(); /*   ",
                "\t\tval f = 1; /* 12345 */ val i = run(); /*"
            )
        ) { checkMixedLineStats(it, 2, 1, true) }
    }

    @Test
    fun utf16LineTest() {
        checkArrayOfLines(
            arrayOf(
                "//世界",
                "\t\t\t//世界",
                "    //世界",
                "  \t//世界",
                "  \t//世界 \t\t",
                "  \t/* 世界 \t\t */",
                "  /* 世界 */  "
            )
        ) { checkSingleCommentLineStats(it) }
    }
}