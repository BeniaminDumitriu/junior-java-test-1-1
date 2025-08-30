package com.example.carins.service;

import com.example.carins.model.Car;
import com.example.carins.model.Owner;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import com.example.carins.repo.OwnerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final InsurancePolicyRepository policyRepository;
    private final OwnerRepository ownerRepository;

    public CarService(CarRepository carRepository, InsurancePolicyRepository policyRepository, OwnerRepository ownerRepository) {
        this.carRepository = carRepository;
        this.policyRepository = policyRepository;
        this.ownerRepository = ownerRepository;
    }

    public List<Car> listCars() {
        return carRepository.findAll();
    }

    public boolean isInsuranceValid(Long carId, LocalDate date) {
        if (carId == null || date == null) return false;
        // TODO: optionally throw NotFound if car does not exist
        return policyRepository.existsActiveOnDate(carId, date);
    }
    
    public Car createCar(String vin, String make, String model, Integer yearOfManufacture, Long ownerId) {
        // Validate owner exists
        Owner owner = ownerRepository.findById(ownerId)
            .orElseThrow(() -> new IllegalArgumentException("Owner with ID " + ownerId + " not found"));
        
        // Create new car - convert Integer to int
        Car car = new Car(vin, make, model, yearOfManufacture.intValue(), owner);
        
        // Save and return
        return carRepository.save(car);
    }
}
