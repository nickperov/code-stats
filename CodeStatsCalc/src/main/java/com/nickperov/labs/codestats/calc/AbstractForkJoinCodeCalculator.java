package com.nickperov.labs.codestats.calc;

import com.nickperov.labs.codestats.calc.model.CodeStats;
import kotlin.Pair;

import java.io.File;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.nickperov.labs.codestats.calc.AbstractForkJoinCodeCalculator.CodeStatsTask;

public abstract class AbstractForkJoinCodeCalculator<T extends CodeStats, RT extends CodeStatsTask<T>> extends AbstractIterativeCodeStatsCalculator<T> {

    final Function<Integer, Integer> thresholdFunction;

    protected AbstractForkJoinCodeCalculator() {
        this.thresholdFunction = getThresholdFunction();
    }

    protected AbstractForkJoinCodeCalculator(int threshold) {
        this.thresholdFunction = numberOfFiles -> threshold;
    }

    public Function<Integer, Integer> getThresholdFunction() {
        return numberOfFiles -> Math.min(100000, Math.toIntExact(numberOfFiles * 10 / Runtime.getRuntime().availableProcessors()));
    }

    @Override
    CodeStats calcDirectory(File file, Supplier<T> codeStatsSupplier) {
        final RT recursiveTask = getRecursiveTask(collectSrcFiles(file));
        final ForkJoinPool pool = new ForkJoinPool();
        return pool.invoke(recursiveTask);
    }

    abstract RT getRecursiveTask(List<File> srcFiles);

    static abstract class CodeStatsTask<T extends CodeStats> extends RecursiveTask<T> {

        final int threshold;
        final int start;
        final int end;
        final List<File> srcFiles;
        final Function<File, CodeStats> srcFileCalculator;
        final Supplier<T> codeStatsSupplier;

        public CodeStatsTask(int threshold, List<File> srcFiles, int start, int end, Function<File, CodeStats> srcFileCalculator, Supplier<T> codeStatsSupplier) {
            this.threshold = threshold;
            this.srcFiles = srcFiles;
            this.start = start;
            this.end = end;
            this.srcFileCalculator = srcFileCalculator;
            this.codeStatsSupplier = codeStatsSupplier;
        }

        abstract Pair<CodeStatsTask<T>, CodeStatsTask<T>> createSubTasks(int middle);

        abstract T joinResults(T first, CodeStats second);

        abstract T computeCodeStats();

        @Override
        protected T compute() {
            if ((end - start) > threshold) {
                // Split
                final int middle = start + (end - start) / 2;
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