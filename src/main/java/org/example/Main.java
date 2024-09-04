package org.example;

import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        PriceInputObject[] priceInput = new PriceInputObject[24];
        Scanner scanner = new Scanner(System.in);
        boolean menuActive = true;
        while (menuActive) {
            System.out.println("Elpriser");
            System.out.println("========");
            System.out.println("1. Inmatning");
            System.out.println("2. Min, Max och Medel");
            System.out.println("3. Sortera");
            System.out.println("4. Bästa Laddningstid (4h)");
            System.out.println("e. Avsluta");
            try {
                String menuInput = scanner.nextLine().toLowerCase();
                switch (menuInput) {
                    case "1":
                        priceInput = priceInput();
                        break;
                    case "2":
                        minMaxAverage(priceInput);
                        break;
                    case "3":
                        sorting(priceInput);
                        break;
                    case "4":
                        bestChargeTime(priceInput);
                        break;
                    case "e":
                        System.out.println("Ending");
                        menuActive = false;
                        break;
                    default:
                        throw new IOException();
                }
            } catch (IOException e) {
                System.out.println("You have to enter a valid menu option - Please try again");
            }
        }
    }

    private static PriceInputObject[] priceInput() {
        System.out.println("Please enter the electricity rate per hour throughout the whole day and night. Rates must be in öre/kWh without decimals");
        PriceInputObject[] hourlyRates = new PriceInputObject[24];
        Scanner rateScanner = new Scanner(System.in);
        for (int i = 0; i < 24; i++) {
            int rate;
            String hourRange = i + "-" + (i + 1);
            System.out.print(i + "-" + (i + 1) + ": ");
            try {
                rate = rateScanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Sorry, you have to type in whole integers in öre/kWh and nothing else. Try again: ");
                rateScanner.nextLine();
                rate = rateScanner.nextInt();
                // TODO: Have to fix this in a good way
            }
            hourlyRates[i] = new PriceInputObject(hourRange, rate);
        }
        return hourlyRates;
    }

    private static void minMaxAverage(PriceInputObject[] input) {
        Arrays.sort(input, new RateComparator()); // Sorting array of classes according to rates
        int max = input[input.length - 1].getRate(); // Finding the highest rate, checking for other hours with the same rate, and printing them
        System.out.println("The highest rate(s) are:");
        for (int i = input.length - 1; i >= 0; i--) {
            if (input[i].getRate() == max) {
                System.out.println(input[i]);
            } else {
                break;
            }
        }
        int min = input[0].getRate(); // Finding the lowest rate, checking for other hours with the same rate, and printing them
        System.out.println("The lowest rate(s) are:");
        for (int i = 0; i < 24; i++) {
            if (input[i].getRate() == min) {
                System.out.println(input[i]);
            } else {
                break;
            }
        }
        OptionalDouble averageRate = Arrays.stream(input) // Calculating average rate and printing
                .mapToInt(PriceInputObject::getRate)
                .average();
        System.out.println("The average rate for the whole 24 hrs is " + averageRate.getAsDouble() + " öre/kWh");
        // TODO: Round it off to less decimals perhaps
    }

    private static void sorting(PriceInputObject[] input) {
        Arrays.sort(input, new RateComparator()); // Sorting array of classes according to rates
        System.out.println("The rates for the whole 24 hour period is as following, sorted from cheapest to most expensive:");
        for (PriceInputObject priceInputObject : input) {
            System.out.println(priceInputObject);
        }
    }

    private static void bestChargeTime(PriceInputObject[] input) {
        PriceInputObject[] rollingAverage = new PriceInputObject[20];
        int sumOfRates = 0;
        int averageRates = 0;

        for (int i = 0; i < input.length - 4; i++) {
            for (int j = 0; j < 4; j++) {
                sumOfRates += input[i + j].getRate();
            }
            System.out.println("Sum of rates is: " + sumOfRates);
            averageRates = sumOfRates / 4;
            String startingTime = input[i].getHourRange();
            rollingAverage[i] = new PriceInputObject(startingTime, averageRates);
            sumOfRates = 0;
        }

        // Printing rolling shit
        for (PriceInputObject priceInputObject : rollingAverage) {
            System.out.println(priceInputObject);
        }

        Arrays.sort(rollingAverage, new RateComparator());
        System.out.println("The best time to start charging your car is at: " + rollingAverage[0]);
        // TODO: make a new class to present double? (or record)
    }
}

class PriceInputObject {
    String hourRange;
    int rate;

    // This is the constructor
    PriceInputObject(String hourRange, int rate) {
        this.hourRange = hourRange;
        this.rate = rate;
    }

    // Getters
    public String getHourRange() {
        return hourRange;
    }

    public int getRate() {
        return rate;
    }

    @Override
    public String toString() {
        return rate + " öre/kWh at the time interval between " + hourRange + " o' clock";
    }
}

class RateComparator implements Comparator<PriceInputObject> {
    @Override
    public int compare(PriceInputObject r1, PriceInputObject r2) {
        return Integer.compare(r1.getRate(), r2.getRate());
    }
}

/*
TODO:
 - Bug test, inputs and stuff
 - Write outputs in a nicer way from the PriceObject class, especially in option 4!
*/
