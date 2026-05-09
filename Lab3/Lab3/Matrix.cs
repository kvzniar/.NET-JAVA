namespace Lab3
{
    // klasa reprezentujaca macierz - paradygmat OOP
    internal class Matrix
    {
        public int Rows { get; }
        public int Cols { get; }
        public double[,] Data { get; }

        public Matrix(int rows, int cols)
        {
            Rows = rows;
            Cols = cols;
            Data = new double[rows, cols];
        }

        // wypelnienie losowymi wartosciami
        public void FillRandom(Random rnd)
        {
            for (int i = 0; i < Rows; i++)
            {
                for (int j = 0; j < Cols; j++)
                {
                    Data[i, j] = rnd.NextDouble() * 10.0;
                }
            }
        }

        public void Print()
        {
            for (int i = 0; i < Rows; i++)
            {
                for (int j = 0; j < Cols; j++)
                {
                    Console.Write($"{Data[i, j],8:F2} ");
                }
                Console.WriteLine();
            }
        }

        // sprawdzenie czy macierze sa identyczne (do walidacji ze rownolegle daje to samo co sekwencyjnie)
        public bool IsEqualTo(Matrix other, double tolerance = 1e-6)
        {
            if (Rows != other.Rows || Cols != other.Cols)
                return false;

            for (int i = 0; i < Rows; i++)
            {
                for (int j = 0; j < Cols; j++)
                {
                    if (Math.Abs(Data[i, j] - other.Data[i, j]) > tolerance)
                        return false;
                }
            }
            return true;
        }
    }
}
