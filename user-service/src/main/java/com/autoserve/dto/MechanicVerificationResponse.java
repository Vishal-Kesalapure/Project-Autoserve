package com.autoserve.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MechanicVerificationResponse {
    private Long mechanicId;
    private boolean verified;
    private boolean accountEnabled;
}
