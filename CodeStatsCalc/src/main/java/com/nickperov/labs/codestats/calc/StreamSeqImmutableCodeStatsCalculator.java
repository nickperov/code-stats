package com.nickperov.labs.codestats.calc;

import com.nickperov.labs.codestats.calc.model.CodeStats;
import com.nickperov.labs.codestats.calc.model.ImmutableCodeStats;

import java.io.File;
import java.util.function.Supplier;

public class StreamSeqImmutableCodeStatsCalculator extends AbstractStreamSeqCodeStatsCalculator<ImmutableCodeStats> {

    @Override
    ImmutableCodeStats initCodeStats() {
        return buildCodeStats(0, 0L, 0L);
    }
    

    @Override
    CodeStats calcDirectory(File file, Supplier<ImmutableCodeStats> codeStatsSupplier) {
        return buildStream(file).map(cs -> (ImmutableCodeStats)cs).reduce(codeStatsSupplier.get(), ImmutableCodeStats::append);
    }

    @Override
    ImmutableCodeStats buildCodeStats(int numOfFiles, long numOfCodeLines, long numberOfCommentLines) {
        return new ImmutableCodeStats(numOfFiles, numOfCodeLines, numberOfCommentLines);
    }
}