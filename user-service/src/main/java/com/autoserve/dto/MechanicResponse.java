package com.autoserve.dto;

import com.autoserve.entity.Mechanic;

import lombok.Data;

@Data
public class MechanicResponse {
	private Long id;
	private String username;
	private String firstName;
	private String lastName;
	private String mobileNumber;
	private String garageName;
	private String garageAddress;
	private String specializations;
	private String certifications;
	private Integer experienceYears;
	private Double rating;
	private boolean verified;
	private boolean accountEnabled;

	public static MechanicResponse from(Mechanic m) {
		MechanicResponse r = new MechanicResponse();
		r.setId(m.getId());
		r.setUsername(m.getUsername());
		r.setFirstName(m.getFirstName());
		r.setLastName(m.getLastName());
		r.setMobileNumber(m.getMobileNumber());
		r.setGarageName(m.getGarageName());
		r.setGarageAddress(m.getGarageAddress());
		r.setSpecializations(m.getSpecializations());
		r.setCertifications(m.getCertifications());
		r.setExperienceYears(m.getExperienceYears());
		r.setRating(m.getRating());
		r.setVerified(m.isVerified());
		r.setAccountEnabled(m.isAccountEnabled());
		return r;
	}
}