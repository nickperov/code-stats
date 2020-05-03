package com.nickperov.labs.codestats.calc;

import com.nickperov.labs.codestats.calc.model.CodeStats;
import com.nickperov.labs.codestats.calc.model.CodeStatsCalculator;
import kotlin.Triple;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.function.Supplier;

public abstract class AbstractCodeStatsCalculator<T extends CodeStats> implements CodeStatsCalculator {

    private static final String javaFile = ".java";
    private static final String kotlinFile = ".kt";
    
    @NotNull
    @Override
    public CodeStats calcDirectory(final File directory) {
        if (!directory.isDirectory())
            throw new RuntimeException("Not a directory");

        return calcDirectory(directory, this::initCodeStats);
    }

    abstract T initCodeStats();

    abstract CodeStats calcDirectory(final File file, final Supplier<T> codeStatsSupplier);

    static boolean checkFileName(final String fileName) {
        return (fileName.endsWith(javaFile) || fileName.endsWith(kotlinFile));
    }

    CodeStats calcSourceFile(final File file) {
        if (!file.isFile())
            return buildCodeStats(0, 0L, 0L);
            
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            var isCommentBlock = false;
            long numberOfCommentLines = 0L;
            long numOfCodeLines = 0L;
            final Iterator<String> lineIterator = fileReader.lines().filter(line -> !line.isBlank()).iterator();
            while (lineIterator.hasNext()) {
                final var line = lineIterator.next();
                final var lineStats = calcLine(isCommentBlock, line);
                // TODO replace triple with special class;
                numOfCodeLines += lineStats.getFirst();
                numberOfCommentLines += lineStats.getSecond();
                isCommentBlock = lineStats.getThird();
            }
            return buildCodeStats(1, numOfCodeLines, numberOfCommentLines);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return buildCodeStats(0, 0L, 0L);
    }
    
    // TODO check empty comments 
    
    // Return number of code lines, comment lines and is comment open or not 
    private static Triple<Integer, Integer, Boolean> calcLine(final boolean isCommentBlock, final String line) {
        
        var isOpenComment = isCommentBlock;
        var isComment = false;
        var isCode = false;
        var numOfCommentLines = isCommentBlock ? 1 : 0;
        var numOfCodeLines = 0;

        var characters = line.toCharArray();
        for (int i = 0; i < characters.length; i++) {
            final var ch = characters[i];
            if (isOpenComment) {
                if ((ch == '*') && (i < (characters.length - 1)) && (characters[i + 1] == '/')) {
                    // '*/' Found => end of comment block
                    i++;
                    isOpenComment = false;
                    isComment = false;
                } else if (ch > 32 && !isComment) {
                    // Non empty symbol inside comment block => +1 to comment line
                    numOfCommentLines++;
                    isComment = true;
                }
            } else if (ch > 32) {
                // Skip empty symbols
                if ((ch == '/') && (i < (characters.length - 1)) && (characters[i + 1] == '/')) {
                    // '//' Found => the rest of the line is single line comment
                    numOfCommentLines++;
                    break;
                } else if ((ch == '/') && (i < (characters.length - 1)) && (characters[i + 1] == '*')) {
                    // '/*' Found => start of new comment block
                    i++;
                    numOfCommentLines++;
                } else if (ch != ';' && !isCode) {
                    // Non empty, not end of the line and non comment symbol => start of new code line
                    isCode = true;
                    numOfCodeLines++;
                } else if (ch == ';' && isCode) {
                    // End of the code line
                    isCode = false;
                }
            }
        }
        return new Triple<>(numOfCodeLines, numOfCommentLines, isOpenComment);
    }
    
    CodeStats buildCodeStats(final int numOfFiles, final long numOfCodeLines, final long numberOfCommentLines) {
        return new CodeStats() {

            @Override
            public int numberOfFiles() {
                return numOfFiles;
            }

            @Override
            public long numberOfCodeLines() {
                return numOfCodeLines;
            }

            @Override
            public long numberOfCommentLines() {
                return numberOfCommentLines;
            }
        };
    }
}