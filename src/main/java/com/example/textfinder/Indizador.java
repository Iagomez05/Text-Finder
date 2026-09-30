package com.example.textfinder;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Indizador {
    private static final Pattern TOKEN_PATTERN = Pattern.compile("[\\p{L}\\p{N}]+");

    private final AVLTree avlTree = new AVLTree();
    private boolean indexed;

    public int rebuild(Iterable<File> files) throws IOException {
        indexed = false;
        avlTree.clear();
        int documentCount = 0;
        for (File file : files) {
            indexDocument(file);
            documentCount++;
        }
        indexed = true;
        return documentCount;
    }

    public int indexDocument(File file) throws IOException {
        String text = DocumentParser.parse(file);
        Matcher matcher = TOKEN_PATTERN.matcher(text.toLowerCase(Locale.ROOT));
        int position = 1;
        while (matcher.find()) {
            avlTree.insert(matcher.group(), position++, file.getAbsolutePath());
        }
        return position - 1;
    }

    public LinkedListLibrary<String> searchDocuments(String word) {
        return avlTree.search(word).uniqueDocuments();
    }

    public boolean isIndexed() {
        return indexed;
    }

    public void markDirty() {
        indexed = false;
    }

    AVLTree tree() {
        return avlTree;
    }
}
