using System.Text;

namespace Lab1_Kuzniar
{
    internal class Result
    {
        public List<int> ChosenItems { get; set; }
        public int TotalValue { get; set; }
        public int TotalWeight { get; set; }

        public Result()
        {
            ChosenItems = new List<int>();
            TotalValue = 0;
            TotalWeight = 0;
        }

        public override string ToString()
        {
            StringBuilder sb = new StringBuilder();

            sb.AppendLine("Solution:");
            sb.AppendLine("Chosen items: " + (ChosenItems.Count == 0 ? "none" : string.Join(", ", ChosenItems)));
            sb.AppendLine("Total value: " + TotalValue);
            sb.AppendLine("Total weight: " + TotalWeight);

            return sb.ToString();
        }
    }
}