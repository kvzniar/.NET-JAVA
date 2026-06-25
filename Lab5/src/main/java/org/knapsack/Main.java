package org.knapsack;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Give number of items:");
        int n = sc.nextInt();

        System.out.println("Give seed:");
        int seed = sc.nextInt();

        System.out.println("Give knapsack capacity:");
        int capacity = sc.nextInt();

        Problem problem = new Problem(n, seed, 1, 10);

        System.out.print(problem);
        System.out.println("-------");

        Result result = problem.Solve(capacity);
        System.out.println(result);
    }
}
