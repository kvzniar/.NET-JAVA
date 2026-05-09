using Lab2.Models;
using Microsoft.EntityFrameworkCore;

namespace Lab2.Data
{
    internal class AppDbContext : DbContext
    {
        public DbSet<User> Users { get; set; }
        public DbSet<Post> Posts { get; set; }

        public AppDbContext()
        {
            Database.EnsureCreated();
        }

        protected override void OnConfiguring(DbContextOptionsBuilder options)
        {
            // baza w pliku w folderze z programem
            options.UseSqlite(@"Data Source=Lab2.db");
        }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            // konfiguracja relacji 1:N pomiedzy User a Post
            modelBuilder.Entity<User>()
                .HasMany(u => u.Posts)
                .WithOne(p => p.User)
                .HasForeignKey(p => p.UserId)
                .OnDelete(DeleteBehavior.Cascade);
        }
    }
}
