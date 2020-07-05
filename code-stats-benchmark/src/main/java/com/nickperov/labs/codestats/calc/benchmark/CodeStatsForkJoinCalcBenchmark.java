package com.nickperov.labs.codestats.calc.benchmark;

import com.nickperov.labs.codestats.calc.base.model.SourceCodeStats;
import com.nickperov.labs.codestats.calc.mt.ImmutableForkJoinCodeCalculator;
import com.nickperov.labs.codestats.calc.mt.MutableForkJoinCodeCalculator;
import org.openjdk.jmh.annotations.*;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Fork(value = 2, warmups = 1)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class CodeStatsForkJoinCalcBenchmark {

    private String path = "../../../";

    @Param({"1000", "10000", "100000"})
    private Integer threshold;

    @Benchmark
    public SourceCodeStats calcCodeStatsImmutable() {
        return new ImmutableForkJoinCodeCalculator(this.threshold).calcDirectory(new File(this.path));
    }

    @Benchmark
    public SourceCodeStats calcCodeStatsMutable() {
        return new MutableForkJoinCodeCalculator(this.threshold).calcDirectory(new File(this.path));
    }

    @Benchmark
    public SourceCodeStats calcCodeStatsImmutableFormula() {
        return new ImmutableForkJoinCodeCalculator().calcDirectory(new File(this.path));
    }

    @Benchmark
    public SourceCodeStats calcCodeStatsMutableFormula() {
        return new MutableForkJoinCodeCalculator().calcDirectory(new File(this.path));
    }
}