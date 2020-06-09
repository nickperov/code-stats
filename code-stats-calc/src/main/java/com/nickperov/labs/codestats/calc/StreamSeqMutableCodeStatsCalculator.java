package com.nickperov.labs.codestats.calc;

import com.nickperov.labs.codestats.calc.model.SourceCodeStats;
import com.nickperov.labs.codestats.calc.model.MutableSourceCodeStats;

import java.io.File;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class StreamSeqMutableCodeStatsCalculator extends AbstractStreamSeqCodeStatsCalculator<MutableSourceCodeStats> {

    @Override
    MutableSourceCodeStats initCodeStats() {
        return new MutableSourceCodeStats();
    }

    @Override
    public Stream<SourceCodeStats> buildStream(File file) {
        return super.buildStream(file).map(cs -> (MutableSourceCodeStats) cs);
    }

    @Override
    SourceCodeStats calcDirectory(File file, Supplier<MutableSourceCodeStats> codeStatsSupplier) {
        return buildStream(file).collect(codeStatsSupplier, MutableSourceCodeStats::append, MutableSourceCodeStats::append);
    }

    @Override
    MutableSourceCodeStats buildCodeStats(int numOfFiles, long numOfCodeLines, long numberOfCommentLines) {
        return new MutableSourceCodeStats(numOfFiles, numOfCodeLines, numberOfCommentLines);
    }
}