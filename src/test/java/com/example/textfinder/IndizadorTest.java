package com.example.textfinder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IndizadorTest {
    @TempDir
    Path tempDirectory;

    @Test
    void indexesNormalizedWordsAndReturnsMatchingDocuments() throws Exception {
        Path first = tempDirectory.resolve("first.txt");
        Path second = tempDirectory.resolve("second.txt");
        Files.writeString(first, "Algorithms make search efficient. Algorithms matter.");
        Files.writeString(second, "Document parsing also matters.");

        Indizador indexer = new Indizador();
        assertEquals(2, indexer.rebuild(List.of(first.toFile(), second.toFile())));

        LinkedListLibrary<String> matches = indexer.searchDocuments("ALGORITHMS");
        assertEquals(1, matches.size());
        assertEquals(first.toFile().getAbsolutePath(), matches.get(0));
        assertTrue(indexer.tree().isBalanced());
    }
}
