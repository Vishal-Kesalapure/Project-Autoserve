package com.autoserve.dto;


import com.autoserve.entity.Garage;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class GarageResponse {

    private Long id;
    private Long mechanicId;
    private String mechanicName;
    private String garageName;
    private String garageAddress;
    private String specializations;
    private String certifications;
    private Integer experienceYears;
    private Double rating;
    private Integer totalReviews;
    private LocalTime openFrom;
    private LocalTime openTo;
    private String workingDays;
    private boolean active;
    private List<ServiceOfferingResponse> services;

    public static GarageResponse from(Garage g) {
        GarageResponse r = new GarageResponse();
        r.setId(g.getId());
        r.setMechanicId(g.getMechanicId());
        r.setMechanicName(g.getMechanicName());
        r.setGarageName(g.getGarageName());
        r.setGarageAddress(g.getGarageAddress());
        r.setSpecializations(g.getSpecializations());
        r.setCertifications(g.getCertifications());
        r.setExperienceYears(g.getExperienceYears());
        r.setRating(g.getRating());
        r.setTotalReviews(g.getTotalReviews());
        r.setOpenFrom(g.getOpenFrom());
        r.setOpenTo(g.getOpenTo());
        r.setWorkingDays(g.getWorkingDays());
        r.setActive(g.isActive());
        r.setServices(
            g.getServices().stream()
             .map(ServiceOfferingResponse::from)
             .collect(Collectors.toList())
        );
        return r;
    }
}

