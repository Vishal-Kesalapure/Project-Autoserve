package com.autoserve.entity;

import java.util.Collection;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("SUPPLIER")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Mechanic extends User {

	@Column(name = "garage_name")
	private String garageName;

	@Column(name = "garage_address")
	private String garageAddress;

	@Column(name = "specializations")
	private String specializations;

	@Column(name = "certifications")
	private String certifications;

	// requirement uses "experience_yrs" as column name
	@Column(name = "experience_yrs")
	private Integer experienceYears;

	@Column(name = "rating")
	private Double rating = 0.0;

	@Column(name = "verified", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
	private boolean verified = false;

	@Override
	public Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities() {
		return List.of(() -> "ROLE_SUPPLIER");
	}
}