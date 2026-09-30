package com.example.textfinder;

import java.util.Locale;

class AVLNode {
    String key;
    int height = 1;
    AVLNode left;
    AVLNode right;
    LinkedListOccurrences occurrences = new LinkedListOccurrences();

    AVLNode(String key, int wordNumber, String document) {
        this.key = key;
        occurrences.add(wordNumber, document);
    }
}

public class AVLTree {
    private AVLNode root;

    public void clear() {
        root = null;
    }

    public void insert(String key, int wordNumber, String document) {
        String normalizedKey = normalize(key);
        if (!normalizedKey.isEmpty()) {
            root = insert(root, normalizedKey, wordNumber, document);
        }
    }

    public boolean contains(String key) {
        return find(normalize(key)) != null;
    }

    public LinkedListOccurrences search(String key) {
        AVLNode node = find(normalize(key));
        return node == null ? new LinkedListOccurrences() : node.occurrences;
    }

    public int height() {
        return height(root);
    }

    public boolean isBalanced() {
        return isBalanced(root);
    }

    private AVLNode insert(AVLNode node, String key, int wordNumber, String document) {
        if (node == null) {
            return new AVLNode(key, wordNumber, document);
        }

        int comparison = key.compareTo(node.key);
        if (comparison < 0) {
            node.left = insert(node.left, key, wordNumber, document);
        } else if (comparison > 0) {
            node.right = insert(node.right, key, wordNumber, document);
        } else {
            node.occurrences.add(wordNumber, document);
            return node;
        }

        updateHeight(node);
        int balance = balance(node);

        if (balance > 1 && key.compareTo(node.left.key) < 0) {
            return rotateRight(node);
        }
        if (balance < -1 && key.compareTo(node.right.key) > 0) {
            return rotateLeft(node);
        }
        if (balance > 1) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        if (balance < -1) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    private AVLNode find(String key) {
        AVLNode current = root;
        while (current != null) {
            int comparison = key.compareTo(current.key);
            if (comparison == 0) {
                return current;
            }
            current = comparison < 0 ? current.left : current.right;
        }
        return null;
    }

    private AVLNode rotateRight(AVLNode node) {
        AVLNode newRoot = node.left;
        AVLNode transferredSubtree = newRoot.right;
        newRoot.right = node;
        node.left = transferredSubtree;
        updateHeight(node);
        updateHeight(newRoot);
        return newRoot;
    }

    private AVLNode rotateLeft(AVLNode node) {
        AVLNode newRoot = node.right;
        AVLNode transferredSubtree = newRoot.left;
        newRoot.left = node;
        node.right = transferredSubtree;
        updateHeight(node);
        updateHeight(newRoot);
        return newRoot;
    }

    private void updateHeight(AVLNode node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }

    private int balance(AVLNode node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private int height(AVLNode node) {
        return node == null ? 0 : node.height;
    }

    private boolean isBalanced(AVLNode node) {
        return node == null
                || Math.abs(balance(node)) <= 1 && isBalanced(node.left) && isBalanced(node.right);
    }

    private String normalize(String key) {
        return key == null ? "" : key.toLowerCase(Locale.ROOT).trim();
    }
}
