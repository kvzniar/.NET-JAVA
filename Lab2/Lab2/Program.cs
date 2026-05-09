using Lab2.Api;
using Lab2.Data;
using Lab2.Models;
using Microsoft.EntityFrameworkCore;

namespace Lab2
{
    internal class Program
    {
        static async Task Main(string[] args)
        {
            Console.WriteLine("=== Lab 2 - API + SQLite ===");
            Console.WriteLine("Dane z API: https://jsonplaceholder.typicode.com");
            Console.WriteLine();

            using var db = new AppDbContext();

            // pobieramy z API tylko jak baza jest pusta
            if (!db.Users.Any())
            {
                Console.WriteLine("Baza pusta - pobieram dane z API...");
                await DownloadFromApi(db);
            }
            else
            {
                Console.WriteLine($"Wczytano dane z bazy: {db.Users.Count()} userow, {db.Posts.Count()} postow.");
            }

            // proste menu
            bool running = true;
            while (running)
            {
                ShowMenu();
                string? input = Console.ReadLine();
                Console.WriteLine();

                switch (input)
                {
                    case "1":
                        ShowAllUsers(db);
                        break;
                    case "2":
                        ShowPostsForUser(db);
                        break;
                    case "3":
                        AddUser(db);
                        break;
                    case "4":
                        FilterUsers(db);
                        break;
                    case "5":
                        SortUsers(db);
                        break;
                    case "6":
                        RemoveUser(db);
                        break;
                    case "7":
                        await ResetDatabase(db);
                        break;
                    case "0":
                        running = false;
                        break;
                    default:
                        Console.WriteLine("Nieznana opcja.");
                        break;
                }
                Console.WriteLine();
            }

            Console.WriteLine("Koniec.");
        }

        static void ShowMenu()
        {
            Console.WriteLine("--- MENU ---");
            Console.WriteLine("1. Pokaz wszystkich userow");
            Console.WriteLine("2. Pokaz posty dla usera");
            Console.WriteLine("3. Dodaj usera recznie");
            Console.WriteLine("4. Filtruj userow (po nazwie)");
            Console.WriteLine("5. Sortuj userow po nazwie");
            Console.WriteLine("6. Usun usera (po Id)");
            Console.WriteLine("7. Resetuj baze (pobierz ponownie z API)");
            Console.WriteLine("0. Wyjscie");
            Console.Write("Wybor: ");
        }

        static async Task DownloadFromApi(AppDbContext db)
        {
            var api = new ApiClient();
            var users = await api.GetUsersAsync();
            var posts = await api.GetAllPostsAsync();

            // przypisanie postow do userow
            foreach (var user in users)
            {
                user.Posts = posts.Where(p => p.UserId == user.Id).ToList();
            }

            db.Users.AddRange(users);
            db.SaveChanges();

            Console.WriteLine($"Pobrano: {users.Count} userow, {posts.Count} postow.");
        }

        static void ShowAllUsers(AppDbContext db)
        {
            var users = db.Users.ToList();
            Console.WriteLine($"Znaleziono {users.Count} userow:");
            foreach (var u in users)
            {
                Console.WriteLine(u);
            }
        }

        static void ShowPostsForUser(AppDbContext db)
        {
            Console.Write("Podaj Id usera: ");
            string? line = Console.ReadLine();
            if (!int.TryParse(line, out int id))
            {
                Console.WriteLine("Niepoprawne Id.");
                return;
            }

            // Include zaciaga rowniez powiazane posty (relacja)
            var user = db.Users.Include(u => u.Posts).FirstOrDefault(u => u.Id == id);
            if (user == null)
            {
                Console.WriteLine("Nie ma takiego usera.");
                return;
            }

            Console.WriteLine($"Posty usera {user.Name} ({user.Posts.Count}):");
            foreach (var p in user.Posts)
            {
                Console.WriteLine($"  - {p.Title}");
            }
        }

        static void AddUser(AppDbContext db)
        {
            Console.Write("Imie/Nazwa: ");
            string name = Console.ReadLine() ?? "";
            Console.Write("Username: ");
            string username = Console.ReadLine() ?? "";
            Console.Write("Email: ");
            string email = Console.ReadLine() ?? "";

            if (string.IsNullOrWhiteSpace(name) || string.IsNullOrWhiteSpace(email))
            {
                Console.WriteLine("Nazwa i email sa wymagane.");
                return;
            }

            var user = new User
            {
                Name = name,
                Username = username,
                Email = email
            };

            db.Users.Add(user);
            db.SaveChanges();
            Console.WriteLine($"Dodano usera z Id={user.Id}.");
        }

        static void FilterUsers(AppDbContext db)
        {
            Console.Write("Podaj fragment nazwy: ");
            string filter = Console.ReadLine() ?? "";

            // LINQ - filtrowanie
            var result = db.Users
                .Where(u => u.Name.Contains(filter))
                .ToList();

            Console.WriteLine($"Znaleziono {result.Count}:");
            foreach (var u in result)
            {
                Console.WriteLine(u);
            }
        }

        static void SortUsers(AppDbContext db)
        {
            // LINQ - sortowanie
            var result = db.Users.OrderBy(u => u.Name).ToList();
            Console.WriteLine("Po sortowaniu:");
            foreach (var u in result)
            {
                Console.WriteLine(u);
            }
        }

        static void RemoveUser(AppDbContext db)
        {
            Console.Write("Podaj Id usera do usuniecia: ");
            string? line = Console.ReadLine();
            if (!int.TryParse(line, out int id))
            {
                Console.WriteLine("Niepoprawne Id.");
                return;
            }

            var user = db.Users.FirstOrDefault(u => u.Id == id);
            if (user == null)
            {
                Console.WriteLine("Nie ma takiego usera.");
                return;
            }

            db.Users.Remove(user);
            db.SaveChanges();
            Console.WriteLine("Usunieto.");
        }

        static async Task ResetDatabase(AppDbContext db)
        {
            db.Posts.RemoveRange(db.Posts);
            db.Users.RemoveRange(db.Users);
            db.SaveChanges();
            Console.WriteLine("Baza wyczyszczona. Pobieram ponownie z API...");
            await DownloadFromApi(db);
        }
    }
}
