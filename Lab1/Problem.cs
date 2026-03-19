using System.Text;

namespace Lab1_Kuzniar
{
    internal class Problem
    {
        public int NumberOfItems { get; set; }
        public List<Item> Items { get; set; }

        public Problem(int n, int seed)
        {
            NumberOfItems = n;
            Items = new List<Item>();

            Random random = new Random(seed);

            for (int i = 0; i < n; i++)
            {
                int value = random.Next(1, 11);
                int weight = random.Next(1, 11);

                Items.Add(new Item(i + 1, value, weight));
            }
        }

        public Result Solve(int capacity)
        {
            Result result = new Result();

            List<Item> sortedItems = Items
            .OrderByDescending(x => x.Ratio())
            .ToList();

            foreach (Item item in sortedItems)
            {
                if (result.TotalWeight + item.Weight <= capacity)
                {
                    result.ChosenItems.Add(item.Id);
                    result.TotalWeight += item.Weight;
                    result.TotalValue += item.Value;
                }
            }

            return result;
        }

        public override string ToString()
        {
            StringBuilder sb = new StringBuilder();

            sb.AppendLine($"Problem with {NumberOfItems} items:");
            foreach (Item item in Items)
            {
                sb.AppendLine(item.ToString());
            }

            return sb.ToString();
        }
    }
}