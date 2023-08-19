package com.coding.task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Application {
    private static final int X_MAX = (int) Math.pow(10, 7);
    private static final int Y_MAX = 2* (int) Math.pow(10, 5);
    private static final long K_MAX = (long) Math.pow(10, 9);

    private static final Scanner scanner = new Scanner(System.in);

    /**
     * This code could be solved using many approaches for the input in this case we will consider two:
     * Consider asking the user for the input (default option)
     * Consider using a filename that contains the input in each line (uncomment this option to test it)
     * */
    public static void main(String[] args) {
        //Use this function to execute the method when a user inputs the values
        askUserOption();
        //In case you want to test it using a file as input use the following method
        //readFileOption();
    }

    private static void askUserOption () {
        int x, y;
        do {
            System.out.println("Introduce the X and Y values separated by comma");
            String input = scanner.nextLine();
            String[] xyValues = input.split(" ");
            if (xyValues.length != 2) {
                System.out.println("Wrong number of arguments");
                continue; // We need to execute the process again if the user doesn't introduce exactly 2 values
            }
            String value = "X";
            try {
                x = Integer.parseInt(xyValues[0]);
                value = "Y";
                y = Integer.parseInt(xyValues[1]);
            } catch (NumberFormatException e) {
                //Instead of validating if the input is numeric here we can make sure the input are valid numbers
                System.out.println(value + " is not a valid number");
                continue;
            }
            if (invalidXY(x, y)) {
                continue;
            }
            break;
        } while (true);

        long[] values = new long[x];
        Arrays.fill(values, 0L);
        operate(values, 1, y, x);
        // In this case I decided to iterate the array as a stream and perform the function max,
        // but we also could implement a recursive method to get the greatest value like
        // private static long getGreatest (long[] values, int position) {
        // if (position == values.length) return 0;
        // return Math.max(values[position], getGreatest(values), position + 1);
        // }
        System.out.println("Result: " + Arrays.stream(values).max().orElse(0));
    }

    private static boolean invalidXY(int x, int y) {
        if (x < 3 || x > X_MAX) {
            System.out.println("X value should be more than or equals 3 and less than or equals " + X_MAX);
            return true;
        }
        if (y < 1 || y > Y_MAX) {
            System.out.println("Y value should be more than or equals 1 and less than or equals " + Y_MAX);
            return true;
        }
        return false;
    }

    public static void operate(long[] values, int row, int yValue, int xValue) {
        //We could implement a for loop from 1 to y to loop the process of getting all the y rows
        // but in this case I think the best option is make a recursive function
        if (row > yValue) {
            return;
        }
        int i, j;
        long k;
        do {
            System.out.println("Introduce i j and k values as string separated by comma");
            String ijkValues = scanner.nextLine();
            String[] splitValues = ijkValues.split(" ");
            if (splitValues.length != 3) {
                System.out.println("Make sure your input is exactly 3 values");
                continue;
            }
            String val = "I";
            try {
                i = Integer.parseInt(splitValues[0]);
                val = "J";
                j = Integer.parseInt(splitValues[1]);
                val = "K";
                k = Long.parseLong(splitValues[2]);
            } catch (NumberFormatException e) {
                System.out.println(val + " Must be a valid number");
                continue;
            }
            if (invalidIJK(i, j, k, xValue)) {
                continue;
            }
            break;
        } while (true);
        //In this case we need to send the values of i and j decreased by one in order to keep all in the boundaries of the array
        updateValue(values, i - 1, j - 1, i - 1, k);
        operate(values, row + 1, yValue, xValue);
    }

    private static boolean invalidIJK(int i, int j, long k, int xValue) {
        if (i < 1 || i > j) {
            System.out.println("I value should be more than or equals 1 and less than or equals J value");
            return true;
        }
        if (j > xValue) {
            System.out.println("J value should be more than or equals I value and less than or equals X value");
            return true;
        }
        if (k < 0 || k > K_MAX) {
            System.out.println("K value should be more than or equals 0 and less than or equals " + K_MAX);
            return true;
        }
        return false;
    }

    public static void updateValue(long[] values, int start, int end, int position,  long kValue) {
        // We could create a loop to update the values in the array but a recursive function can be better;
        if (position < start || position > end) {
            return;
        }
        values[position] = values[position] + kValue;
        updateValue(values, start, end, position + 1, kValue);
    }

    //The following are methods to solve the problem using a file instead of asking the user to introduce each line
    private static void readFileOption() {
        // In this scenario I will execute the algorithm y = min(inputY, minlinesInFile) times
        // but just y - error times the process of adding elements will be executed
        // Consider y - error because if an element withing a row is validated as invalid the process will skip that value and continue with the execution

        /*
         * Uncomment the following lines and the line 153 if you want to introduce your own filename
         * //System.out.println("Introduce the file path ");
         * //String fileName = scanner.nextLine();
         */
        try {
            //File file = new File(fileName);
            File file = new File(System.getProperties().get("user.dir") + "/src/com/coding/task/input.txt");
            if (!(file.exists() && file.isFile())) {
                System.out.println("File not found");
                return;
            }
            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.size() <= 1) {
                System.out.println("File must contain at least 2 lines");
                return;
            }
            String[] xyValues = lines.get(0).split(" ");
            if (xyValues.length != 2) {
                System.out.println("First line should contain two values");
                return; //The process will stop since the file is malformed
            }
            String value = "X";
            int x, y;
            try {
                x = Integer.parseInt(xyValues[0]);
                value = "Y";
                y = Integer.parseInt(xyValues[1]);
            } catch (NumberFormatException e) {
                //Instead of validating if the input is numeric here we can make sure the input are valid numbers
                System.out.println(value + " in file is not a valid number");
                return;
            }
            if (invalidXY(x, y)) {
                return;
            }
            processLines(lines.subList(1, lines.size()), x, y);
        } catch (IOException e) {
            System.out.println("Error reading file");
        }
    }

    //This method will process the lines in the file using a stream since it is faster than a for loop
    private static void processLines (List<String> lines, int xValue, int yValue) {
        long[] values = new long[xValue];
        Arrays.fill(values, 0L);
        int[] currentRow = {1};
        int[] errors = {0};
        if (lines.size() < yValue) {
            System.out.println("Lines in file are less than the actual number of lines in Y value");
            return;
        }
        lines.forEach(line -> {
            if (currentRow[0] > yValue) {
                return;
            }
            currentRow[0] += 1;
            String[] splitValues = line.split(" ");
            if (splitValues.length != 3) {
                System.out.println("Make sure your input is exactly 3 values");
                errors[0] += 1;
                return;
            }
            String val = "I";
            int i, j;
            long k;
            try {
                i = Integer.parseInt(splitValues[0]);
                val = "J";
                j = Integer.parseInt(splitValues[1]);
                val = "K";
                k = Long.parseLong(splitValues[2]);
            } catch (NumberFormatException e) {
                System.out.println(val + " Must be a valid number");
                errors[0] += 1;
                return;
            }
            if (invalidIJK(i, j, k, xValue)) {
                errors[0] += 1;
                return;
            }
            updateValue(values, i - 1, j - 1, i - 1, k);
        });
        if (errors [0] != 0) {
            System.out.println("Lines with errors " + errors[0]);
        }
        System.out.println("Result: " + Arrays.stream(values).max().orElse(0));
    }


}
