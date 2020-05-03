package com.nickperov.labs.codestats.calc;

import com.nickperov.labs.codestats.calc.model.CodeStats;
import com.nickperov.labs.codestats.calc.model.MutableCodeStats;

import java.io.File;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class StreamSeqMutableCodeStatsCalculator extends AbstractStreamSeqCodeStatsCalculator<MutableCodeStats> {

    @Override
    MutableCodeStats initCodeStats() {
        return new MutableCodeStats();
    }

    @Override
    public Stream<CodeStats> buildStream(File file) {
        return super.buildStream(file).map(cs -> (MutableCodeStats) cs);
    }

    @Override
    CodeStats calcDirectory(File file, Supplier<MutableCodeStats> codeStatsSupplier) {
        return buildStream(file).collect(codeStatsSupplier, MutableCodeStats::append, MutableCodeStats::append);
    }

    @Override
    MutableCodeStats buildCodeStats(int numOfFiles, long numOfCodeLines, long numberOfCommentLines) {
        return new MutableCodeStats(numOfFiles, numOfCodeLines, numberOfCommentLines);
    }
}