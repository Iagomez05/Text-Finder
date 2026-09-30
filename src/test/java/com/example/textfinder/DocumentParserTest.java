package com.example.textfinder;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DocumentParserTest {
    @TempDir
    Path tempDirectory;

    @Test
    void extractsTextFromEverySupportedFormat() throws Exception {
        Path textFile = tempDirectory.resolve("sample.txt");
        Files.writeString(textFile, "searchable text document");

        File pdfFile = tempDirectory.resolve("sample.pdf").toFile();
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 12);
                content.newLineAtOffset(50, 700);
                content.showText("searchable pdf document");
                content.endText();
            }
            document.save(pdfFile);
        }

        File docxFile = tempDirectory.resolve("sample.docx").toFile();
        try (XWPFDocument document = new XWPFDocument();
             FileOutputStream output = new FileOutputStream(docxFile)) {
            document.createParagraph().createRun().setText("searchable word document");
            document.write(output);
        }

        assertTrue(DocumentParser.parse(textFile.toFile()).contains("searchable text"));
        assertTrue(DocumentParser.parse(pdfFile).contains("searchable pdf"));
        assertTrue(DocumentParser.parse(docxFile).contains("searchable word"));
    }
}
