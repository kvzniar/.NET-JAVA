using System.Diagnostics;

namespace Lab3
{
    internal class Program
    {
        static void Main(string[] args)
        {
            Console.WriteLine("=== Lab 3 - Mnozenie macierzy wielowatkowo ===");
            Console.WriteLine($"Liczba dostepnych rdzeni: {Environment.ProcessorCount}");
            Console.WriteLine();

            // mozliwosc podania rozmiaru jako parametr z argv (zad. 1 wymaga parametru)
            int size = 300;
            if (args.Length >= 1 && int.TryParse(args[0], out int s)) size = s;

            // ile prob do usrednienia
            int trials = 3;

            int[] threadCounts = { 1, 2, 4, 8, 16 };
            int[] sizes = { 100, 200, size };

            // mozliwosc podgladu wynikow dla malej macierzy (do sprawdzenia poprawnosci)
            Console.WriteLine("--- Test poprawnosci na malej macierzy (5x5) ---");
            CorrectnessTest();
            Console.WriteLine();

            // Glowne badania
            foreach (int n in sizes.Distinct().OrderBy(x => x))
            {
                Console.WriteLine($"========== Rozmiar macierzy {n}x{n} ==========");
                RunBenchmark(n, threadCounts, trials);
                Console.WriteLine();
            }

            Console.WriteLine("Koniec. Nacisnij Enter aby zakonczyc...");
            Console.ReadLine();
        }

        static void CorrectnessTest()
        {
            var rnd = new Random(42);
            var a = new Matrix(5, 5);
            var b = new Matrix(5, 5);
            a.FillRandom(rnd);
            b.FillRandom(rnd);

            var seq = MatrixMultiplier.MultiplySequential(a, b);
            var par = MatrixMultiplier.MultiplyParallel(a, b, 4);
            var thr = MatrixMultiplier.MultiplyThreads(a, b, 4);

            Console.WriteLine($"Parallel == Sequential : {seq.IsEqualTo(par)}");
            Console.WriteLine($"Threads  == Sequential : {seq.IsEqualTo(thr)}");

            Console.WriteLine("\nMacierz wynikowa (Sequential):");
            seq.Print();
        }

        static void RunBenchmark(int size, int[] threadCounts, int trials)
        {
            var rnd = new Random(123);
            var a = new Matrix(size, size);
            var b = new Matrix(size, size);
            a.FillRandom(rnd);
            b.FillRandom(rnd);

            // 1. SEKWENCYJNE (1 watek - punkt odniesienia)
            double seqTime = MeasureAvg(() => MatrixMultiplier.MultiplySequential(a, b), trials);
            Console.WriteLine($"Sekwencyjne (1 watek)        : {seqTime,8:F2} ms   speedup = 1.00x");
            Console.WriteLine();

            // 2. PARALLEL.FOR
            Console.WriteLine("--- Parallel.For ---");
            Console.WriteLine($"{"Watki",-8}{"Czas [ms]",-15}{"Speedup",-10}");
            foreach (int tc in threadCounts)
            {
                double t = MeasureAvg(() => MatrixMultiplier.MultiplyParallel(a, b, tc), trials);
                double speedup = seqTime / t;
                Console.WriteLine($"{tc,-8}{t,-15:F2}{speedup,-10:F2}");
            }

            // 3. THREAD
            Console.WriteLine("\n--- Klasa Thread ---");
            Console.WriteLine($"{"Watki",-8}{"Czas [ms]",-15}{"Speedup",-10}");
            foreach (int tc in threadCounts)
            {
                double t = MeasureAvg(() => MatrixMultiplier.MultiplyThreads(a, b, tc), trials);
                double speedup = seqTime / t;
                Console.WriteLine($"{tc,-8}{t,-15:F2}{speedup,-10:F2}");
            }
        }

        // pomiar sredniego czasu z kilku prob
        static double MeasureAvg(Action action, int trials)
        {
            // jedno "rozgrzewkowe" wywolanie zeby JIT skompilowal
            action();

            var sw = new Stopwatch();
            double total = 0;
            for (int i = 0; i < trials; i++)
            {
                sw.Restart();
                action();
                sw.Stop();
                total += sw.Elapsed.TotalMilliseconds;
            }
            return total / trials;
        }
    }
}
