package com.nickperov.labs.codestats.calc;

import com.nickperov.labs.codestats.calc.model.CodeStats;
import com.nickperov.labs.codestats.calc.model.MutableAtomicCodeStats;

import java.io.File;
import java.util.function.Supplier;

public class StreamParallelMutableCodeStatsCalculator extends AbstractStreamParallelCodeStatsCalculator<MutableAtomicCodeStats> {

    @Override
    MutableAtomicCodeStats initCodeStats() {
        return new MutableAtomicCodeStats(0, 0L);
    }

    @Override
    CodeStats calcDirectory(File file, Supplier<MutableAtomicCodeStats> codeStatsSupplier) {
        return buildStream(file).collect(codeStatsSupplier, MutableAtomicCodeStats::append, MutableAtomicCodeStats::append);
    }
}