package com.nickperov.labs.codestats.calc.st;

import com.nickperov.labs.codestats.calc.AbstractIterativeCodeStatsCalculator;
import com.nickperov.labs.codestats.calc.StreamCodeStatsCalculator;
import com.nickperov.labs.codestats.calc.base.model.SourceCodeStats;

import java.io.File;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class AbstractStreamSeqCodeStatsCalculator<T extends SourceCodeStats> extends AbstractIterativeCodeStatsCalculator<T> implements StreamCodeStatsCalculator {

    @Override
    public Stream<? extends SourceCodeStats> setStreamType(final Stream<? extends SourceCodeStats> stream) {
        return stream.sequential();
    }

    @Override
    public Function<File, SourceCodeStats> getMappingFunction() {
        return this::calcSourceFile;
    }

    @Override
    protected abstract T buildCodeStats(int numOfFiles, long numOfCodeLines, long numberOfCommentLines);
}