using System.Text.Json;
using Lab2.Models;

namespace Lab2.Api
{
    internal class ApiClient
    {
        private readonly HttpClient client;
        private const string BaseUrl = "https://jsonplaceholder.typicode.com";

        public ApiClient()
        {
            client = new HttpClient();
        }

        // pobieranie listy userow z API
        public async Task<List<User>> GetUsersAsync()
        {
            string url = $"{BaseUrl}/users";
            string response = await client.GetStringAsync(url);

            var options = new JsonSerializerOptions
            {
                PropertyNameCaseInsensitive = true
            };

            var users = JsonSerializer.Deserialize<List<User>>(response, options);
            return users ?? new List<User>();
        }

        // pobieranie postow dla konkretnego usera (parametr w URL)
        public async Task<List<Post>> GetPostsByUserAsync(int userId)
        {
            string url = $"{BaseUrl}/posts?userId={userId}";
            string response = await client.GetStringAsync(url);

            var options = new JsonSerializerOptions
            {
                PropertyNameCaseInsensitive = true
            };

            var posts = JsonSerializer.Deserialize<List<Post>>(response, options);
            return posts ?? new List<Post>();
        }

        // pobranie wszystkich postow naraz
        public async Task<List<Post>> GetAllPostsAsync()
        {
            string url = $"{BaseUrl}/posts";
            string response = await client.GetStringAsync(url);

            var options = new JsonSerializerOptions
            {
                PropertyNameCaseInsensitive = true
            };

            var posts = JsonSerializer.Deserialize<List<Post>>(response, options);
            return posts ?? new List<Post>();
        }
    }
}
