package com.nickperov.labs.codestats.calc.mt;

import com.nickperov.labs.codestats.calc.base.model.ImmutableSourceCodeStats;
import com.nickperov.labs.codestats.calc.base.model.SourceCodeStats;

import java.io.File;
import java.util.function.Supplier;

public class StreamParallelImmutableCodeStatsCalculator extends AbstractStreamParallelCodeStatsCalculator<ImmutableSourceCodeStats> {

    @Override
    protected ImmutableSourceCodeStats initCodeStats() {
        return buildCodeStats(0, 0L, 0L);
    }

    @Override
    protected SourceCodeStats calcDirectory(final File file, final Supplier<ImmutableSourceCodeStats> codeStatsSupplier) {
        return buildStream(file).map(cs -> (ImmutableSourceCodeStats) cs).reduce(ImmutableSourceCodeStats::append).orElseGet(codeStatsSupplier);
    }

    @Override
    protected ImmutableSourceCodeStats buildCodeStats(final int numOfFiles, final long numOfCodeLines, final long numberOfCommentLines) {
        return new ImmutableSourceCodeStats(numOfFiles, numOfCodeLines, numberOfCommentLines);
    }
}
