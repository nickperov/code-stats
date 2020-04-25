package com.nickperov.labs.codestats.calc;

import com.nickperov.labs.codestats.calc.model.CodeStats;

import java.io.File;
import java.util.function.Function;
import java.util.stream.Stream;

public interface StreamCodeStatsCalculator {

    default Stream<? extends CodeStats> buildStream(File file) {
        return setStreamType(AbstractIterativeCodeStatsCalculator.collectSrcFiles(file).stream()
                .map(getMappingFunction()))/*.map(cs -> (T) cs)*/;
    }
    
    Stream<? extends CodeStats> setStreamType(Stream<? extends CodeStats> stream);
    
    Function<File, CodeStats> getMappingFunction();
}