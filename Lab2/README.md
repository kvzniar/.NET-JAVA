# Laboratorium 2 - .NET (ocena 4.0)

Aplikacja konsolowa w .NET 8.0 pobierajaca dane z API i zapisujaca je do bazy SQLite z uzyciem Entity Framework Core.

## Co zostalo zrobione
- **Zadanie 1** - pobranie i deserializacja danych z API (https://jsonplaceholder.typicode.com)
- **Zadanie 2** - obsluga bazy danych SQLite + relacja 1:N pomiedzy `User` a `Post`. Dane sa pobierane z API tylko gdy baza jest pusta.

## Jak uruchomic
```
cd Lab2
dotnet run
```

Przy pierwszym uruchomieniu utworzy sie plik `Lab2.db` i zostana pobrane dane z API.

## Struktura
- `Models/User.cs`, `Models/Post.cs` - klasy encji (relacja 1:N)
- `Data/AppDbContext.cs` - kontekst EF Core (SQLite)
- `Api/ApiClient.cs` - klient HTTP do API
- `Program.cs` - menu konsolowe (CRUD, filtrowanie, sortowanie)
