package service;

import com.cartrawler.assessment.car.CarResult;
import com.cartrawler.assessment.service.CarService;
import com.cartrawler.assessment.util.CarUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    private final CarService carService = new CarService();

    @Test
    void testGetCarsResultFiltersAndCombinesCorrectly() {
        // Arrange
        CarResult car1 = new CarResult("Peugeot 107", "SIXT", "MCMR", 19.1d, CarResult.FuelPolicy.FULLEMPTY);
        CarResult car2 = new CarResult("Ford Galaxy", "SIXT", "CMMV", 160.75d, CarResult.FuelPolicy.FULLFULL);
        CarResult car3 = new CarResult("Citroen Berlingo", "SIXT", "CMMV", 134.8d, CarResult.FuelPolicy.FULLEMPTY);
        CarResult car4 = new CarResult("Audi A3", "RECORD", "ECMR", 18.37d, CarResult.FuelPolicy.FULLFULL);

        Set<CarResult> input = Set.of(car1, car2, car3, car4);

        try (MockedStatic<CarUtil> mocked = mockStatic(CarUtil.class)) {
            mocked.when(() -> CarUtil.fetchProperty("corporate.names", "application.properties"))
                    .thenReturn("AVIS,BUDGET,ENTERPRISE,FIREFLY,HERTZ,SIXT,THRIFTY");

            mocked.when(() -> CarUtil.computeMedianOfRentalCost(Set.of(car1, car2, car3)))
                    .thenReturn(134.8d);
            mocked.when(() -> CarUtil.computeMedianOfRentalCost(Set.of(car4)))
                    .thenReturn(18.37);

            CarService spyService = spy(carService);
            doReturn(List.of(car1, car3, car2)).when(spyService)
                    .getFilteredCars(Set.of(car1, car2, car3), 134.8d);
            doReturn(List.of(car4)).when(spyService)
                    .getFilteredCars(Set.of(car4), 18.37);

            // Act
            Set<CarResult> result = spyService.getCarsResult(input);

            // Assert
            List<CarResult> expectedOrder = List.of(car1, car3, car2, car4);
            assertEquals(expectedOrder, new ArrayList<>(result));
        }
    }

    @Test
    void testGetFilteredCarsFiltersAndSortsCorrectly() {
        // Arrange
        CarResult car1 = new CarResult("Peugeot 107", "SIXT", "MCMR", 19.1d, CarResult.FuelPolicy.FULLEMPTY);
        CarResult car2 = new CarResult("Citroen Berlingo", "SIXT", "CMMV", 134.8d, CarResult.FuelPolicy.FULLEMPTY);
        CarResult car3 = new CarResult("Ford Galaxy", "SIXT", "CMMV", 160.75d, CarResult.FuelPolicy.FULLFULL);
        CarResult car4 = new CarResult("Audi A3", "SIXT", "ECMR", 18.37d, CarResult.FuelPolicy.FULLFULL);

        Set<CarResult> input = Set.of(car1, car2, car3, car4);
        double median = 100.0;

        // Act
        List<CarResult> result = carService.getFilteredCars(input, median);

        assertEquals(List.of(car1, car4, car2), result);
    }

    @Test
    void testGetFilteredCarsReturnsEmptyWhenNoneMatch() {
        CarResult car1 = new CarResult("Peugeot 107", "SIXT", "MCMR", 19.1d, CarResult.FuelPolicy.FULLFULL);
        CarResult car2 = new CarResult("Citroen Berlingo", "SIXT", "CMMV", 34.8d, CarResult.FuelPolicy.FULLFULL);

        Set<CarResult> input = Set.of(car1, car2);
        double median = 10.0;

        List<CarResult> result = carService.getFilteredCars(input, median);

        assertTrue(result.isEmpty());
    }
}

