package com.example.textfinder;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public final class DocumentParser {
    private DocumentParser() {
    }

    public static boolean supports(File file) {
        if (file == null || !file.isFile()) {
            return false;
        }
        String name = file.getName().toLowerCase(Locale.ROOT);
        return name.endsWith(".txt") || name.endsWith(".pdf") || name.endsWith(".docx");
    }

    public static String parse(File file) throws IOException {
        if (!supports(file)) {
            throw new IOException("Unsupported document type: " + (file == null ? "null" : file.getName()));
        }

        String name = file.getName().toLowerCase(Locale.ROOT);
        if (name.endsWith(".txt")) {
            return new TextFileParser(file).getTextContent();
        }
        if (name.endsWith(".pdf")) {
            return new PDFParser(file).getParsedText();
        }
        return new DOCXParser(file).getParsedText();
    }
}
