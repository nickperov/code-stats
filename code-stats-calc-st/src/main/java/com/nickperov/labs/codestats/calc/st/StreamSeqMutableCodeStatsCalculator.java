package com.nickperov.labs.codestats.calc.st;

import com.nickperov.labs.codestats.calc.base.model.MutableSourceCodeStats;
import com.nickperov.labs.codestats.calc.base.model.SourceCodeStats;

import java.io.File;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class StreamSeqMutableCodeStatsCalculator extends AbstractStreamSeqCodeStatsCalculator<MutableSourceCodeStats> {

    @Override
    protected MutableSourceCodeStats initCodeStats() {
        return new MutableSourceCodeStats();
    }

    @Override
    public Stream<SourceCodeStats> buildStream(final File file) {
        return super.buildStream(file).map(cs -> (MutableSourceCodeStats) cs);
    }

    @Override
    protected SourceCodeStats calcDirectory(final File file, final Supplier<MutableSourceCodeStats> codeStatsSupplier) {
        return buildStream(file).collect(codeStatsSupplier, MutableSourceCodeStats::append, MutableSourceCodeStats::append);
    }

    @Override
    protected MutableSourceCodeStats buildCodeStats(final int numOfFiles, final long numOfCodeLines, final long numberOfCommentLines) {
        return new MutableSourceCodeStats(numOfFiles, numOfCodeLines, numberOfCommentLines);
    }
}