package com.autoserve.dto;



import com.autoserve.enums.ServiceType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateServiceRequest {

    @NotBlank(message = "Service name is required")
    private String serviceName;

    @NotNull(message = "Service type is required")
    private ServiceType serviceType;

    @Min(value = 1, message = "Estimated duration must be at least 1 hour")
    private Integer estimatedDurationHours;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.01", message = "Base price must be greater than zero")
    private BigDecimal basePrice;

    private String description;
}
