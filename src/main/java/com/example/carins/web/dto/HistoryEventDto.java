package com.example.carins.web.dto;

import java.time.LocalDate;

public record HistoryEventDto(String type, LocalDate date, String title, String details) {}


