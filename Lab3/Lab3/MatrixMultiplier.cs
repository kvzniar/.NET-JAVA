namespace Lab3
{
    // klasa wykonujaca mnozenie macierzy roznymi sposobami
    internal class MatrixMultiplier
    {
        // sekwencyjne mnozenie - jeden watek
        public static Matrix MultiplySequential(Matrix a, Matrix b)
        {
            if (a.Cols != b.Rows)
                throw new ArgumentException("Niezgodne rozmiary macierzy.");

            Matrix result = new Matrix(a.Rows, b.Cols);

            for (int i = 0; i < a.Rows; i++)
            {
                for (int j = 0; j < b.Cols; j++)
                {
                    double sum = 0;
                    for (int k = 0; k < a.Cols; k++)
                    {
                        sum += a.Data[i, k] * b.Data[k, j];
                    }
                    result.Data[i, j] = sum;
                }
            }

            return result;
        }

        // ZADANIE 1 - mnozenie z uzyciem biblioteki Parallel (wysoki poziom)
        public static Matrix MultiplyParallel(Matrix a, Matrix b, int threads)
        {
            if (a.Cols != b.Rows)
                throw new ArgumentException("Niezgodne rozmiary macierzy.");

            Matrix result = new Matrix(a.Rows, b.Cols);

            ParallelOptions opt = new ParallelOptions
            {
                MaxDegreeOfParallelism = threads
            };

            // kazdy wiersz macierzy wynikowej liczony jest na osobnym wątku
            Parallel.For(0, a.Rows, opt, i =>
            {
                for (int j = 0; j < b.Cols; j++)
                {
                    double sum = 0;
                    for (int k = 0; k < a.Cols; k++)
                    {
                        sum += a.Data[i, k] * b.Data[k, j];
                    }
                    result.Data[i, j] = sum;
                }
            });

            return result;
        }

        // ZADANIE 2 - mnozenie z uzyciem klasy Thread (niski poziom)
        public static Matrix MultiplyThreads(Matrix a, Matrix b, int threadsCount)
        {
            if (a.Cols != b.Rows)
                throw new ArgumentException("Niezgodne rozmiary macierzy.");

            Matrix result = new Matrix(a.Rows, b.Cols);

            // dzielimy wiersze rownomiernie pomiedzy watki
            Thread[] threads = new Thread[threadsCount];
            int rowsPerThread = a.Rows / threadsCount;
            int rest = a.Rows % threadsCount;

            int currentStart = 0;
            for (int t = 0; t < threadsCount; t++)
            {
                int startRow = currentStart;
                // pierwsze "rest" watkow dostaje jeden wiersz wiecej
                int rowsForThis = rowsPerThread + (t < rest ? 1 : 0);
                int endRow = startRow + rowsForThis;
                currentStart = endRow;

                threads[t] = new Thread(() =>
                {
                    for (int i = startRow; i < endRow; i++)
                    {
                        for (int j = 0; j < b.Cols; j++)
                        {
                            double sum = 0;
                            for (int k = 0; k < a.Cols; k++)
                            {
                                sum += a.Data[i, k] * b.Data[k, j];
                            }
                            // kazdy watek pisze do innej komorki - nie ma kolizji
                            result.Data[i, j] = sum;
                        }
                    }
                });
                threads[t].Name = $"Thread-{t}";
            }

            // start wszystkich
            foreach (var th in threads) th.Start();
            // czekamy az wszystkie skoncza (sychronizacja - Join)
            foreach (var th in threads) th.Join();

            return result;
        }
    }
}
