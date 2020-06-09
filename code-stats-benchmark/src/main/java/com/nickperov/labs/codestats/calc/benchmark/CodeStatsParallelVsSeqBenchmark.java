package com.nickperov.labs.codestats.calc.benchmark;

import com.nickperov.labs.codestats.calc.MutableForkJoinCodeCalculator;
import com.nickperov.labs.codestats.calc.RecursiveImmutableCodeStatsCalculator;
import com.nickperov.labs.codestats.calc.model.SourceCodeStats;
import org.openjdk.jmh.annotations.*;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Fork(value = 1)
@Measurement(iterations = 5)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class CodeStatsParallelVsSeqBenchmark {
    
    private String path = "../../";

    @Benchmark
    public SourceCodeStats calcCodeStatsSTRecursiveImmutable() {
        return new RecursiveImmutableCodeStatsCalculator().calcDirectory(new File(path));
    }

    @Benchmark
    public SourceCodeStats calcCodeStatsForkJoinMutable() {
        return new MutableForkJoinCodeCalculator(10).calcDirectory(new File(path));
    }
}

