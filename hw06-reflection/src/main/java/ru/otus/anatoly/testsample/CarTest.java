package ru.otus.anatoly.testsample;

import ru.otus.anatoly.annotation.After;
import ru.otus.anatoly.annotation.Before;
import ru.otus.anatoly.annotation.Test;

/**
 * Тестовый класс для тестирования модели Car.
 */
public class CarTest {
    
    private Car car;

    @Before
    public void setUp() {
        // Создаём новую машину перед каждым тестом с параметрами: 0.1 л/км расход, макс скорость 200 км/ч, бак 50 литров
        car = new Car(0.1f, 200f, 50f);
        System.out.printf("[SETUP] Created new Car: consumption= %f L/km, maxSpeed= %f km/h, tankCapacity= %f L %n",
                car.getConsumption(), car.getMaxSpeed(), car.getTankCapacity());
    }

    @After
    public void tearDown() {
        System.out.printf("[TEARDOWN] Final state: fuel= %f L, speed= %f km/h %n", car.getFuel(), car.getSpeed());
    }
    
    @Test
    public void testRefuelWithValidAmount() {
        float refuelAmount = 30.5f;
        car.refuel(refuelAmount);
        if (car.getFuel() != refuelAmount) {
            throw new AssertionError("Expected fuel=" + refuelAmount + ", but got " + car.getFuel());
        }
    }
    
    @Test
    public void testRefuelExceedsCapacity() {
        float tooMuchFuel = 100f;
        try {
            car.refuel(tooMuchFuel);
            throw new AssertionError("Expected IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException e) {
        }
    }
    
    @Test
    public void testRefuelNegativeAmount() {
        try {
            car.refuel(-5f);
            throw new AssertionError("Expected IllegalArgumentException for negative amount");
        } catch (IllegalArgumentException e) {
        }
    }
    
    @Test
    public void testAccelerateToMaxSpeed() {
        // Разгон с 0 до максимума
        car.accelerate(50f);
        if (car.getSpeed() != 50) {
            throw new AssertionError("Expected speed=50, got " + car.getSpeed());
        }
        
        car.accelerate(100f);
        if (car.getSpeed() != 150) {
            throw new AssertionError("Expected speed=150, got " + car.getSpeed());
        }
    }
    
    @Test
    public void testAccelerateBeyondLimit() {
        try {
            car.accelerate(250f); // макс всего 200
            throw new AssertionError("Expected exception for exceeding max speed");
        } catch (IllegalArgumentException e) {
        }
    }
    
    @Test
    public void testDecelerate() {
        car.accelerate(100f); // сначала разгоняемся
        
        if (car.getSpeed() != 100) {
            throw new AssertionError("Expected speed=100, got " + car.getSpeed());
        }
        
        car.decelerate(30f);
        if (car.getSpeed() != 70) {
            throw new AssertionError("Expected speed=70 after deceleration, got " + car.getSpeed());
        }
    }
    
    @Test
    public void testDecelerateToZero() {
        car.accelerate(50f);
        car.decelerate(50f); // полная остановка
        
        if (car.getSpeed() != 0) {
            throw new AssertionError("Expected speed=0, got " + car.getSpeed());
        }
    }
    
    @Test
    public void testDriveWithFuelAndNoConsumption() {
        // Машина стоит (скорость=0), пытаемся ехать
        try {
            car.drive(60f); // 60 секунд движения
            throw new AssertionError("Expected exception for driving without speed");
        } catch (IllegalArgumentException e) {
        }
    }
    
    @Test
    public void testDriveWithFuelAndConsumption() {
        float initialFuel = 20f;
        car.refuel(initialFuel); // заправляем 20 литров
        car.accelerate(60f); // 60 км/ч
        car.drive(2f); // 2 часа
        float expectedConsumption = 0.1f * 60.0f * 2.0f; // 12 литров

        try {
            if (Math.abs(car.getFuel() - (initialFuel - expectedConsumption)) > 0.01f) {
                throw new AssertionError("Expected fuel=" + (initialFuel - expectedConsumption) +
                    ", got " + car.getFuel());
            }
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testDriveWithoutFuel() {
        // Попытка ехать без топлива
        try {
            car.accelerate(50f);
            car.drive(1000f); // 1000 секунд движения при скорости 50 км/ч
            throw new AssertionError("Expected exception for driving without fuel");
        } catch (IllegalArgumentException e) {
        }
    }
    
    @Test
    public void testNegativeAcceleration() {
        try {
            car.accelerate(-10f); // отрицательное ускорение - это не разгон, а торможение
            throw new AssertionError("Expected exception for negative acceleration");
        } catch (IllegalArgumentException e) {
        }
    }
    
    @Test
    public void testZeroDriveDuration() {
        try {
            car.drive(0f); // нулевая длительность движения
            throw new AssertionError("Expected exception for zero duration");
        } catch (IllegalArgumentException e) {
        }
    }
}
