using System.Text.Json.Serialization;

namespace Lab2.Models
{
    // klasa do mapowania obiektu z API i zapisu w bazie
    internal class User
    {
        [JsonPropertyName("id")]
        public int Id { get; set; }

        [JsonPropertyName("name")]
        public required string Name { get; set; }

        [JsonPropertyName("username")]
        public required string Username { get; set; }

        [JsonPropertyName("email")]
        public required string Email { get; set; }

        [JsonPropertyName("phone")]
        public string? Phone { get; set; }

        // relacja 1:N - jeden user ma wiele postow
        public List<Post> Posts { get; set; } = new List<Post>();

        public override string ToString()
        {
            return $"Id: {Id,-3} | {Name,-25} | {Username,-15} | {Email}";
        }
    }
}
