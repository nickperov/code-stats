package com.nickperov.labs.codestats.calc.benchmark;

import com.nickperov.labs.codestats.calc.base.model.SourceCodeStats;
import com.nickperov.labs.codestats.calc.mt.MutableForkJoinCodeCalculator;
import com.nickperov.labs.codestats.calc.st.RecursiveImmutableCodeStatsCalculator;
import org.openjdk.jmh.annotations.*;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Fork(value = 2, warmups = 1)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class CodeStatsParallelVsSeqBenchmark {

    private final String path = "../../";

    @Benchmark
    public SourceCodeStats calcCodeStatsSTRecursiveImmutable() {
        return new RecursiveImmutableCodeStatsCalculator().calcDirectory(new File(this.path));
    }

    @Benchmark
    public SourceCodeStats calcCodeStatsForkJoinMutable() {
        return new MutableForkJoinCodeCalculator(10).calcDirectory(new File(this.path));
    }
}

