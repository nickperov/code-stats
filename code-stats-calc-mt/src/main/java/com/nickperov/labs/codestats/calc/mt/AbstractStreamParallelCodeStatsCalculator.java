package com.nickperov.labs.codestats.calc.mt;

import com.nickperov.labs.codestats.calc.AbstractIterativeCodeStatsCalculator;
import com.nickperov.labs.codestats.calc.StreamCodeStatsCalculator;
import com.nickperov.labs.codestats.calc.base.model.SourceCodeStats;

import java.io.File;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class AbstractStreamParallelCodeStatsCalculator<T extends SourceCodeStats> extends AbstractIterativeCodeStatsCalculator<T> implements StreamCodeStatsCalculator {

    @Override
    public Stream<? extends SourceCodeStats> setStreamType(final Stream<? extends SourceCodeStats> stream) {
        return stream.parallel();
    }

    @Override
    public Function<File, SourceCodeStats> getMappingFunction() {
        return this::calcSourceFile;
    }

    @Override
    protected SourceCodeStats buildCodeStats(final int numOfFiles, final long numOfCodeLines, final long numberOfCommentLines) {
        return new SourceCodeStats() {
            @Override
            public int numberOfFiles() {
                return numOfFiles;
            }

            @Override
            public long numberOfCodeLines() {
                return numOfCodeLines;
            }

            @Override
            public long numberOfCommentLines() {
                return numberOfCommentLines;
            }
        };
    }
}