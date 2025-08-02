# Code statistics project

## Description
This project is an experiment. The purpose of this experiment is to explore kotlin coroutines and compare to traditional 
java multithreading approaches.

#### Project consists of modules:
* __code-stats-calc-base__ - code statistics calculator library base classes;
* __code-stats-calc-common__ - code statistics calculator library common classes;
* __code-stats-calc-st__ - code statistics calculator library single thread implementation;
* __code-stats-calc-mt__ - code statistics calculator library multi thread implementation;
* __code-stats-calc-coroutines__ - code statistics calculator library coroutines implementation;
* __code-stats-benchmark__ - JMH benchmark;
* __code-stat-cli__ - java CLI tool.

Code calculator library takes directory as input parameter and return code stats consist of: total number of source files, 
number of code lines, number of comment lines.

JMH Benchmark compares different single-thread and multi-thread approaches.  

## Build instructions

### App build
Before build clean all generated files - mvn clean.

Build everything - mvn package.

Build CLI app (./code-stats-cli/target/app/) - mvn deploy.

### Benchmark
To run all JMH benchmark - java -Xmx16g -Xms16g -jar ./code-stats-benchmark/target/code-stats-benchmark.jar
 
## Authors
Nikolay Perov

## License
This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Benchmark Analysis (August 2025)

The latest benchmarks (see BenchmarkResults05.txt) compare several code statistics calculation implementations on a Mac OS Ventura (Apple M1 Pro, 32GB RAM). The tested approaches include:
- Coroutines (Immutable/Mutable)
- Single-threaded recursive (Immutable/Mutable)
- Java Streams (Sequential/Parallel, Immutable/Mutable)
- ForkJoin (Immutable/Mutable, with/without thread limits)

### Key Observations
- **Coroutines (Immutable/Mutable)** are consistently the fastest for small and medium codebases, with average times around 14 ms (small) and 1,013–1,645 ms (medium - large).
- **Single-threaded recursive** and **Stream Sequential** implementations are significantly slower, especially as the codebase size increases up to 5,300 ms for large codebase.
- **ForkJoin** and **Stream Parallel** approaches do not outperform coroutines and, in some cases, are even slower than single-threaded for large codebases.
- Increasing thread count in ForkJoin (e.g., ForkJoinMutableTh_100000) does not yield better performance and may introduce overhead.

### Summary Table (ms/op, average)
| Approach                | small | medium | large |
|-------------------------|-------|--------|-------|
| Coroutines (Immutable)  | 13.9  | 1,013  | 1,643 |
| Coroutines (Mutable)    | 14.0  | 1,013  | 1,645 |
| ST Recursive (Immutable)| 39.3  | 3,148  | 4,845 |
| ST Recursive (Mutable)  | 42.8  | 3,465  | 5,302 |
| Stream Parallel (Mut.)  | 60.4  | 3,559  | 5,445 |
| Stream Parallel (Imm.)  | 61.8  | 3,221  | 5,468 |
| ForkJoin (Mutable)      | 64.8  | 4,124  | -     |
| ForkJoin (Immutable)    | 64.7  | 4,127  | -     |
| Stream Seq (Mutable)    | 65.4  | 4,605  | -     |
| Stream Seq (Immutable)  | 65.7  | 4,252  | -     |

### Benchmark Dataset Sizes
- **small**: ~250 files, ~100 lines per file
- **medium**: ~640 files, ~120 lines per file
- **large**: ~12,400 files, ~125 lines per file

### Conclusion
Kotlin coroutines provide the best performance for this workload, especially on Apple Silicon. Traditional Java concurrency and stream approaches are less efficient, particularly for large codebases.
