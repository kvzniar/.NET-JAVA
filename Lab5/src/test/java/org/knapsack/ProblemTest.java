package org.knapsack;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProblemTest {

    @Test
    public void liczbaPrzedmiotowTest() {
        Problem pr1 = new Problem(5, 1, 1, 10);
        assertEquals(5, pr1.instance.size());
    }

    @Test
    public void przynajmniejJedenPrzedmiotMiesciSieWPlecaku() {
        Problem pr1 = new Problem(10, 1, 1, 10);
        Result res = pr1.Solve(10);
        assertFalse(res.isEmpty());
    }

    @Test
    public void zadenPrzedmiotNieMiesciSieWPlecaku() {
        Problem pr1 = new Problem(5, 1, 5, 10);
        Result res = pr1.Solve(0);
        assertTrue(res.isEmpty());
    }

    @Test
    public void wagaIWartoscWZakresie() {
        Problem pr1 = new Problem(20, 1, 1, 10);
        for (Przedmiot p : pr1.instance) {
            assertTrue(p.getWaga() >= 1 && p.getWaga() <= 10);
            assertTrue(p.getWartosc() >= 1 && p.getWartosc() <= 10);
        }
    }

    @Test
    public void poprawnoscWynikuDlaInstancji() {
        Problem pr1 = new Problem(10, 1, 1, 10);
        Result res = pr1.Solve(15);

        assertTrue(res.getSumaWagi() <= 15);
        assertNotNull(res.toString());
    }
}
