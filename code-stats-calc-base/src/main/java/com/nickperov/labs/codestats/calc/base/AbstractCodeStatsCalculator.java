package com.nickperov.labs.codestats.calc.base;

import com.nickperov.labs.codestats.calc.base.model.CodeStatsCalculator;
import com.nickperov.labs.codestats.calc.base.model.LineCodeStats;
import com.nickperov.labs.codestats.calc.base.model.SourceCodeStats;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.function.Supplier;

public abstract class AbstractCodeStatsCalculator<T extends SourceCodeStats> implements CodeStatsCalculator {

    private static final String javaFile = ".java";
    private static final String kotlinFile = ".kt";

    @NotNull
    @Override
    public SourceCodeStats calcDirectory(final File directory) {
        if (!directory.isDirectory())
            throw new RuntimeException("Not a directory");

        return calcDirectory(directory, this::initCodeStats);
    }

    protected abstract T initCodeStats();

    protected abstract SourceCodeStats calcDirectory(final File file, final Supplier<T> codeStatsSupplier);

    protected static boolean checkFileName(final String fileName) {
        return (fileName.endsWith(javaFile) || fileName.endsWith(kotlinFile));
    }

    protected SourceCodeStats calcSourceFile(final File file) {
        if (!file.isFile())
            return buildCodeStats(0, 0L, 0L);

        try (final BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            var isCommentBlock = false;
            var numberOfCommentLines = 0L;
            var numOfCodeLines = 0L;
            final Iterator<String> lineIterator = fileReader.lines().filter(line -> !line.isBlank()).iterator();
            while (lineIterator.hasNext()) {
                final var line = lineIterator.next();
                final var lineStats = calcLine(isCommentBlock, line);
                numOfCodeLines += lineStats.numberOfCodeLines();
                numberOfCommentLines += lineStats.numberOfCommentLines();
                isCommentBlock = lineStats.isOpenCommentBlock();
            }
            return buildCodeStats(1, numOfCodeLines, numberOfCommentLines);
        } catch (final IOException e) {
            throw new RuntimeException("Failed to read file - " + e.getMessage());
        }
    }

    // Return number of code lines, comment lines and is comment open or not 
    private static LineCodeStats calcLine(final boolean isCommentBlock, final String line) {

        var isOpenComment = isCommentBlock;
        var isComment = false;
        var isCode = false;
        var numOfCommentLines = 0;
        var numOfCodeLines = 0;

        final var characters = line.toCharArray();
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
                    // Check whether the comment is not empty
                    for (int k = i + 2; k < characters.length; k++) {
                        if (characters[k] > 32) {
                            numOfCommentLines++;
                            break;
                        }
                    }
                    break;
                } else if ((ch == '/') && (i < (characters.length - 1)) && (characters[i + 1] == '*')) {
                    // '/*' Found => start of new comment block
                    i++;
                    isOpenComment = true;
                    //numOfCommentLines++;
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
        return new LineCodeStats(numOfCodeLines, numOfCommentLines, isOpenComment);
    }

    protected SourceCodeStats buildCodeStats(final int numOfFiles, final long numOfCodeLines, final long numberOfCommentLines) {
        return new SourceCodeStats() {

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