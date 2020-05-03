package com.nickperov.labs.codestats.calc;

import com.nickperov.labs.codestats.calc.model.SourceCodeStats;
import com.nickperov.labs.codestats.calc.model.MutableAtomicSourceCodeStats;

import java.io.File;
import java.util.function.Supplier;

public class StreamParallelMutableCodeStatsCalculator extends AbstractStreamParallelCodeStatsCalculator<MutableAtomicSourceCodeStats> {

    @Override
    MutableAtomicSourceCodeStats initCodeStats() {
        return new MutableAtomicSourceCodeStats(0, 0L, 0L);
    }

    @Override
    SourceCodeStats calcDirectory(File file, Supplier<MutableAtomicSourceCodeStats> codeStatsSupplier) {
        return buildStream(file).collect(codeStatsSupplier, MutableAtomicSourceCodeStats::append, MutableAtomicSourceCodeStats::append);
    }
}