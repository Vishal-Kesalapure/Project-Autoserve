package com.autoserve.dto;

import lombok.Data;

@Data
public class RegisterGarageRequest {
	private Long mechanicId;
	private String mechanicName;
	private String garageName;
	private String garageAddress;
	private String specializations;
	private String certifications;
	private Integer experienceYears;
}