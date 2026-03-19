namespace Lab1_Kuzniar
{
    internal class Item
    {
        public int Id { get; set; }
        public int Value { get; set; }
        public int Weight { get; set; }

        public Item(int id, int value, int weight)
        {
            Id = id;
            Value = value;
            Weight = weight;
        }

        public double Ratio()
        {
            return (double)Value / Weight;
        }

        public override string ToString()
        {
            return $"Item {Id}: value = {Value}, weight = {Weight}";
        }
    }
}