package com.autoserve.dto;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Sent internally (or by the mechanic registration flow) to create the
 * initial garage profile in garage-service.
 */
@Data
public class RegisterGarageRequest {

    @NotNull(message = "mechanicId is required")
    private Long mechanicId;

    @NotBlank(message = "mechanicName is required")
    private String mechanicName;

    @NotBlank(message = "garageName is required")
    private String garageName;

    @NotBlank(message = "garageAddress is required")
    private String garageAddress;

    private String specializations;
    private String certifications;
    private Integer experienceYears;
}
