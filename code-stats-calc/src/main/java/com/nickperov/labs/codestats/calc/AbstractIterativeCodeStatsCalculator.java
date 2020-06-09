package com.nickperov.labs.codestats.calc;

import com.nickperov.labs.codestats.calc.model.SourceCodeStats;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractIterativeCodeStatsCalculator<T extends SourceCodeStats> extends AbstractCodeStatsCalculator<T> {
    
    static List<File> collectSrcFiles(File directory) {
        List<File> srcFiles = new ArrayList<>();
        collectSrcFiles(directory, srcFiles);
        return srcFiles;
    }
    
    private static void collectSrcFiles(File directory, List<File> srcFilesAccumulator) {
        if (!directory.isDirectory())
            return;

        List<File> directories = Stream.of(Objects.requireNonNull(directory.listFiles()))
                .filter(File::isDirectory).collect(Collectors.toList());
        List<File> srcFiles = Stream.of(Objects.requireNonNull(directory.listFiles()))
                .filter(File::isFile).filter(file -> checkFileName(file.getName())).collect(Collectors.toList());

        srcFilesAccumulator.addAll(srcFiles);

        for (File dir : directories)
            collectSrcFiles(dir, srcFilesAccumulator);
    }
}
