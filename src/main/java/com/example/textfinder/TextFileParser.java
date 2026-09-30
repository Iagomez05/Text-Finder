package com.example.textfinder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class TextFileParser {
    private final String textContent;
    private final int wordCounter;

    public TextFileParser(File textFile) throws IOException {
        textContent = Files.readString(textFile.toPath(), StandardCharsets.UTF_8);
        wordCounter = countWords(textContent);
    }

    static int countWords(String text) {
        String normalized = text == null ? "" : text.trim();
        return normalized.isEmpty() ? 0 : normalized.split("\\s+").length;
    }

    public String getTextContent() {
        return textContent;
    }

    public int getWordCounter() {
        return wordCounter;
    }
}
