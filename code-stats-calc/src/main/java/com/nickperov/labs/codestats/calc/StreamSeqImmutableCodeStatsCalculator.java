package com.nickperov.labs.codestats.calc;

import com.nickperov.labs.codestats.calc.model.SourceCodeStats;
import com.nickperov.labs.codestats.calc.model.ImmutableSourceCodeStats;

import java.io.File;
import java.util.function.Supplier;

public class StreamSeqImmutableCodeStatsCalculator extends AbstractStreamSeqCodeStatsCalculator<ImmutableSourceCodeStats> {

    @Override
    ImmutableSourceCodeStats initCodeStats() {
        return buildCodeStats(0, 0L, 0L);
    }
    

    @Override
    SourceCodeStats calcDirectory(File file, Supplier<ImmutableSourceCodeStats> codeStatsSupplier) {
        return buildStream(file).map(cs -> (ImmutableSourceCodeStats)cs).reduce(codeStatsSupplier.get(), ImmutableSourceCodeStats::append);
    }

    @Override
    ImmutableSourceCodeStats buildCodeStats(int numOfFiles, long numOfCodeLines, long numberOfCommentLines) {
        return new ImmutableSourceCodeStats(numOfFiles, numOfCodeLines, numberOfCommentLines);
    }
}