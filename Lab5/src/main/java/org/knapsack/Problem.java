package org.knapsack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Problem {

    private int n;
    private int seed;
    private int lowerBound;
    private int upperBound;

    public List<Przedmiot> instance;

    public Problem(int n, int seed, int lowerBound, int upperBound) {
        this.n = n;
        this.seed = seed;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;

        instance = new ArrayList<>();
        Random rand = new Random(seed);

        for (int i = 0; i < n; i++) {
            int waga = rand.nextInt(upperBound - lowerBound + 1) + lowerBound;
            int wartosc = rand.nextInt(upperBound - lowerBound + 1) + lowerBound;
            instance.add(new Przedmiot(waga, wartosc));
        }
    }

    public Result Solve(int capacity) {
        Result result = new Result();

        List<Przedmiot> sorted = new ArrayList<>(instance);
        sorted.sort(Comparator.comparingDouble(Przedmiot::stosunek).reversed());

        int wolneMiejsce = capacity;

        for (Przedmiot p : sorted) {
            if (p.getWaga() <= 0) {
                continue;
            }
            int ile = wolneMiejsce / p.getWaga();
            if (ile > 0) {
                int numer = instance.indexOf(p);
                result.dodaj(numer, ile, p.getWartosc(), p.getWaga());
                wolneMiejsce -= ile * p.getWaga();
            }
        }

        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < instance.size(); i++) {
            sb.append("No: ").append(i).append(" ").append(instance.get(i)).append("\n");
        }
        return sb.toString();
    }
}
