package com.griefcraft.cache;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MethodCounter {

    private final Map<String, Integer> counts = new HashMap<>();

    /**
     * Increment a method in the counts
     *
     * @param method The method to increment count of
     */
    public void increment(String method) {
        deltaMethod(method, 1);
    }

    /**
     * Decrement a method in the cache
     *
     * @param method The method to decrement count of
     */
    public void decrement(String method) {
        deltaMethod(method, -1);
    }

    /**
     * Get the counts for a method
     *
     * @param method The method to get the count of
     * @return int representing count of the method or 0 if not available
     */
    public int get(String method) {
        return counts.getOrDefault(method, 0);
    }

    /**
     * Sorts the method counts by the value and returns an unmodifiable map for it
     *
     * @return Unmodifiable {@literal Map<String, Integer>} containing counts of methods sorted by their size
     */
    public Map<String, Integer> sortByValue() {
        return Collections.unmodifiableMap(sortByComparator(counts, false));
    }

    private void deltaMethod(String method, int delta) {
        if (!counts.containsKey(method)) {
            counts.put(method, delta);
            return;
        }

        counts.put(method, counts.get(method) + delta);
    }

    private static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap, final boolean order) {
        List<Map.Entry<String, Integer>> list = new LinkedList<>(unsortMap.entrySet());
        
        // Sorting the list based on values
        list.sort((o1, o2) -> {
            if (order) {
                return o1.getValue().compareTo(o2.getValue());
            } else {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

}
