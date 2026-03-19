namespace Lab1_Kuzniar
{
    internal class Program
    {
        static void Main(string[] args)
        {
            Console.Write("Enter number of items: ");
            int n = int.Parse(Console.ReadLine());

            Console.Write("Enter seed: ");
            int seed = int.Parse(Console.ReadLine());

            Console.Write("Enter knapsack capacity: ");
            int capacity = int.Parse(Console.ReadLine());

            Problem problem = new Problem(n, seed);

            Console.WriteLine();
            Console.WriteLine(problem);

            Result result = problem.Solve(capacity);

            Console.WriteLine(result);
        }
    }
}