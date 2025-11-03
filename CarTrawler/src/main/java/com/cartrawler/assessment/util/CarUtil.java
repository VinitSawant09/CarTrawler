package com.cartrawler.assessment.util;

import com.cartrawler.assessment.app.AssessmentRunner;
import com.cartrawler.assessment.car.CarResult;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class CarUtil {

    public static String fetchProperty (String propertyName, String propertyFileName) {
        Properties props = new Properties();
        try (InputStream input = AssessmentRunner.class.getClassLoader().getResourceAsStream(propertyFileName)) {
            if (input != null) {
                props.load(input);
                return props.getProperty(propertyName, "");
            } else {
                throw new FileNotFoundException("property file " + propertyFileName + " not found");
            }
        } catch (IOException e) {
            System.out.println("Error loading " + propertyFileName);
        }
        return "";
    }

    public static double computeMedianOfRentalCost(Set<CarResult> carSet) {
        List<Double> prices = carSet.stream()
                .map(CarResult::rentalCost)
                .sorted()
                .toList();

        if (prices.isEmpty()) return 0.0;
        int size = prices.size();
        if (size % 2 == 1) {
            return prices.get(size / 2);
        } else {
            return (prices.get(size / 2 - 1) + prices.get(size / 2)) / 2.0;
        }
    }

    public static boolean isNotAboveMedianWithFullFuel(CarResult car, double medianValue) {
        if(car==null){
            return false;
        }
        return !(car.rentalCost() > medianValue)
                || car.fuelPolicy() != CarResult.FuelPolicy.FULLFULL;
    }

    public static Comparator<CarResult> getCarResultComparator() {
        List<Character> priorityOrder = List.of('M', 'E', 'C');
        return Comparator
                .comparingInt((CarResult c) -> {
                    char first = Character.toUpperCase(c.sippCode().charAt(0));
                    int idx = priorityOrder.indexOf(first);
                    return idx == -1 ? Integer.MAX_VALUE : idx; // non-priority go last
                })
                .thenComparing(CarResult::sippCode)   // secondary sort by alphabetical order
                .thenComparing(CarResult::rentalCost);// third sort by rental cost
    }
}
