package com.example.textfinder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AVLTreeTest {
    @Test
    void balancesInsertionsAndStoresEveryOccurrence() {
        AVLTree tree = new AVLTree();
        tree.insert("gamma", 1, "first.txt");
        tree.insert("beta", 2, "first.txt");
        tree.insert("alpha", 3, "first.txt");
        tree.insert("beta", 8, "second.txt");

        assertTrue(tree.isBalanced());
        assertTrue(tree.contains("ALPHA"));
        assertEquals(2, tree.search("beta").size());
        assertEquals(2, tree.search("beta").uniqueDocuments().size());
        assertFalse(tree.contains("missing"));
    }
}
