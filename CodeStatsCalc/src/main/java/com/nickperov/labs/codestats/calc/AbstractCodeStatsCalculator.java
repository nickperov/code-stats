package com.nickperov.labs.codestats.calc;

import com.nickperov.labs.codestats.calc.model.CodeStats;
import com.nickperov.labs.codestats.calc.model.CodeStatsCalculator;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.function.Supplier;

public abstract class AbstractCodeStatsCalculator<T extends CodeStats> implements CodeStatsCalculator {

    private static final String javaFile = ".java";
    private static final String kotlinFile = ".kt";

    @NotNull
    @Override
    public CodeStats calcDirectory(File directory) {
        if (!directory.isDirectory())
            throw new RuntimeException("Not a directory");

        return calcDirectory(directory, this::initCodeStats);
    }

    abstract T initCodeStats();

    abstract CodeStats calcDirectory(File file, Supplier<T> codeStatsSupplier);

    static boolean checkFileName(String fileName) {
        return (fileName.endsWith(javaFile) || fileName.endsWith(kotlinFile));
    }

    CodeStats calcSourceFile(File file) {
        if (!file.isFile())
            return buildCodeStats(0, 0L);
        
        // TODO count comments
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            long numOfLines = fileReader.lines().filter(line -> !line.isBlank()).count();
            return buildCodeStats(1, numOfLines);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return buildCodeStats(0, 0L);
    }

    CodeStats buildCodeStats(int numOfFiles, long numOfLines) {
        return new CodeStats() {
            @Override
            public int numberOfFiles() {
                return numOfFiles;
            }

            @Override
            public long numberOfLines() {
                return numOfLines;
            }
        };
    }
}