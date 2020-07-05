package com.nickperov.labs.codestats.calc.mt;

import com.nickperov.labs.codestats.calc.AbstractIterativeCodeStatsCalculator;
import com.nickperov.labs.codestats.calc.base.model.SourceCodeStats;
import kotlin.Pair;

import java.io.File;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.nickperov.labs.codestats.calc.mt.AbstractForkJoinCodeCalculator.CodeStatsTask;

public abstract class AbstractForkJoinCodeCalculator<T extends SourceCodeStats, RT extends CodeStatsTask<T>> extends AbstractIterativeCodeStatsCalculator<T> {

    final Function<Integer, Integer> thresholdFunction;

    protected AbstractForkJoinCodeCalculator() {
        this.thresholdFunction = getThresholdFunction();
    }

    protected AbstractForkJoinCodeCalculator(final int threshold) {
        this.thresholdFunction = numberOfFiles -> threshold;
    }

    public Function<Integer, Integer> getThresholdFunction() {
        return numberOfFiles -> Math.min(10000, Math.toIntExact(numberOfFiles * 10 / Runtime.getRuntime().availableProcessors()));
    }

    @Override
    protected SourceCodeStats calcDirectory(final File file, final Supplier<T> codeStatsSupplier) {
        final RT recursiveTask = getRecursiveTask(collectSrcFiles(file));
        final ForkJoinPool pool = new ForkJoinPool();
        return pool.invoke(recursiveTask);
    }

    abstract RT getRecursiveTask(List<File> srcFiles);

    static abstract class CodeStatsTask<T extends SourceCodeStats> extends RecursiveTask<T> {

        final int threshold;
        final int start;
        final int end;
        final List<File> srcFiles;
        final Function<File, SourceCodeStats> srcFileCalculator;
        final Supplier<T> codeStatsSupplier;

        public CodeStatsTask(final int threshold, final List<File> srcFiles, final int start, final int end, final Function<File, SourceCodeStats> srcFileCalculator, final Supplier<T> codeStatsSupplier) {
            this.threshold = threshold;
            this.srcFiles = srcFiles;
            this.start = start;
            this.end = end;
            this.srcFileCalculator = srcFileCalculator;
            this.codeStatsSupplier = codeStatsSupplier;
        }

        abstract Pair<CodeStatsTask<T>, CodeStatsTask<T>> createSubTasks(int middle);

        abstract T joinResults(T first, SourceCodeStats second);

        abstract T computeCodeStats();

        @Override
        protected T compute() {
            if ((this.end - this.start) > this.threshold) {
                // Split
                final int middle = this.start + (this.end - this.start) / 2;
                final var tasks = createSubTasks(middle);
                final var firstTask = tasks.getFirst();
                final var secondTask = tasks.getSecond();
                secondTask.fork();
                return joinResults(firstTask.compute(), secondTask.join());
            } else {
                // Calc
                return computeCodeStats();
            }
        }
    }
}