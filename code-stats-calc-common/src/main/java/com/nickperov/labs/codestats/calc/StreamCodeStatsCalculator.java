package com.nickperov.labs.codestats.calc;

import com.nickperov.labs.codestats.calc.base.model.SourceCodeStats;

import java.io.File;
import java.util.function.Function;
import java.util.stream.Stream;

public interface StreamCodeStatsCalculator {

    default Stream<? extends SourceCodeStats> buildStream(final File file) {
        return setStreamType(AbstractIterativeCodeStatsCalculator.collectSrcFiles(file).stream()
                .map(getMappingFunction()));
    }

    Stream<? extends SourceCodeStats> setStreamType(Stream<? extends SourceCodeStats> stream);

    Function<File, SourceCodeStats> getMappingFunction();
}