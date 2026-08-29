package com.pastry;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ExampleTest {
    @Test
    void greetReturnsGreetingWithName() {
        Example example = new Example();

        assertEquals("Hello, Pastry!", example.greet("Pastry"));
    }

    @Test
    void getSumReturnsCorrectSum() {
        Example example = new Example();
        assertEquals(5, example.getSum(2, 3));
    }
}
