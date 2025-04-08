package com.interview.reece.addressbook;

import java.util.Collection;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestUtil {
    public static final String UNIT_TEST = "UnitTest";
    public static final String INTEGRATION_TEST = "IntegrationTest";
    public static String BASE_URL = "http://localhost:";
    public static String BASE_URL_FORMAT = BASE_URL + "%d/%s";

    public static final String SPRING_PROFILE_TEST = "test";

    /**
     * Helper method to assert a value is found based on a collection criteria
     *
     * @param collection the collection to test
     * @param criteria   the criteria
     * @param <T>        the type
     */
    public static <T> void assertContains(final Collection<T> collection, Predicate<T> criteria) {
        assertTrue(collection.stream().anyMatch(criteria::test));
    }

    /**
     * Helper method to assert a value is not found based on a collection criteria
     *
     * @param collection the collection to test
     * @param criteria   the criteria
     * @param <T>        the type
     */
    public static <T> void assertNotContains(final Collection<T> collection, Predicate<T> criteria) {
        assertTrue(collection.stream().noneMatch(criteria::test));
    }

    /**
     * Asserts that collection one's entry in any order matches in collection two in any order
     * @param one the first collection
     * @param two the second collection
     */
    public static void assertMatchesAllUnordered(final Collection one, Collection two) {
        assertTrue(one.size() == two.size());
        assertTrue(one.stream().allMatch(item -> two.contains(item)));
    }
}
