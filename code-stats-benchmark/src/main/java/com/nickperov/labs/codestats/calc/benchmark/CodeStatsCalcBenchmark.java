package com.nickperov.labs.codestats.calc.benchmark;

import com.nickperov.labs.codestats.calc.base.model.CodeStatsCalculator;
import com.nickperov.labs.codestats.calc.base.model.SourceCodeStats;
import com.nickperov.labs.codestats.calc.coroutines.CoroutinesImmutableCodeStatsCalculator;
import com.nickperov.labs.codestats.calc.coroutines.CoroutinesMutableCodeStatsCalculator;
import com.nickperov.labs.codestats.calc.mt.ImmutableForkJoinCodeCalculator;
import com.nickperov.labs.codestats.calc.mt.MutableForkJoinCodeCalculator;
import com.nickperov.labs.codestats.calc.mt.StreamParallelImmutableCodeStatsCalculator;
import com.nickperov.labs.codestats.calc.mt.StreamParallelMutableCodeStatsCalculator;
import com.nickperov.labs.codestats.calc.st.RecursiveImmutableCodeStatsCalculator;
import com.nickperov.labs.codestats.calc.st.RecursiveMutableCodeStatsCalculator;
import com.nickperov.labs.codestats.calc.st.StreamSeqImmutableCodeStatsCalculator;
import com.nickperov.labs.codestats.calc.st.StreamSeqMutableCodeStatsCalculator;
import org.openjdk.jmh.annotations.*;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Fork(value = 2, warmups = 1)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class CodeStatsCalcBenchmark {

    public enum CalculatorType {
        STRecursiveMutable, STRecursiveImmutable, StreamSeqMutable, StreamSeqImmutable, StreamParallelImmutable,
        StreamParallelMutable, ForkJoinImmutable, ForkJoinMutable, CoroutinesImmutable, CoroutinesMutable,
        ForkJoinImmutableTh_1000, ForkJoinMutableTh_100000
    }

    @Param({"STRecursiveMutable", "STRecursiveImmutable", "StreamSeqMutable", "StreamSeqImmutable",
            "StreamParallelImmutable", "StreamParallelMutable", "ForkJoinImmutable", "ForkJoinMutable", 
            "CoroutinesImmutable", "CoroutinesMutable", "ForkJoinImmutableTh_1000", "ForkJoinMutableTh_100000",})
    private CalculatorType calcType;

    @Param({"../", "../../", "../../../"})
    private String path;

    private CodeStatsCalculator codeStatsCalculator;

    @Setup(Level.Trial)
    public void initCalculator() {
        switch (this.calcType) {
            case STRecursiveMutable -> this.codeStatsCalculator = new RecursiveMutableCodeStatsCalculator();
            case STRecursiveImmutable -> this.codeStatsCalculator = new RecursiveImmutableCodeStatsCalculator();
            case StreamSeqMutable -> this.codeStatsCalculator = new StreamSeqMutableCodeStatsCalculator();
            case StreamSeqImmutable -> this.codeStatsCalculator = new StreamSeqImmutableCodeStatsCalculator();
            case StreamParallelMutable -> this.codeStatsCalculator = new StreamParallelMutableCodeStatsCalculator();
            case StreamParallelImmutable -> this.codeStatsCalculator = new StreamParallelImmutableCodeStatsCalculator();
            case ForkJoinMutable -> this.codeStatsCalculator = new MutableForkJoinCodeCalculator();
            case ForkJoinImmutable -> this.codeStatsCalculator = new ImmutableForkJoinCodeCalculator();
            case CoroutinesMutable -> this.codeStatsCalculator = new CoroutinesMutableCodeStatsCalculator();
            case CoroutinesImmutable -> this.codeStatsCalculator = new CoroutinesImmutableCodeStatsCalculator();
            case ForkJoinImmutableTh_1000 -> this.codeStatsCalculator = new ImmutableForkJoinCodeCalculator(1000);
            case ForkJoinMutableTh_100000 -> this.codeStatsCalculator = new MutableForkJoinCodeCalculator(100000);
        }
    }

    @Benchmark
    public SourceCodeStats calcCodeStats() {
        return this.codeStatsCalculator.calcDirectory(new File(this.path));
    }
}