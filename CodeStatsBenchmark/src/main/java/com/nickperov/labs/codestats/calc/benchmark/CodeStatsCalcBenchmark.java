package com.nickperov.labs.codestats.calc.benchmark;

import com.nickperov.labs.codestats.calc.*;
import com.nickperov.labs.codestats.calc.model.CodeStats;
import com.nickperov.labs.codestats.calc.model.CodeStatsCalculator;
import org.openjdk.jmh.annotations.*;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Fork(value = 1, warmups = 1)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
//@Threads(value = -1)
public class CodeStatsCalcBenchmark {

    public enum CalculatorType {
        STRecursiveMutable, STRecursiveImmutable, StreamSeqMutable, StreamSeqImmutable, StreamParallelImmutable,
        StreamParallelMutable, ForkJoinImmutable, ForkJoinMutable, CoroutinesImmutable, CoroutinesMutable,
        ForkJoinImmutableTh_1000, ForkJoinMutableTh_100000
    }

    @Param({"../", "../../", "../../../"})
    private String path;

    @Param({"STRecursiveMutable", "STRecursiveImmutable", "StreamSeqMutable", "StreamSeqImmutable",
            "StreamParallelImmutable", "StreamParallelMutable", "ForkJoinImmutable", "ForkJoinMutable", "CoroutinesImmutable",
            "CoroutinesMutable", "ForkJoinImmutableTh_1000", "ForkJoinMutableTh_100000",})
    private CalculatorType calcType;

    private CodeStatsCalculator codeStatsCalculator;

    @Setup(Level.Trial)
    public void initCalculator() {
        switch (calcType) {
            case STRecursiveMutable:
                codeStatsCalculator = new RecursiveMutableCodeStatsCalculator();
                break;
            case STRecursiveImmutable:
                codeStatsCalculator = new RecursiveImmutableCodeStatsCalculator();
                break;
            case StreamSeqMutable:
                codeStatsCalculator = new StreamSeqMutableCodeStatsCalculator();
                break;
            case StreamSeqImmutable:
                codeStatsCalculator = new StreamSeqImmutableCodeStatsCalculator();
                break;
            case StreamParallelMutable:
                codeStatsCalculator = new StreamParallelMutableCodeStatsCalculator();
                break;
            case StreamParallelImmutable:
                codeStatsCalculator = new StreamParallelImmutableCodeStatsCalculator();
                break;
            case ForkJoinMutable:
                codeStatsCalculator = new MutableForkJoinCodeCalculator();
                break;
            case ForkJoinImmutable:
                codeStatsCalculator = new ImmutableForkJoinCodeCalculator();
                break;
            case CoroutinesMutable:
                codeStatsCalculator = new CoroutinesMutableCodeStatsCalculator();
                break;
            case CoroutinesImmutable:
                codeStatsCalculator = new CoroutinesImmutableCodeStatsCalculator();
                break;
            case ForkJoinImmutableTh_1000:
                codeStatsCalculator = new ImmutableForkJoinCodeCalculator(1000);
                break;
            case ForkJoinMutableTh_100000:
                codeStatsCalculator = new MutableForkJoinCodeCalculator(100000);
                break;
        }
    }

    @Benchmark
    public CodeStats calcCodeStats() {
        return codeStatsCalculator.calcDirectory(new File(path));
    }
}