package root;

import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        PriceInputObject[] priceInput = new PriceInputObject[24];
        Scanner scanner = new Scanner(System.in);
        boolean menuActive = true;
        boolean ratesEntered = false;
        while (menuActive) {
            System.out.println();
            System.out.println("ELPRISER");
            System.out.println("========");
            System.out.println("1. Inmatning");
            System.out.println("2. Min, Max och Medel");
            System.out.println("3. Sortera");
            System.out.println("4. Bästa Laddningstid (4h)");
            System.out.println("e. Avsluta");
            System.out.println();
            System.out.print("Please enter your choice: ");
            try {
                String menuInput = scanner.nextLine().toLowerCase();
                switch (menuInput) {
                    case "1":
                        priceInput = priceInput();
                        ratesEntered = true;
                        break;
                    case "2":
                        if (ratesEntered) {
                            minMaxAverage(priceInput);
                            break;
                        } else {
                            throw new NullPointerException();
                        }
                    case "3":
                        if (ratesEntered) {
                            sorting(priceInput);
                            break;
                        } else {
                            throw new NullPointerException();
                        }
                    case "4":
                        if (ratesEntered) {
                            bestChargeTime(priceInput);
                            break;
                        } else {
                            throw new NullPointerException();
                        }
                    case "e":
                        System.out.println("Ending");
                        menuActive = false;
                        break;
                    default:
                        throw new IOException();
                }
            } catch (IOException e) {
                System.out.println("You have to enter a valid menu option - Please try again");
            } catch (NullPointerException e) {
                System.out.println("Please enter the rates (option 1) before choosing any of the other options");
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
            while (true) {
                System.out.print(i + "-" + (i + 1) + ": ");

                if (rateScanner.hasNextInt()) {
                    rate = rateScanner.nextInt();
                    rateScanner.nextLine();
                    hourlyRates[i] = new PriceInputObject(hourRange, rate);
                    break;
                } else {
                    rateScanner.nextLine(); // Avoid infinite loop by consuming the invalid input
                    System.out.println("Invalid input. Please enter an integer.");
                }
            }
        }
        return hourlyRates;
    }

    private static void minMaxAverage(PriceInputObject[] input) {
        Arrays.sort(input, new RateComparator()); // Sorting array of classes according to rates
        int max = input[input.length - 1].getRate(); // Finding the highest rate, checking for other hours with the same rate, and printing them
        System.out.println("The highest rate(s) are:");
        for (int i = input.length - 1; i >= 0; i--) {
            if (input[i].getRate() == max) {
                System.out.println("- " + input[i]);
            } else {
                break;
            }
        }
        int min = input[0].getRate(); // Finding the lowest rate, checking for other hours with the same rate, and printing them
        System.out.println("The lowest rate(s) are:");
        for (int i = 0; i < 24; i++) {
            if (input[i].getRate() == min) {
                System.out.println("- " + input[i]);
            } else {
                break;
            }
        }
        OptionalDouble averageRate = Arrays.stream(input) // Calculating average rate and printing
                .mapToInt(PriceInputObject::getRate).average();
        if (averageRate.isPresent()) {
            System.out.println("The average rate for the whole 24 hrs is " + (Math.round(averageRate.getAsDouble() * 10)) / 10.0 + " öre/kWh");
        }
    }

    private static void sorting(PriceInputObject[] input) {
        PriceInputObject[] sortedInput = Arrays.copyOf(input, input.length);
        Arrays.sort(sortedInput, new RateComparator()); // Sorting array of classes according to rates
        System.out.println("The rates for the whole 24 hour period is as following, sorted from cheapest to most expensive:");
        for (PriceInputObject priceInputObject : sortedInput) {
            System.out.println("- " + priceInputObject);
        }
    }

    private static void bestChargeTime(PriceInputObject[] input) {
        AverageRateList[] rollingAverage = new AverageRateList[21];
        int sumOfRates = 0;
        double averageRates;

        for (int i = 0; i < input.length - 3; i++) {
            for (int j = 0; j < 4; j++) {
                sumOfRates += input[i + j].getRate();
            }
            averageRates = sumOfRates / 4.00;
            String startingTime = input[i].getHourRange();
            rollingAverage[i] = new AverageRateList(startingTime, averageRates);
            sumOfRates = 0;
        }

        Arrays.sort(rollingAverage, new AverageRateComparator());
        System.out.println("The best time to start charging your car is at " + rollingAverage[0].getHourRange() + " o' clock at a an average rate of " + rollingAverage[0].getRate() + " öre/kWh");
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

class AverageRateList {
    String hourRange;
    double rate;

    // This is the constructor
    AverageRateList(String hourRange, double rate) {
        this.hourRange = hourRange;
        this.rate = rate;
    }

    // Getters
    public String getHourRange() {
        return hourRange;
    }

    public Double getRate() {
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

class AverageRateComparator implements Comparator<AverageRateList> {
    @Override
    public int compare(AverageRateList dp1, AverageRateList dp2) {
        return dp1.getRate().compareTo(dp2.getRate());
    }
}

/*
TODO:
 - Bug test, inputs and stuff
 - Clean up code, split into separate documents?

LEARNINGS:
- Could I do it better than having two separate list classes (for int and double) and two dedicated comparators?
*/
