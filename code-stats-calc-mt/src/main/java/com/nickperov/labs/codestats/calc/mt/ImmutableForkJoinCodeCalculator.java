package com.nickperov.labs.codestats.calc.mt;

import com.nickperov.labs.codestats.calc.base.model.ImmutableSourceCodeStats;
import com.nickperov.labs.codestats.calc.base.model.SourceCodeStats;
import kotlin.Pair;

import java.io.File;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.nickperov.labs.codestats.calc.mt.ImmutableForkJoinCodeCalculator.ImmutableCodeStatsTask;

public class ImmutableForkJoinCodeCalculator extends AbstractForkJoinCodeCalculator<ImmutableSourceCodeStats, ImmutableCodeStatsTask> {

    public ImmutableForkJoinCodeCalculator() {
    }

    public ImmutableForkJoinCodeCalculator(final int threshold) {
        super(threshold);
    }

    @Override
    ImmutableCodeStatsTask getRecursiveTask(final List<File> srcFiles) {
        return new ImmutableCodeStatsTask(this.thresholdFunction.apply(srcFiles.size()), srcFiles, 0, srcFiles.size(), this::calcSourceFile, this::initCodeStats);
    }

    @Override
    protected ImmutableSourceCodeStats initCodeStats() {
        return new ImmutableSourceCodeStats(0, 0L, 0L);
    }

    static class ImmutableCodeStatsTask extends CodeStatsTask<ImmutableSourceCodeStats> {

        public ImmutableCodeStatsTask(final int threshold, final List<File> srcFiles, final int start, final int end, final Function<File, SourceCodeStats> srcFileCalculator, final Supplier<ImmutableSourceCodeStats> codeStatsSupplier) {
            super(threshold, srcFiles, start, end, srcFileCalculator, codeStatsSupplier);
        }

        @Override
        Pair<CodeStatsTask<ImmutableSourceCodeStats>, CodeStatsTask<ImmutableSourceCodeStats>> createSubTasks(final int middle) {
            return new Pair<>(new ImmutableCodeStatsTask(this.threshold, this.srcFiles, this.start, middle, this.srcFileCalculator, this.codeStatsSupplier),
                    new ImmutableCodeStatsTask(this.threshold, this.srcFiles, middle, this.end, this.srcFileCalculator, this.codeStatsSupplier));
        }

        @Override
        ImmutableSourceCodeStats joinResults(final ImmutableSourceCodeStats first, final SourceCodeStats second) {
            return first.append(second);
        }

        @Override
        ImmutableSourceCodeStats computeCodeStats() {
            var currentCodeStats = this.codeStatsSupplier.get();
            for (int i = this.start; i < this.end; i++) {
                currentCodeStats = currentCodeStats.append(this.srcFileCalculator.apply(this.srcFiles.get(i)));
            }
            return currentCodeStats;
        }
    }
}