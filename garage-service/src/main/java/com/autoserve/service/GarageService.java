package com.autoserve.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.autoserve.dto.CreateServiceRequest;
import com.autoserve.dto.GarageResponse;
import com.autoserve.dto.RegisterGarageRequest;
import com.autoserve.dto.ServiceOfferingResponse;
import com.autoserve.dto.UpdateGarageProfileRequest;
import com.autoserve.dto.UpdateHoursRequest;
import com.autoserve.entity.Garage;
import com.autoserve.entity.ServiceOffering;
import com.autoserve.repository.GarageRepository;
import com.autoserve.repository.ServiceOfferingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GarageService {

    private final GarageRepository garageRepository;
    private final ServiceOfferingRepository serviceOfferingRepository;

    @Transactional
    public GarageResponse registerGarage(RegisterGarageRequest request) {
        if (garageRepository.findByMechanicId(request.getMechanicId()).isPresent()) {
            throw new RuntimeException("Garage already exists for mechanic id: " + request.getMechanicId());
        }
        Garage garage = Garage.builder()
                .mechanicId(request.getMechanicId())
                .mechanicName(request.getMechanicName())
                .garageName(request.getGarageName())
                .garageAddress(request.getGarageAddress())
                .specializations(request.getSpecializations())
                .certifications(request.getCertifications())
                .experienceYears(request.getExperienceYears())
                .rating(0.0)
                .totalReviews(0)
                .active(true)
                .services(new ArrayList<>())
                .build();
        return GarageResponse.from(garageRepository.save(garage));
    }

    @Transactional(readOnly = true)
    public List<GarageResponse> getAllActiveGarages() {
        return garageRepository.findByActiveTrue().stream().map(GarageResponse::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GarageResponse getGarageById(Long id) {
        Garage garage = garageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Garage not found with id: " + id));
        return GarageResponse.from(garage);
    }

    @Transactional(readOnly = true)
    public GarageResponse getGarageByIdInternal(Long id) {
        Garage garage = garageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Garage not found with id: " + id));
        return GarageResponse.from(garage);
    }

    @Transactional(readOnly = true)
    public ServiceOfferingResponse getServiceOfferingById(Long serviceId) {
        ServiceOffering offering = serviceOfferingRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service offering not found with id: " + serviceId));
        return ServiceOfferingResponse.from(offering);
    }

    @Transactional(readOnly = true)
    public List<GarageResponse> searchGarages(String query) {
        if (query == null || query.isBlank()) return getAllActiveGarages();

        List<Garage> byName = garageRepository.findByGarageNameContainingIgnoreCaseAndActiveTrue(query);
        List<Garage> byAddress = garageRepository.findByGarageAddressContainingIgnoreCaseAndActiveTrue(query);

        Map<Long, Garage> merged = new LinkedHashMap<>();
        byName.forEach(g -> merged.put(g.getId(), g));
        byAddress.forEach(g -> merged.putIfAbsent(g.getId(), g));

        return merged.values().stream().map(GarageResponse::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GarageResponse> getBySpecialization(String specialization) {
        return garageRepository.findBySpecializationsContainingIgnoreCaseAndActiveTrue(specialization)
                .stream().map(GarageResponse::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GarageResponse> getAllGaragesForAdmin() {
        return garageRepository.findAll().stream().map(GarageResponse::from).collect(Collectors.toList());
    }

    @Transactional
    public void deactivateGarageByMechanicId(Long mechanicId) {
        Garage garage = garageRepository.findByMechanicId(mechanicId)
                .orElseThrow(() -> new RuntimeException("Garage not found for mechanic id: " + mechanicId));
        garage.setActive(false);
        garageRepository.save(garage);
    }

    @Transactional
    public void activateGarageByMechanicId(Long mechanicId) {
        Garage garage = garageRepository.findByMechanicId(mechanicId)
                .orElseThrow(() -> new RuntimeException("Garage not found for mechanic id: " + mechanicId));
        garage.setActive(true);
        garageRepository.save(garage);
    }

    @Transactional
    public void deleteGarageByMechanicId(Long mechanicId) {
        Garage garage = garageRepository.findByMechanicId(mechanicId)
                .orElseThrow(() -> new RuntimeException("Garage not found for mechanic id: " + mechanicId));
        garageRepository.delete(garage);
    }

    @Transactional(readOnly = true)
    public GarageResponse getMyGarage(Long mechanicId) {
        return GarageResponse.from(getOwnGarageOrThrow(mechanicId));
    }

    @Transactional
    public GarageResponse updateProfile(Long mechanicId, UpdateGarageProfileRequest request) {
        Garage garage = getOwnGarageOrThrow(mechanicId);

        if (request.getGarageName() != null && !request.getGarageName().isBlank()) garage.setGarageName(request.getGarageName());
        if (request.getGarageAddress() != null && !request.getGarageAddress().isBlank()) garage.setGarageAddress(request.getGarageAddress());
        if (request.getSpecializations() != null) garage.setSpecializations(request.getSpecializations());
        if (request.getCertifications() != null) garage.setCertifications(request.getCertifications());
        if (request.getExperienceYears() != null) garage.setExperienceYears(request.getExperienceYears());

        return GarageResponse.from(garageRepository.save(garage));
    }

    @Transactional
    public GarageResponse updateHours(Long mechanicId, UpdateHoursRequest request) {
        Garage garage = getOwnGarageOrThrow(mechanicId);

        if (request.getOpenFrom() != null && request.getOpenTo() != null
                && !request.getOpenFrom().isBefore(request.getOpenTo())) {
            throw new RuntimeException("openFrom must be earlier than openTo");
        }

        garage.setOpenFrom(request.getOpenFrom());
        garage.setOpenTo(request.getOpenTo());
        garage.setWorkingDays(request.getWorkingDays());

        return GarageResponse.from(garageRepository.save(garage));
    }

    @Transactional
    public ServiceOfferingResponse addService(Long mechanicId, CreateServiceRequest request) {
        Garage garage = getOwnGarageOrThrow(mechanicId);

        ServiceOffering offering = ServiceOffering.builder()
                .garage(garage)
                .serviceName(request.getServiceName())
                .serviceType(request.getServiceType())
                .estimatedDurationHours(request.getEstimatedDurationHours())
                .basePrice(request.getBasePrice())
                .description(request.getDescription())
                .build();

        return ServiceOfferingResponse.from(serviceOfferingRepository.save(offering));
    }

    @Transactional
    public ServiceOfferingResponse updateService(Long mechanicId, Long serviceId, CreateServiceRequest request) {
        Garage garage = getOwnGarageOrThrow(mechanicId);

        ServiceOffering offering = serviceOfferingRepository.findByIdAndGarageId(serviceId, garage.getId())
                .orElseThrow(() -> new RuntimeException("Service offering not found or does not belong to your garage"));

        if (request.getServiceName() != null && !request.getServiceName().isBlank()) offering.setServiceName(request.getServiceName());
        if (request.getServiceType() != null) offering.setServiceType(request.getServiceType());
        if (request.getEstimatedDurationHours() != null) offering.setEstimatedDurationHours(request.getEstimatedDurationHours());
        if (request.getBasePrice() != null) offering.setBasePrice(request.getBasePrice());
        if (request.getDescription() != null) offering.setDescription(request.getDescription());

        return ServiceOfferingResponse.from(serviceOfferingRepository.save(offering));
    }

    @Transactional
    public void deleteService(Long mechanicId, Long serviceId) {
        Garage garage = getOwnGarageOrThrow(mechanicId);

        ServiceOffering offering = serviceOfferingRepository.findByIdAndGarageId(serviceId, garage.getId())
                .orElseThrow(() -> new RuntimeException("Service offering not found or does not belong to your garage"));

        serviceOfferingRepository.delete(offering);
    }

    private Garage getOwnGarageOrThrow(Long mechanicId) {
        return garageRepository.findByMechanicId(mechanicId)
                .orElseThrow(() -> new RuntimeException("No garage profile found for mechanic id: " + mechanicId));
    }
}