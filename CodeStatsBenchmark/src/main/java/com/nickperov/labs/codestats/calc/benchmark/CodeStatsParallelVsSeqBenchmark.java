package com.nickperov.labs.codestats.calc.benchmark;

import com.nickperov.labs.codestats.calc.MutableForkJoinCodeCalculator;
import com.nickperov.labs.codestats.calc.RecursiveImmutableCodeStatsCalculator;
import com.nickperov.labs.codestats.calc.model.CodeStats;
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
    public CodeStats calcCodeStatsSTRecursiveImmutable() {
        return new RecursiveImmutableCodeStatsCalculator().calcDirectory(new File(path));
    }

    @Benchmark
    public CodeStats calcCodeStatsForkJoinMutable() {
        return new MutableForkJoinCodeCalculator(10).calcDirectory(new File(path));
    }
}

