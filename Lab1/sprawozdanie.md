# Lab1 – Problem plecakowy (wersja zachłanna)

## O co chodzi w tym programie?

Program rozwiązuje uproszczony problem plecakowy.  
Tworzy losowe przedmioty (każdy ma wagę i wartość), a potem próbuje wybrać takie, które zmieszczą się do plecaka o zadanej pojemności.

Wybór odbywa się w prosty sposób — program bierze najpierw te przedmioty, które mają najlepszy stosunek wartości do wagi (`value / weight`).

---

## Jak działa program krok po kroku?

1. Użytkownik podaje:
   - liczbę przedmiotów
   - seed (do losowania)
   - pojemność plecaka

2. Program generuje listę przedmiotów (losowe wartości i wagi)

3. Każdy przedmiot dostaje współczynnik opłacalności:
- Value/weight

4. Przedmioty są sortowane malejąco według tego współczynnika

5. Program przechodzi po tej liście i:
- dodaje przedmiot, jeśli jeszcze się mieści
- pomija go, jeśli przekroczyłby pojemność

6. Na końcu wypisywany jest wynik

---

## Opis plików

### `Item.cs`
Reprezentuje pojedynczy przedmiot:
- id
- wartość
- waga

Ma też metodę do liczenia `value / weight`.

---

### `Problem.cs`
Najważniejsza klasa:
- generuje przedmioty
- zawiera metodę `Solve()`, która rozwiązuje problem

To tutaj dzieje się całe "myślenie" programu (sortowanie i wybór elementów).

---

### `Result.cs`
Przechowuje wynik:
- jakie przedmioty zostały wybrane
- suma wartości
- suma wag

---

### `Program.cs`
Główna klasa:
- pobiera dane od użytkownika
- uruchamia program
- wyświetla wynik

---

## Wnioski

Algorytm jest prosty i szybki, ale nie zawsze daje najlepsze możliwe rozwiązanie (bo jest zachłanny).

Dobrze pokazuje:
- pracę z klasami
- listami
- sortowaniem
- podstawową logikę algorytmów