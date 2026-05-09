# Laboratorium 3 - .NET (ocena 4.0)

Aplikacja konsolowa w .NET 8.0 do wielowatkowego mnozenia macierzy z porownaniem wydajnosci.

## Co zostalo zrobione
- **Zadanie 1** - mnozenie macierzy z uzyciem `Parallel.For` + porownanie z wersja sekwencyjna (1 watek)
- **Zadanie 2** - mnozenie macierzy z uzyciem klasy `Thread` (niskopoziomowo) + porownanie

## Jak uruchomic
```
cd Lab3
dotnet run
```
Mozna podac rozmiar macierzy jako parametr:
```
dotnet run -- 500
```

## Co robi program
1. Test poprawnosci - mnoży 5x5 trzema sposobami i porownuje wyniki
2. Benchmark dla roznych rozmiarow (100, 200, parametr) i roznej liczby watkow (1, 2, 4, 8, 16)
3. Wypisuje srednie czasy z 3 prob + wspolczynnik przyspieszenia (speedup)

## Wnioski
Przy odpowiednio duzych macierzach (>=100) widoczne jest przyspieszenie. Dla `Thread` osiaga sie zwykle podobne lub lekko lepsze rezultaty od `Parallel.For` przy odpowiednio dobranej liczbie watkow (rownej fizycznym rdzeniom).

Powyzej liczby fizycznych rdzeni dalsze zwiekszanie liczby watkow nie daje juz tak duzego zysku (a czasem nawet pogarsza wynik przez koszt zarzadzania watkami).
