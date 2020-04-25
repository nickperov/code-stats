package com.nickperov.labs.codestats.calc.benchmark;

import com.nickperov.labs.codestats.calc.*;
import com.nickperov.labs.codestats.calc.model.CodeStats;
import org.openjdk.jmh.annotations.*;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Fork(value = 1, warmups = 1)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
//@Threads(value = -1)
public class CodeStatsForkJoinCalcBenchmark {
    
    private String path = "../../../";

    @Param({"1000", "10000", "100000"})
    private Integer threshold;

    @Benchmark
    public CodeStats calcCodeStatsImmutable() {
        return new ImmutableForkJoinCodeCalculator(threshold).calcDirectory(new File(path));
    }

    @Benchmark
    public CodeStats calcCodeStatsMutable() {
        return new MutableForkJoinCodeCalculator(threshold).calcDirectory(new File(path));
    }

    @Benchmark
    public CodeStats calcCodeStatsImmutableFormula() {
        return new ImmutableForkJoinCodeCalculator().calcDirectory(new File(path));
    }

    @Benchmark
    public CodeStats calcCodeStatsMutableFormula() {
        return new MutableForkJoinCodeCalculator().calcDirectory(new File(path));
    }
}