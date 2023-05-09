@file:OptIn(DelicateCoroutinesApi::class)

package com.nickperov.labs.codestats.calc.cli

import com.nickperov.labs.codestats.calc.base.model.SourceCodeStats
import com.nickperov.labs.codestats.calc.coroutines.CoroutinesImmutableCodeStatsCalculator
import kotlinx.cli.*
import kotlinx.coroutines.*
import java.io.File
import java.util.*

fun main(args: Array<String>) {

    val progress = charArrayOf('|', '/', '-', '\\')
    val codeStats = CoroutinesImmutableCodeStatsCalculator()

    val parser = ArgParser("code-stats")
    val directory by parser.option(ArgType.String, shortName = "d", description = "Input directory").required()

    try {
        parser.parse(args)
    } catch (e: Exception) {
        println(e.message)
        return
    }

    val dir = File(directory)
    if (!dir.exists() && !dir.isDirectory) {
        println("Directory does not exists")
        return
    }

    try {
        var result: Optional<SourceCodeStats> = Optional.empty()
        val calculation = GlobalScope.launch(Dispatchers.IO) {
            result = Optional.of(codeStats.calcDirectory(dir, this))
            return@launch
        }

        runBlocking {
            var progressIndex = 0
            while (true) {
                if (calculation.isCompleted) {
                    print("\r")
                    break
                } else if (calculation.isCancelled) {
                    throw RuntimeException("Calculation cancelled")
                } else {
                    delay(100L)
                    print("\r Calculating..." + progress[(progressIndex++) % progress.size])
                }
            }
        }

        val statsResult = result.orElseThrow()

        println("Code stats result for directory: " + dir.absoluteFile.normalize().path)
        println("   Number of source files: " + statsResult.numberOfFiles())
        println("   Number of code lines: " + statsResult.numberOfCodeLines())
        println("   Number of comment lines: " + statsResult.numberOfCommentLines())
    } catch (e: Exception) {
        println("Failed to calulate: " + e.message)
        return
    }
}