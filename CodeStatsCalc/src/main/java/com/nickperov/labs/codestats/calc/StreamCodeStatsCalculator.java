package com.nickperov.labs.codestats.calc;

import com.nickperov.labs.codestats.calc.model.SourceCodeStats;

import java.io.File;
import java.util.function.Function;
import java.util.stream.Stream;

public interface StreamCodeStatsCalculator {

    default Stream<? extends SourceCodeStats> buildStream(File file) {
        return setStreamType(AbstractIterativeCodeStatsCalculator.collectSrcFiles(file).stream()
                .map(getMappingFunction()))/*.map(cs -> (T) cs)*/;
    }
    
    Stream<? extends SourceCodeStats> setStreamType(Stream<? extends SourceCodeStats> stream);
    
    Function<File, SourceCodeStats> getMappingFunction();
}