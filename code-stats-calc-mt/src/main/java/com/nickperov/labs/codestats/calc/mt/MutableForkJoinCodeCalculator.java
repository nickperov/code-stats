package com.nickperov.labs.codestats.calc.mt;

import com.nickperov.labs.codestats.calc.base.model.MutableAtomicSourceCodeStats;
import com.nickperov.labs.codestats.calc.base.model.SourceCodeStats;
import kotlin.Pair;

import java.io.File;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.nickperov.labs.codestats.calc.mt.MutableForkJoinCodeCalculator.MutableCodeStatsTask;

public class MutableForkJoinCodeCalculator extends AbstractForkJoinCodeCalculator<MutableAtomicSourceCodeStats, MutableCodeStatsTask> {

    private final MutableAtomicSourceCodeStats codeStatsAccumulator = new MutableAtomicSourceCodeStats(0, 0L, 0L);

    public MutableForkJoinCodeCalculator() {
    }

    public MutableForkJoinCodeCalculator(final int threshold) {
        super(threshold);
    }

    @Override
    MutableCodeStatsTask getRecursiveTask(final List<File> srcFiles) {
        return new MutableCodeStatsTask(this.thresholdFunction.apply(srcFiles.size()), srcFiles, 0, srcFiles.size(), this::calcSourceFile, this::initCodeStats);
    }

    @Override
    protected MutableAtomicSourceCodeStats initCodeStats() {
        return this.codeStatsAccumulator;
    }

    static class MutableCodeStatsTask extends CodeStatsTask<MutableAtomicSourceCodeStats> {

        public MutableCodeStatsTask(final int threshold, final List<File> srcFiles, final int start, final int end, final Function<File, SourceCodeStats> srcFileCalculator, final Supplier<MutableAtomicSourceCodeStats> codeStatsSupplier) {
            super(threshold, srcFiles, start, end, srcFileCalculator, codeStatsSupplier);
        }

        @Override
        Pair<CodeStatsTask<MutableAtomicSourceCodeStats>, CodeStatsTask<MutableAtomicSourceCodeStats>> createSubTasks(final int middle) {
            return new Pair<>(new MutableCodeStatsTask(this.threshold, this.srcFiles, this.start, middle, this.srcFileCalculator, this.codeStatsSupplier),
                    new MutableCodeStatsTask(this.threshold, this.srcFiles, middle, this.end, this.srcFileCalculator, this.codeStatsSupplier));
        }

        @Override
        MutableAtomicSourceCodeStats joinResults(final MutableAtomicSourceCodeStats first, final SourceCodeStats second) {
            return this.codeStatsSupplier.get();
        }

        @Override
        MutableAtomicSourceCodeStats computeCodeStats() {
            final var currentCodeStats = this.codeStatsSupplier.get();
            for (int i = this.start; i < this.end; i++) {
                currentCodeStats.append(this.srcFileCalculator.apply(this.srcFiles.get(i)));
            }
            return currentCodeStats;
        }
    }
}
