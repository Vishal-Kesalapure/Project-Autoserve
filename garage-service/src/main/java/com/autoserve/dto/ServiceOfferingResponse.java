package com.autoserve.dto;



import com.autoserve.entity.ServiceOffering;
import com.autoserve.enums.ServiceType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServiceOfferingResponse {

    private Long id;
    private Long garageId;
    private String serviceName;
    private ServiceType serviceType;
    private Integer estimatedDurationHours;
    private BigDecimal basePrice;
    private String description;

    public static ServiceOfferingResponse from(ServiceOffering s) {
        ServiceOfferingResponse r = new ServiceOfferingResponse();
        r.setId(s.getId());
        r.setGarageId(s.getGarage().getId());
        r.setServiceName(s.getServiceName());
        r.setServiceType(s.getServiceType());
        r.setEstimatedDurationHours(s.getEstimatedDurationHours());
        r.setBasePrice(s.getBasePrice());
        r.setDescription(s.getDescription());
        return r;
    }
}
