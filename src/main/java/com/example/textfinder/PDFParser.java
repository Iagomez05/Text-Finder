package com.example.textfinder;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class PDFParser {
    private final String parsedText;
    private final int wordCounter;

    public PDFParser(File pdfFile) throws IOException {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            parsedText = new PDFTextStripper().getText(document);
        }
        wordCounter = TextFileParser.countWords(parsedText);
    }

    public String getParsedText() {
        return parsedText;
    }

    public int getWordCounter() {
        return wordCounter;
    }
}
