package com.mch.unicoursehub.model.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record SemesterResponse(

        String name,
        LocalDate startDate,
        LocalDate endDate,
        int minUnits,
        int maxUnits
) {}

