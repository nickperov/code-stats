# Code statistics project

## Description
This project is an experiment. The purpose of this experiment is to explore kotlin coroutines and compare to traditional 
java multithreading approaches.

#### Project consists of modules:
* __code-stats-calc__ - code statistics calculator library;
* __code-stats-benchmark__ - JMH benchmark;
* __code-stat-cli__ - java CLI tool.

Code calculator library takes directory as input parameter and return code stats consist of: total number of source files, 
number of code lines, number of comment lines.

JMH Benchmark compares different single-thread and multi-thread approaches.  

## Build instructions

### App build
Build everything - mvn clean package.

Build CLI app (./code-stats-cli/target/app/) - mvn clean deploy.

### Benchmark
To run all JMH benchmark - java -Xmx16g -Xms16g -jar ./code-stats-benchmark/target/code-stats-benchmark.jar
 
## Authors
Nikolay Perov

## License
This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

