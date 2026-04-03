package com.autoserve.entity;

import java.util.Collection;
import java.util.List;

import com.autoserve.enums.FuelType;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("CUSTOMER")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class VehicleOwner extends User {

	@Column(name = "vehicle_number")
	private String vehicleNumber;

	@Column(name = "vehicle_make")
	private String vehicleMake;

	@Column(name = "vehicle_model")
	private String vehicleModel;

	@Column(name = "vehicle_year")
	private Integer vehicleYear;

	@Enumerated(EnumType.STRING)
	@Column(name = "fuel_type")
	private FuelType fuelType;

	@Override
	public Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities() {
		return List.of(() -> "ROLE_CUSTOMER");
	}
}
