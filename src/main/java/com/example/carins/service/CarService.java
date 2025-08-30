package com.example.carins.service;

import com.example.carins.model.Car;
import com.example.carins.model.InsuranceClaim;
import com.example.carins.model.Owner;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.InsuranceClaimRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import com.example.carins.repo.OwnerRepository;
import com.example.carins.web.dto.ClaimDto;
import com.example.carins.web.dto.HistoryEventDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final InsurancePolicyRepository policyRepository;
    private final OwnerRepository ownerRepository;
    private final InsuranceClaimRepository claimRepository;

    public CarService(CarRepository carRepository, InsurancePolicyRepository policyRepository, OwnerRepository ownerRepository, InsuranceClaimRepository claimRepository) {
        this.carRepository = carRepository;
        this.policyRepository = policyRepository;
        this.ownerRepository = ownerRepository;
        this.claimRepository = claimRepository;
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

    public ClaimDto registerClaim(Long carId, LocalDate claimDate, String description, BigDecimal amount) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new EntityNotFoundException("Car " + carId + " not found"));
        InsuranceClaim claim = new InsuranceClaim(car, claimDate, description, amount);
        InsuranceClaim saved = claimRepository.save(claim);
        return new ClaimDto(saved.getId(), car.getId(), saved.getClaimDate(), saved.getDescription(), saved.getAmount());
    }

    public List<HistoryEventDto> getCarHistory(Long carId) {
        if (!carRepository.existsById(carId)) {
            throw new EntityNotFoundException("Car " + carId + " not found");
        }

        var policyEvents = policyRepository.findByCarId(carId).stream().flatMap(p -> {
            HistoryEventDto start = new HistoryEventDto("POLICY_START", p.getStartDate(), "Policy started", p.getProvider());
            HistoryEventDto end = new HistoryEventDto("POLICY_END", p.getEndDate(), "Policy ends", p.getProvider());
            return Stream.of(start, end);
        });

        var claimEvents = claimRepository.findByCarIdOrderByClaimDateAsc(carId).stream()
                .map(c -> new HistoryEventDto("CLAIM", c.getClaimDate(), "Claim registered", c.getDescription()));

        List<HistoryEventDto> all = new ArrayList<>();
        policyEvents.forEach(all::add);
        claimEvents.forEach(all::add);
        all.sort(Comparator.comparing(HistoryEventDto::date));
        return all;
    }
}
