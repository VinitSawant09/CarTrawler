package com.cartrawler.assessment.service;

import com.cartrawler.assessment.car.CarResult;
import com.cartrawler.assessment.car.SupplierType;
import com.cartrawler.assessment.util.CarUtil;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CarService {

    public Set<CarResult> getCarsResult(Set<CarResult> cars) {

        /*Fetching corporate names from property file*/
        String corporateNames = CarUtil.fetchProperty("corporate.names", "application.properties");

        /*Separating the base set into two sets corporate and non-corporate*/

        Map<SupplierType, Set<CarResult>> groups = cars.stream().distinct()
                .collect(Collectors.groupingBy(
                        cr -> corporateNames.contains(cr.supplierName())
                                ? SupplierType.CORPORATE
                                : SupplierType.NON_CORPORATE,
                        Collectors.toSet()
                ));
        Set<CarResult> corporateCars = groups.get(SupplierType.CORPORATE);
        Set<CarResult> nonCorporateCars = groups.get(SupplierType.NON_CORPORATE);

        List<CarResult> corporateCarsList = processGroup(corporateCars, "Corporate Cars");
        List<CarResult> nonCorporateCarsList = processGroup(nonCorporateCars, "Non-Corporate Cars");

        /*Combine corporate and non-corporate result by retaining the order*/
        Set<CarResult> resultSet = new LinkedHashSet<>();
        resultSet.addAll(corporateCarsList);
        resultSet.addAll(nonCorporateCarsList);
        return resultSet;
    }

    public List<CarResult> getFilteredCars(Set<CarResult> cars, double medianOfRentalCost) {
        return cars.stream()
                   .sorted(CarUtil.getCarResultComparator())
                   .filter(car -> CarUtil.isNotAboveMedianWithFullFuel(car, medianOfRentalCost)) //Bonus/Optional step
                   .toList();
    }

    private List<CarResult> processGroup(Set<CarResult> cars, String label) {
        double median = CarUtil.computeMedianOfRentalCost(cars);
        System.out.println("Median of Rental Cost of " + label + ": " + median);
        return getFilteredCars(cars, median);
    }

}
