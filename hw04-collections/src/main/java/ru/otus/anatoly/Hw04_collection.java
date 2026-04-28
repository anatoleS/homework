package ru.otus.anatoly;

import java.util.*;
import java.util.stream.Collectors;

public class Hw04_collection {

    public static void example() {

        System.out.println("Demonstration replaceArrayElements method on array [a, b, c, d, e, f], and swap 2 and 5 elements:");
        String[] arr1 = new String[]{"a", "b", "c", "d", "e", "f"};
        System.out.println("  Before: " + Arrays.toString(arr1));
        replaceArrayElements(arr1, 2, 5);
        System.out.println("  After : " + Arrays.toString(arr1));

        System.out.println("Demonstration output convert array to ArrayList method on array [a, b, c, d, e, f], and swap 2 and 5 elements:");
        String[] arr2 = new String[]{"a", "b", "c", "d", "e", "f"};
        System.out.println(arr2.getClass());
        ArrayList<String> list = toArrayList(arr2);
        System.out.println("  " + list.getClass() + " " + list);

        System.out.println("Demonstration calculation words in array:");
        List<String> listOfWords = Arrays.asList("one", "two", "three", "four", "five", "six", "seven", "eight", "nine");
        String[] arrayOfWords = createWordArray(listOfWords, 20);
        System.out.println("  Library of words: " + listOfWords);
        System.out.println("  Result random array: " + Arrays.toString(arrayOfWords));

        HashMap<String, Integer> map = new HashMap<>();
        Arrays.stream(arrayOfWords).forEach(word -> map.put(word, map.getOrDefault(word, 0) + 1));
        map.forEach((s, count) -> System.out.println("     word: " + s + ", count = " + count));

        System.out.println("  whole unique words: " + map.size());
        System.out.println("  unique words list (use distinct): " + Arrays.stream(arrayOfWords).distinct().toList());
        System.out.println("  unique words list (use set): " + new HashSet<>(Arrays.asList(arrayOfWords)));
        System.out.println("  unique words list (use output keys of map): " + map.keySet());
    }

    /**
     * Creates an array of words with random selection from the provided list.
     * Words may repeat in the array.
     *
     * @param wordList list of words to select from
     * @param size     the size of the array to create
     * @return array of words with possible duplicates
     */
    private static String[] createWordArray(List<String> wordList, int size) {
        String[] arrayOfWords = new String[size];
        Random random = new Random();
        for (int i = 0; i < arrayOfWords.length; i++) {
            arrayOfWords[i] = wordList.get(random.nextInt(wordList.size()));
        }
        return arrayOfWords;
    }

    /**
     * Swaps two elements in the array at the specified indices.
     *
     * @param arr    the array whose elements need to be swapped (must not be null)
     * @param index1 the index of the first element to swap
     * @param index2 the index of the second element to swap
     * @throws IllegalArgumentException if the array is null or if either index is out of array bounds
     */
    public static void replaceArrayElements(Object[] arr, int index1, int index2) {
        if (arr == null) {
            throw new IllegalArgumentException("array must not be null");
        }
        if (index1 < 0 || index1 >= arr.length) {
            throw new IllegalArgumentException("first index out of range: " + index1 + ", array length: " + arr.length);
        }
        if (index2 < 0 || index2 >= arr.length) {
            throw new IllegalArgumentException("second index out of range: " + index2 + ", array length: " + arr.length);
        }
        if (index1 != index2) {
            Object temp = arr[index1];
            arr[index1] = arr[index2];
            arr[index2] = temp;
        }
    }

    /**
     * Converts an array of objects to a mutable ArrayList.
     * <p>
     * This method creates a new ArrayList containing all elements from the array.
     * The returned list is independent of the original array - changes to the list
     * will not affect the array.
     * Alternatives:
     * new ArrayList<>(Arrays.asList(java.lang.Object[]))
     * Collections#addAll(java.util.Collection, java.lang.Object[]);
     *
     * @param <T> the type of elements in the array
     * @param arr the array to convert (can be null)
     * @return a new ArrayList containing all array elements, or null if the input array is null
     */
    public static <T> ArrayList<T> toArrayList(T[] arr) {
        if (arr == null) {
            return null;
        }
        ArrayList<T> list = new ArrayList<>(arr.length);
        Collections.addAll(list, arr);
        return list;
    }

}