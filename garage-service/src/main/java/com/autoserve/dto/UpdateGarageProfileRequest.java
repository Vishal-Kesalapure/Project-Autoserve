package com.autoserve.dto;



import lombok.Data;

@Data
public class UpdateGarageProfileRequest {

    private String garageName;
    private String garageAddress;
    private String specializations;
    private String certifications;
    private Integer experienceYears;
}

