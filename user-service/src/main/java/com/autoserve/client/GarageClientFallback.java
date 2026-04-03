package com.autoserve.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.autoserve.dto.RegisterGarageRequest;

@Component
public class GarageClientFallback implements GarageClient {

	private static final Logger log = LoggerFactory.getLogger(GarageClientFallback.class);

	@Override
	public void registerGarage(String internalCall, RegisterGarageRequest request) {
		log.warn("[FALLBACK] garage-service unavailable - garage profile NOT created for mechanic '{}'.",
				request.getMechanicId());
		throw new RuntimeException(
				"garage-service unavailable: garage profile was not created for mechanic " + request.getMechanicId());
	}

	@Override
	public void deactivateGarage(String role, Long mechanicId) {
		log.error("[FALLBACK] garage-service unavailable - garage for mechanic {} could NOT be deactivated.",
				mechanicId);
		throw new RuntimeException(
				"garage-service unavailable: garage for mechanic " + mechanicId + " was not deactivated");
	}

	@Override
	public void activateGarage(String role, Long mechanicId) {
		log.error("[FALLBACK] garage-service unavailable - garage for mechanic {} could NOT be activated.", mechanicId);
		throw new RuntimeException(
				"garage-service unavailable: garage for mechanic " + mechanicId + " was not activated");
	}
    @Override
    public void deleteGarageByMechanic(String role, Long mechanicId) {
        log.warn("[FALLBACK] garage-service unavailable - garage record for mechanic {} was NOT deleted. Manual cleanup may be required.", mechanicId);
        throw new RuntimeException(
                "garage-service unavailable: garage for mechanic " + mechanicId + " was not deleted");
    }
    

}



