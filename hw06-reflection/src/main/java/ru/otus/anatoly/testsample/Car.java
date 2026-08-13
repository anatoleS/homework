package ru.otus.anatoly.testsample;

import lombok.Data;

/**
 * Модель автомобиля с параметрами: расход топлива, максимальная скорость, объём бака.
 * Модель упрощена, нет зависимостей от среды, ускорение мгновенное, расход топлива зависит от пути
 */
@Data
public class Car {

    private float fuel;              // текущее топливо в литрах
    private float speed;             // текущая скорость в км/ч

    private final float consumption; // расход на километр (л/км)
    private final float maxSpeed;    // максимальная скорость (км/ч)
    private final float tankCapacity;// объём бака (литры)

    /**
     * Конструктор автомобиля с валидацией параметров.
     *
     * @param consumption    расход топлива на километр в литрах (должен быть > 0)
     * @param maxSpeed       максимальная скорость в км/час (должна быть > 0)
     * @param tankCapacity   объём бака в литрах (должен быть > 0)
     */
    public Car(float consumption, float maxSpeed, float tankCapacity) {
        this.consumption = checkValue(consumption, "Consumption must be greater than zero");
        this.maxSpeed = checkValue(maxSpeed, "Max speed must be greater than zero");
        this.tankCapacity = checkValue(tankCapacity, "Tank capacity must be greater than zero");
    }

    /**
     * Заправляет автомобиль указанным количеством топлива.
     *
     * @param amount количество литров для заправки (должно быть >= 0)
     * @throws IllegalArgumentException если amount < 0 или после заправки топливо превысит объём бака
     */
    public void refuel(float amount) {
        float newFuel = fuel + checkValue(amount, "Refuel amount cannot be negative");
        if (newFuel > tankCapacity) {
            throw new IllegalArgumentException(
                "Cannot refuel " + amount + "L - would exceed tank capacity. Current: " + fuel + "L, Capacity: " + tankCapacity + "L"
            );
        }
        this.fuel = newFuel;
    }

    /**
     * Увеличивает скорость на указанное значение.
     *
     * @param increase увеличение скорости в км/ч (должно быть > 0)
     * @throws IllegalArgumentException если новая скорость превысит максимальную
     */
    public void accelerate(float increase) {
        float newSpeed = speed + checkValue(increase, "Acceleration must be positive");
        if (newSpeed > maxSpeed) {
            throw new IllegalArgumentException(
                "Cannot accelerate to " + newSpeed + " km/h - exceeds maximum speed of " + maxSpeed + " km/h"
            );
        }
        this.speed = newSpeed;
    }

    /**
     * Уменьшает скорость на указанное значение (торможение).
     * Скорость не может быть отрицательной.
     *
     * @param decrease уменьшение скорости в км/ч (должно быть > 0)
     */
    public void decelerate(float decrease) {
        float newSpeed = speed - checkValue(decrease, "Deceleration must be positive");
        this.speed = Math.max(0f, newSpeed);
    }

    /**
     * Едет указанный промежуток времени.
     * Расход топлива рассчитывается как: consumption × speed × duration
     *
     * @param durationInHours время движения в секундах (должно быть > 0)
     * @throws IllegalArgumentException если duration <= 0 или недостаточно топлива
     */
    public void drive(float durationInHours) {
        checkValue(durationInHours, "Drive duration must be positive");
        checkValue(speed, "Cannot drive with speed <= 0 km/h");
        float fuelConsumption = consumption * speed * durationInHours;
        if (fuel < fuelConsumption) {
            throw new IllegalArgumentException(
                "Not enough fuel to drive for " + durationInHours + "s at speed " + speed +
                "km/h - need " + fuelConsumption + "L, have " + fuel + "L"
            );
        }
        this.fuel -= fuelConsumption;
    }


     /**
     * Проверяет что значение больше нуля.
     *
     * @param value проверяемое числовое значение (должно быть > 0)
     * @param errorMessage сообщение для исключения при ошибке валидации
     * @return проверенное значение
     * @throws IllegalArgumentException если value <= 0
     */
    private float checkValue(float value, String errorMessage) {
    if (value <= 0) {
            throw new IllegalArgumentException(errorMessage + ": " + value);
        }
        return value;
    }
}
