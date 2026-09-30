package com.example.textfinder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SortingAlgorithmsTest {
    @TempDir
    Path tempDirectory;

    @Test
    void sortsFilesByNameDateAndSize() throws Exception {
        File charlie = createFile("charlie.txt", "123456789", 3_000);
        File alpha = createFile("alpha.txt", "1", 1_000);
        File bravo = createFile("bravo.txt", "1234", 2_000);

        LinkedListLibrary<File> byName = list(charlie, alpha, bravo);
        QuickSort.sort(byName);
        assertEquals("alpha.txt", byName.get(0).getName());
        assertEquals("charlie.txt", byName.get(2).getName());

        LinkedListLibrary<File> byDate = list(charlie, alpha, bravo);
        BubbleSort.sort(byDate);
        assertEquals("alpha.txt", byDate.get(0).getName());
        assertEquals("charlie.txt", byDate.get(2).getName());

        LinkedListLibrary<File> bySize = list(charlie, alpha, bravo);
        RadixSort.sort(bySize);
        assertEquals("alpha.txt", bySize.get(0).getName());
        assertEquals("charlie.txt", bySize.get(2).getName());
    }

    private File createFile(String name, String content, long modified) throws Exception {
        Path path = tempDirectory.resolve(name);
        Files.writeString(path, content);
        File file = path.toFile();
        file.setLastModified(modified);
        return file;
    }

    @SafeVarargs
    private LinkedListLibrary<File> list(File... files) {
        LinkedListLibrary<File> result = new LinkedListLibrary<>();
        for (File file : files) {
            result.add(file);
        }
        return result;
    }
}
