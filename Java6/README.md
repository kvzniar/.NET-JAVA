# Edytor Obrazów — Laboratorium 6

Aplikacja okienkowa w JavaFX do podstawowej obróbki zdjęć w formacie JPG.

Wczytujesz zdjęcie, wybierasz operację z listy (negatyw, progowanie, konturowanie, rozmycie), klikasz Wykonaj i widzisz wynik obok oryginału. Możesz też obrócić obraz albo zmienić jego rozmiar. Na końcu zapisujesz wynik do pliku.

Operacje przetwarzania obrazu działają na 4 wątkach jednocześnie — obraz jest dzielony na 4 poziome paski, każdy wątek robi swój kawałek. Wszystkie akcje użytkownika i błędy lądują w pliku `app.log`.


## Autor

Maksymilian Kuźniar 280063 — Politechnika Wrocławska
