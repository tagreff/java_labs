package com.tagreff.lab27;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args)  {
        Scanner myInput = new Scanner( System.in );
        int n;
        System.out.print( "Enter integer: " );
        n = myInput.nextInt();
        int[][] table = new int[n][n];
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[i].length; j++) {
                table[i][j] = (int)(Math.random()*100);
                System.out.print(table [i][j] + " ");
            }
            System.out.println();
        }

        System.out.println("Среднее значение: " + avnNum(table));
        System.out.println("Одномерный массив: " + Arrays.toString(to1dimension(table)));
    }

    public static int[] to1dimension(int[][] data) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int[] datum : data) {
            for (int i : datum) {
                list.add(i);
            }
        }
        return  list.stream().mapToInt(i -> i).toArray();
    }

    public static double avnNum(int[][] data){
        return Arrays.stream(data).flatMapToInt(Arrays::stream).average().getAsDouble();
    }
}
