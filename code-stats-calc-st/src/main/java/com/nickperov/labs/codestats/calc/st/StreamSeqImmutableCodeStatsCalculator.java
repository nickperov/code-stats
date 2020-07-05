package com.nickperov.labs.codestats.calc.st;

import com.nickperov.labs.codestats.calc.base.model.ImmutableSourceCodeStats;
import com.nickperov.labs.codestats.calc.base.model.SourceCodeStats;

import java.io.File;
import java.util.function.Supplier;

public class StreamSeqImmutableCodeStatsCalculator extends AbstractStreamSeqCodeStatsCalculator<ImmutableSourceCodeStats> {

    @Override
    protected ImmutableSourceCodeStats initCodeStats() {
        return buildCodeStats(0, 0L, 0L);
    }


    @Override
    protected SourceCodeStats calcDirectory(final File file, final Supplier<ImmutableSourceCodeStats> codeStatsSupplier) {
        return buildStream(file).map(cs -> (ImmutableSourceCodeStats) cs).reduce(codeStatsSupplier.get(), ImmutableSourceCodeStats::append);
    }

    @Override
    protected ImmutableSourceCodeStats buildCodeStats(final int numOfFiles, final long numOfCodeLines, final long numberOfCommentLines) {
        return new ImmutableSourceCodeStats(numOfFiles, numOfCodeLines, numberOfCommentLines);
    }
}