package com.nickperov.labs.codestats.calc;

import com.nickperov.labs.codestats.calc.model.SourceCodeStats;

import java.io.File;
import java.util.function.Function;;
import java.util.stream.Stream;

public abstract class AbstractStreamParallelCodeStatsCalculator<T extends SourceCodeStats> extends AbstractIterativeCodeStatsCalculator<T> implements StreamCodeStatsCalculator {

    @Override
    public Stream<? extends SourceCodeStats> setStreamType(Stream<? extends SourceCodeStats> stream) {
        return stream.parallel();
    }

    @Override
    public Function<File, SourceCodeStats> getMappingFunction() {
        return this::calcSourceFile;
    }

    SourceCodeStats buildCodeStats(int numOfFiles, long numOfCodeLines, long numberOfCommentLines) {
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