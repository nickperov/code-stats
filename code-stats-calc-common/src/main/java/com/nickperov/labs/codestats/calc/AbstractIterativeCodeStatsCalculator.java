package com.nickperov.labs.codestats.calc;

import com.nickperov.labs.codestats.calc.base.AbstractCodeStatsCalculator;
import com.nickperov.labs.codestats.calc.base.model.SourceCodeStats;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public abstract class AbstractIterativeCodeStatsCalculator<T extends SourceCodeStats> extends AbstractCodeStatsCalculator<T> {

    protected static List<File> collectSrcFiles(final File directory) {
        final List<File> srcFiles = new ArrayList<>();
        collectSrcFiles(directory, srcFiles);
        return srcFiles;
    }

    private static void collectSrcFiles(final File directory, final List<File> srcFilesAccumulator) {
        if (!directory.isDirectory())
            return;

        final List<File> directories = Stream.of(Objects.requireNonNull(directory.listFiles()))
                .filter(File::isDirectory).toList();
        final List<File> srcFiles = Stream.of(Objects.requireNonNull(directory.listFiles()))
                .filter(File::isFile).filter(file -> AbstractCodeStatsCalculator.checkFileName(file.getName())).toList();

        srcFilesAccumulator.addAll(srcFiles);

        for (final File dir : directories)
            collectSrcFiles(dir, srcFilesAccumulator);
    }
}
