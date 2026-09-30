package com.example.textfinder;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class DOCXParser {
    private final String parsedText;
    private final int wordCounter;

    public DOCXParser(File docxFile) throws IOException {
        try (FileInputStream input = new FileInputStream(docxFile);
             XWPFDocument document = new XWPFDocument(input);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            parsedText = extractor.getText();
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
