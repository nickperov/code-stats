package com.nickperov.labs.codestats.calc.cli

import com.nickperov.labs.codestats.calc.CoroutinesImmutableCodeStatsCalculator
import kotlinx.cli.*
import java.io.File

fun main(args: Array<String>) {

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
        // TODO add progress indicator 
        val result = codeStats.calcDirectory(dir)
        println("Code stats result for directory: " + dir.absoluteFile.normalize().path)
        println("   Number of source files: " + result.numberOfFiles())
        println("   Number of code lines: " + result.numberOfCodeLines())
        println("   Number of comment lines: " + result.numberOfCommentLines())
    } catch (e: Exception) {
        println("Failed to calulate: " + e.message)
        return
    }
}