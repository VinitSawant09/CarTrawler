package util;

import com.cartrawler.assessment.car.CarResult;
import com.cartrawler.assessment.util.CarUtil;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CarUtilTest {

    @Test
    void testFetchCorporateNamesReturnsExpectedValue() {
        String result = CarUtil.fetchProperty("corporate.names", "application.properties");
        assertNotNull(result);
        assertEquals("AVIS,BUDGET,ENTERPRISE,FIREFLY,HERTZ,SIXT,THRIFTY", result);
    }

    @Test
    void testFetchPropetyReturnsEmptyWhenMissing() {
        String result = CarUtil.fetchProperty("abc.def","application.properties");
        assertEquals("", result);
    }

    @Test
    void testFetchPropetyWhenFileIsMissing() {
        String result = CarUtil.fetchProperty("abc.def","applications.properties");
        assertEquals("", result);
    }

    @Test
    void testEmptySetReturnsZero() {
        Set<CarResult> cars = Set.of();
        double median = CarUtil.computeMedianOfRentalCost(cars);
        assertEquals(0.0, median, "Median of empty set should be 0.0");
    }

    @Test
    void testWithOddCars() {
        Set<CarResult> cars = Set.of(
                new CarResult("Peugeot 107", "HERTZ", "MCMR", 60.34d, CarResult.FuelPolicy.FULLFULL),
                new CarResult("Peugeot 107", "HERTZ", "MCMR", 67.98d, CarResult.FuelPolicy.FULLFULL),
                new CarResult("Peugeot 107", "ENTERPRISE", "MCMR", 78.1d, CarResult.FuelPolicy.FULLFULL),
                new CarResult("Volkswagen Passat Estate", "SIXT", "SWMR", 292.78d, CarResult.FuelPolicy.FULLFULL),
                new CarResult("Ford Galaxy", "SIXT", "FVMR", 692.45d, CarResult.FuelPolicy.FULLFULL)
        );
        double median = CarUtil.computeMedianOfRentalCost(cars);
        assertEquals(78.1d, median, "Median of [60.34, 67.98, 78.1, 292.78, 692.45] should be 20");
    }

    @Test
    void testWithEvenCars() {
        Set<CarResult> cars = Set.of(
                new CarResult("Peugeot 107", "HERTZ", "MCMR", 60.34d, CarResult.FuelPolicy.FULLFULL),
                new CarResult("Peugeot 107", "HERTZ", "MCMR", 67.98d, CarResult.FuelPolicy.FULLFULL),
                new CarResult("Peugeot 107", "ENTERPRISE", "MCMR", 78.1d, CarResult.FuelPolicy.FULLFULL),
                new CarResult("Volkswagen Passat Estate", "SIXT", "SWMR", 292.78d, CarResult.FuelPolicy.FULLFULL)
        );
        double median = CarUtil.computeMedianOfRentalCost(cars);
        assertEquals(73.03999999999999, median, "Median of [60.34, 67.98, 78.1, 292.78] should be 73.03999999999999");
    }

    @Test
    void testWithSingleCar() {
        Set<CarResult> cars = Set.of(
                new CarResult("Volkswagen Passat Estate", "SIXT", "SWMR", 292.78d, CarResult.FuelPolicy.FULLFULL)
        );
        double median = CarUtil.computeMedianOfRentalCost(cars);
        assertEquals(292.78, median, "Median of [292.78] should be 292.78");
    }

    @Test
    void returnsFalseWhenCarIsNull() {
        assertFalse(CarUtil.isNotAboveMedianWithFullFuel(null, 100.0));
    }

    @Test
    void returnsTrueWhenRentalCostIsBelowMedianAndFuelIsFullFull() {
        CarResult car = new CarResult("Peugeot 107", "HERTZ", "MCMR", 60.34d, CarResult.FuelPolicy.FULLFULL);
        assertTrue(CarUtil.isNotAboveMedianWithFullFuel(car, 100.0));
    }

    @Test
    void returnsTrueWhenRentalCostIsAboveMedianButFuelIsNotFullFull() {
        CarResult car= new CarResult("Volkswagen Passat Estate", "SIXT", "MCMR", 100.34d, CarResult.FuelPolicy.FULLEMPTY);
        assertTrue(CarUtil.isNotAboveMedianWithFullFuel(car, 100.0));
    }

    @Test
    void returnsFalseWhenRentalCostIsAboveMedianAndFuelIsFullFull() {
        CarResult car= new CarResult("Peugeot 107", "HERTZ", "MCMR", 100.01d, CarResult.FuelPolicy.FULLFULL);
        assertFalse(CarUtil.isNotAboveMedianWithFullFuel(car, 100.0));
    }

    @Test
    void returnsTrueWhenRentalCostEqualsMedianAndFuelIsFullFull() {
        CarResult car= new CarResult("Peugeot 107", "SIXT", "MCMR", 100d, CarResult.FuelPolicy.FULLFULL);
        assertTrue(CarUtil.isNotAboveMedianWithFullFuel(car, 100.0));
    }

    @Test
    void testCarResultComparatorPriorityOrder() {
        CarResult car1 = new CarResult("Peugeot 107", "SIXT", "MCMR", 19.1d, CarResult.FuelPolicy.FULLEMPTY); // Priority 0
        CarResult car2 = new CarResult("Citroen Berlingo", "SIXT", "CMMV", 34.8d, CarResult.FuelPolicy.FULLEMPTY); // Priority 3
        CarResult car3 = new CarResult("Ford Galaxy", "SIXT", "CMMV", 160.75d, CarResult.FuelPolicy.FULLEMPTY); // Priority 4
        CarResult car4 = new CarResult("Audi A3", "SIXT", "ECMR", 186.37d, CarResult.FuelPolicy.FULLFULL); // Priority 2

        List<CarResult> cars = new ArrayList<>(List.of(car4, car3, car2, car1));
        cars.sort(CarUtil.getCarResultComparator());
        assertEquals(List.of(car1, car4, car2, car3), cars);
    }

    @Test
    void testCarResultComparatorAlphabeticalTieBreaker() {
        CarResult car1 = new CarResult("Peugeot 107", "RECORD", "MCMR", 19.1d, CarResult.FuelPolicy.FULLEMPTY); // Priority 0
        CarResult car2 = new CarResult("Citroen Berlingo", "RECORD", "MCME", 34.8d, CarResult.FuelPolicy.FULLEMPTY); // Priority 1

        List<CarResult> cars = new ArrayList<>(List.of(car2, car1));
        cars.sort(CarUtil.getCarResultComparator());

        assertEquals(List.of(car2, car1), cars);
    }

    @Test
    void testCarResultComparatorRentalCostTieBreaker() {
        CarResult car1 = new CarResult("Peugeot 107", "SIXT", "RECORDA", 19.1d, CarResult.FuelPolicy.FULLEMPTY); // Priority 0
        CarResult car2 = new CarResult("Citroen Berlingo", "SIXT", "RECORDA", 19.1d, CarResult.FuelPolicy.FULLEMPTY); // Priority 1

        List<CarResult> cars = new ArrayList<>(List.of(car1, car2));
        cars.sort(CarUtil.getCarResultComparator());

        assertEquals(List.of(car1, car2), cars);
    }

}

