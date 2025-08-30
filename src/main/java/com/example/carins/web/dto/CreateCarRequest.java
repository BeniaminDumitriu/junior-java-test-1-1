package com.example.carins.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;

public class CreateCarRequest {
    
    @NotBlank(message = "VIN is required")
    @Size(min = 5, max = 32, message = "VIN must be between 5 and 32 characters")
    private String vin;
    
    private String make;
    
    private String model;
    
    @NotNull(message = "Year of manufacture is required")
    @Min(value = 1900, message = "Year of manufacture must be at least 1900")
    private Integer yearOfManufacture;
    
    @NotNull(message = "Owner ID is required")
    private Long ownerId;
    
    // Default constructor
    public CreateCarRequest() {}
    
    // Constructor with all fields
    public CreateCarRequest(String vin, String make, String model, Integer yearOfManufacture, Long ownerId) {
        this.vin = vin;
        this.make = make;
        this.model = model;
        this.yearOfManufacture = yearOfManufacture;
        this.ownerId = ownerId;
    }
    
    // Getters and Setters
    public String getVin() {
        return vin;
    }
    
    public void setVin(String vin) {
        this.vin = vin;
    }
    
    public String getMake() {
        return make;
    }
    
    public void setMake(String make) {
        this.make = make;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public Integer getYearOfManufacture() {
        return yearOfManufacture;
    }
    
    public void setYearOfManufacture(Integer yearOfManufacture) {
        this.yearOfManufacture = yearOfManufacture;
    }
    
    public Long getOwnerId() {
        return ownerId;
    }
    
    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}
