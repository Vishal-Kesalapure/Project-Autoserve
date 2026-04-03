package com.autoserve.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.autoserve.dto.RegisterGarageRequest;

@FeignClient(name = "garage-service", fallback = GarageClientFallback.class)
public interface GarageClient {

	@PostMapping("/api/garages/register")
	void registerGarage(@RequestHeader("X-Internal-Call") String internalCall,
			@RequestBody RegisterGarageRequest request);

	@PutMapping("/api/garages/admin/mechanic/{mechanicId}/deactivate")
	void deactivateGarage(@RequestHeader("X-User-Role") String role,
			@PathVariable("mechanicId") Long mechanicId);

	@PutMapping("/api/garages/admin/mechanic/{mechanicId}/activate")
	void activateGarage(@RequestHeader("X-User-Role") String role,
			@PathVariable("mechanicId") Long mechanicId);

	@DeleteMapping("/api/garages/admin/mechanic/{mechanicId}")
	void deleteGarageByMechanic(@RequestHeader("X-User-Role") String role,
			@PathVariable("mechanicId") Long mechanicId);
	
}

