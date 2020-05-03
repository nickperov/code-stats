package com.nickperov.labs.codestats.calc;

import com.nickperov.labs.codestats.calc.model.CodeStats;
import com.nickperov.labs.codestats.calc.model.ImmutableCodeStats;
import kotlin.Pair;

import java.io.File;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.nickperov.labs.codestats.calc.ImmutableForkJoinCodeCalculator.ImmutableCodeStatsTask;

public class ImmutableForkJoinCodeCalculator extends AbstractForkJoinCodeCalculator<ImmutableCodeStats, ImmutableCodeStatsTask> {

    public ImmutableForkJoinCodeCalculator() {
    }

    public ImmutableForkJoinCodeCalculator(int threshold) {
        super(threshold);
    }

    @Override
    ImmutableCodeStatsTask getRecursiveTask(List<File> srcFiles) {
        return new ImmutableCodeStatsTask(thresholdFunction.apply(srcFiles.size()), srcFiles, 0, srcFiles.size(), this::calcSourceFile, this::initCodeStats);
    }

    @Override
    ImmutableCodeStats initCodeStats() {
        return new ImmutableCodeStats(0, 0L, 0L);
    }

    static class ImmutableCodeStatsTask extends CodeStatsTask<ImmutableCodeStats> {

        public ImmutableCodeStatsTask(int threshold, List<File> srcFiles, int start, int end, Function<File, CodeStats> srcFileCalculator, Supplier<ImmutableCodeStats> codeStatsSupplier) {
            super(threshold, srcFiles, start, end, srcFileCalculator, codeStatsSupplier);
        }

        @Override
        Pair<CodeStatsTask<ImmutableCodeStats>, CodeStatsTask<ImmutableCodeStats>> createSubTasks(int middle) {
            return new Pair<>(new ImmutableCodeStatsTask(this.threshold, srcFiles, start, middle, this.srcFileCalculator, this.codeStatsSupplier),
                    new ImmutableCodeStatsTask(this.threshold, srcFiles, middle, end, this.srcFileCalculator, this.codeStatsSupplier));
        }

        @Override
        ImmutableCodeStats joinResults(ImmutableCodeStats first, CodeStats second) {
            return first.append(second);
        }

        @Override
        ImmutableCodeStats computeCodeStats() {
            var currentCodeStats = codeStatsSupplier.get();
            for (int i = start; i < end; i++) {
                currentCodeStats = currentCodeStats.append(srcFileCalculator.apply(srcFiles.get(i)));
            }
            return currentCodeStats;
        }
    }
}