package org.knapsack;

import java.util.ArrayList;
import java.util.List;

public class Result {

    private List<Integer> numeryPrzedmiotow;
    private List<Integer> ilosci;
    private int sumaWartosci;
    private int sumaWagi;

    public Result() {
        numeryPrzedmiotow = new ArrayList<>();
        ilosci = new ArrayList<>();
        sumaWartosci = 0;
        sumaWagi = 0;
    }

    public void dodaj(int numer, int ilosc, int wartosc, int waga) {
        numeryPrzedmiotow.add(numer);
        ilosci.add(ilosc);
        sumaWartosci += wartosc * ilosc;
        sumaWagi += waga * ilosc;
    }

    public List<Integer> getNumeryPrzedmiotow() {
        return numeryPrzedmiotow;
    }

    public List<Integer> getIlosci() {
        return ilosci;
    }

    public int getSumaWartosci() {
        return sumaWartosci;
    }

    public int getSumaWagi() {
        return sumaWagi;
    }

    public boolean isEmpty() {
        return numeryPrzedmiotow.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numeryPrzedmiotow.size(); i++) {
            sb.append("No: ").append(numeryPrzedmiotow.get(i))
                    .append(" x").append(ilosci.get(i)).append("\n");
        }
        sb.append("Weight: ").append(sumaWagi).append("\n");
        sb.append("Value: ").append(sumaWartosci);
        return sb.toString();
    }
}
