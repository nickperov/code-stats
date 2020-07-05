package com.nickperov.labs.codestats.calc.mt;

import com.nickperov.labs.codestats.calc.base.model.MutableAtomicSourceCodeStats;
import com.nickperov.labs.codestats.calc.base.model.SourceCodeStats;

import java.io.File;
import java.util.function.Supplier;

public class StreamParallelMutableCodeStatsCalculator extends AbstractStreamParallelCodeStatsCalculator<MutableAtomicSourceCodeStats> {

    @Override
    protected MutableAtomicSourceCodeStats initCodeStats() {
        return new MutableAtomicSourceCodeStats(0, 0L, 0L);
    }

    @Override
    protected SourceCodeStats calcDirectory(final File file, final Supplier<MutableAtomicSourceCodeStats> codeStatsSupplier) {
        return buildStream(file).collect(codeStatsSupplier, MutableAtomicSourceCodeStats::append, MutableAtomicSourceCodeStats::append);
    }
}