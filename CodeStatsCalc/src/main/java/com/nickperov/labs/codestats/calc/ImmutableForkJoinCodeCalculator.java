package com.nickperov.labs.codestats.calc;

import com.nickperov.labs.codestats.calc.model.SourceCodeStats;
import com.nickperov.labs.codestats.calc.model.ImmutableSourceCodeStats;
import kotlin.Pair;

import java.io.File;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.nickperov.labs.codestats.calc.ImmutableForkJoinCodeCalculator.ImmutableCodeStatsTask;

public class ImmutableForkJoinCodeCalculator extends AbstractForkJoinCodeCalculator<ImmutableSourceCodeStats, ImmutableCodeStatsTask> {

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
    ImmutableSourceCodeStats initCodeStats() {
        return new ImmutableSourceCodeStats(0, 0L, 0L);
    }

    static class ImmutableCodeStatsTask extends CodeStatsTask<ImmutableSourceCodeStats> {

        public ImmutableCodeStatsTask(int threshold, List<File> srcFiles, int start, int end, Function<File, SourceCodeStats> srcFileCalculator, Supplier<ImmutableSourceCodeStats> codeStatsSupplier) {
            super(threshold, srcFiles, start, end, srcFileCalculator, codeStatsSupplier);
        }

        @Override
        Pair<CodeStatsTask<ImmutableSourceCodeStats>, CodeStatsTask<ImmutableSourceCodeStats>> createSubTasks(int middle) {
            return new Pair<>(new ImmutableCodeStatsTask(this.threshold, srcFiles, start, middle, this.srcFileCalculator, this.codeStatsSupplier),
                    new ImmutableCodeStatsTask(this.threshold, srcFiles, middle, end, this.srcFileCalculator, this.codeStatsSupplier));
        }

        @Override
        ImmutableSourceCodeStats joinResults(ImmutableSourceCodeStats first, SourceCodeStats second) {
            return first.append(second);
        }

        @Override
        ImmutableSourceCodeStats computeCodeStats() {
            var currentCodeStats = codeStatsSupplier.get();
            for (int i = start; i < end; i++) {
                currentCodeStats = currentCodeStats.append(srcFileCalculator.apply(srcFiles.get(i)));
            }
            return currentCodeStats;
        }
    }
}