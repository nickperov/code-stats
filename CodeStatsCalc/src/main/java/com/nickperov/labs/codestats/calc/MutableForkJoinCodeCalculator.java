package com.nickperov.labs.codestats.calc;

import com.nickperov.labs.codestats.calc.model.SourceCodeStats;
import com.nickperov.labs.codestats.calc.model.MutableAtomicSourceCodeStats;
import kotlin.Pair;

import java.io.File;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.nickperov.labs.codestats.calc.MutableForkJoinCodeCalculator.MutableCodeStatsTask;

public class MutableForkJoinCodeCalculator extends AbstractForkJoinCodeCalculator<MutableAtomicSourceCodeStats, MutableCodeStatsTask> {

    private final MutableAtomicSourceCodeStats codeStatsAccumulator = new MutableAtomicSourceCodeStats(0, 0L, 0L);

    public MutableForkJoinCodeCalculator() {
    }

    public MutableForkJoinCodeCalculator(int threshold) {
        super(threshold);
    }

    @Override
    MutableCodeStatsTask getRecursiveTask(List<File> srcFiles) {
        return new MutableCodeStatsTask(thresholdFunction.apply(srcFiles.size()), srcFiles, 0, srcFiles.size(), this::calcSourceFile, this::initCodeStats);
    }

    @Override
    MutableAtomicSourceCodeStats initCodeStats() {
        return codeStatsAccumulator;
    }

    static class MutableCodeStatsTask extends CodeStatsTask<MutableAtomicSourceCodeStats> {

        public MutableCodeStatsTask(int threshold, List<File> srcFiles, int start, int end, Function<File, SourceCodeStats> srcFileCalculator, Supplier<MutableAtomicSourceCodeStats> codeStatsSupplier) {
            super(threshold, srcFiles, start, end, srcFileCalculator, codeStatsSupplier);
        }

        @Override
        Pair<CodeStatsTask<MutableAtomicSourceCodeStats>, CodeStatsTask<MutableAtomicSourceCodeStats>> createSubTasks(int middle) {
            return new Pair<>(new MutableCodeStatsTask(this.threshold, srcFiles, start, middle, this.srcFileCalculator, this.codeStatsSupplier),
                    new MutableCodeStatsTask(this.threshold, srcFiles, middle, end, this.srcFileCalculator, this.codeStatsSupplier));
        }

        @Override
        MutableAtomicSourceCodeStats joinResults(MutableAtomicSourceCodeStats first, SourceCodeStats second) {
            return this.codeStatsSupplier.get();
        }

        @Override
        MutableAtomicSourceCodeStats computeCodeStats() {
            final var currentCodeStats = codeStatsSupplier.get();
            for (int i = start; i < end; i++) {
                currentCodeStats.append(srcFileCalculator.apply(srcFiles.get(i)));
            }
            return currentCodeStats;
        }
    }
}
