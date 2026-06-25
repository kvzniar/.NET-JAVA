package org.knapsack;

public class Przedmiot {

    private int waga;
    private int wartosc;

    public Przedmiot(int waga, int wartosc) {
        this.waga = waga;
        this.wartosc = wartosc;
    }

    public int getWaga() {
        return waga;
    }

    public int getWartosc() {
        return wartosc;
    }

    public double stosunek() {
        return (double) wartosc / waga;
    }

    @Override
    public String toString() {
        return "v: " + wartosc + " w: " + waga;
    }
}
