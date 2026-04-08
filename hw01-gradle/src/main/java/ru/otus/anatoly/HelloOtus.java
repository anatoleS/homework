package ru.otus.anatoly;

import com.google.common.collect.Lists;

import java.util.List;

public class HelloOtus {

    public void example() {
        // Guava - .newArrayList
        List<String> names = Lists.newArrayList("тест1", "тест2", "тест3");
        System.out.println("Список имен: " + names);

    }

}
