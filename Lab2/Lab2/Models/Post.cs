using System.Text.Json.Serialization;

namespace Lab2.Models
{
    internal class Post
    {
        [JsonPropertyName("id")]
        public int Id { get; set; }

        [JsonPropertyName("userId")]
        public int UserId { get; set; }

        [JsonPropertyName("title")]
        public required string Title { get; set; }

        [JsonPropertyName("body")]
        public string? Body { get; set; }

        // klucz obcy - relacja do User
        public User? User { get; set; }

        public override string ToString()
        {
            return $"PostId: {Id,-3} | UserId: {UserId,-3} | {Title}";
        }
    }
}
