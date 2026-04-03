package com.autoserve.dto;

import com.autoserve.entity.VehicleOwner;

import lombok.Data;

@Data
public class VehicleOwnerResponse {
	private Long id;
	private String username;
	private String firstName;
	private String lastName;
	private String mobileNumber;
	private String vehicleNumber;
	private String vehicleMake;
	private String vehicleModel;
	private Integer vehicleYear;
	private String fuelType;
	private boolean accountEnabled;

	public static VehicleOwnerResponse from(VehicleOwner o) {
		VehicleOwnerResponse r = new VehicleOwnerResponse();
		r.setId(o.getId());
		r.setUsername(o.getUsername());
		r.setFirstName(o.getFirstName());
		r.setLastName(o.getLastName());
		r.setMobileNumber(o.getMobileNumber());
		r.setVehicleNumber(o.getVehicleNumber());
		r.setVehicleMake(o.getVehicleMake());
		r.setVehicleModel(o.getVehicleModel());
		r.setVehicleYear(o.getVehicleYear());
		r.setFuelType(o.getFuelType() != null ? o.getFuelType().name() : null);
		r.setAccountEnabled(o.isAccountEnabled());
		return r;
	}
}