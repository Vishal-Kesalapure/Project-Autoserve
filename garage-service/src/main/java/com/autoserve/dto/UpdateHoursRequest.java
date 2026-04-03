package com.autoserve.dto;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class UpdateHoursRequest {

    @NotNull(message = "openFrom is required")
    private LocalTime openFrom;

    @NotNull(message = "openTo is required")
    private LocalTime openTo;

    @NotBlank(message = "workingDays is required")
    private String workingDays;
}

