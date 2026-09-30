package com.example.textfinder;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedListOccurrences implements Iterable<LinkedListOccurrences.Occurrence> {
    public record Occurrence(int wordNumber, String document) {
    }

    private static class OccurrenceNode {
        private final Occurrence value;
        private OccurrenceNode next;

        private OccurrenceNode(Occurrence value) {
            this.value = value;
        }
    }

    private OccurrenceNode head;
    private OccurrenceNode tail;
    private int size;

    public void add(int wordNumber, String document) {
        OccurrenceNode newNode = new OccurrenceNode(new Occurrence(wordNumber, document));
        if (head == null) {
            head = newNode;
        } else {
            tail.next = newNode;
        }
        tail = newNode;
        size++;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public LinkedListLibrary<String> uniqueDocuments() {
        LinkedListLibrary<String> documents = new LinkedListLibrary<>();
        for (Occurrence occurrence : this) {
            if (!documents.contains(occurrence.document())) {
                documents.add(occurrence.document());
            }
        }
        return documents;
    }

    @Override
    public Iterator<Occurrence> iterator() {
        return new Iterator<>() {
            private OccurrenceNode current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public Occurrence next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                Occurrence value = current.value;
                current = current.next;
                return value;
            }
        };
    }
}
