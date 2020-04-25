package com.nickperov.labs.codestats.calc;

import com.nickperov.labs.codestats.calc.model.CodeStats;

import java.io.File;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class AbstractStreamSeqCodeStatsCalculator<T extends CodeStats> extends AbstractIterativeCodeStatsCalculator<T> implements StreamCodeStatsCalculator {

    @Override
    public Stream<? extends CodeStats> setStreamType(Stream<? extends CodeStats> stream) {
        return stream.sequential();
    }

    @Override
    public Function<File, CodeStats> getMappingFunction() {
        return this::calcSourceFile;
    }

    @Override
    abstract T buildCodeStats(int numOfFiles, long numOfLines);
}