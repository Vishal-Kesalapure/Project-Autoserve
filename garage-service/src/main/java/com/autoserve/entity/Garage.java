package com.autoserve.entity;



import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "garages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Garage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Links back to user-service Mechanic id */
    @Column(name = "mechanic_id", nullable = false, unique = true)
    private Long mechanicId;

    @Column(name = "mechanic_name", nullable = false)
    private String mechanicName;

    @Column(name = "garage_name", nullable = false)
    private String garageName;

    @Column(name = "garage_address", nullable = false)
    private String garageAddress;

    /** Comma-separated: e.g. "Engine,Brakes,AC" */
    @Column(name = "specializations")
    private String specializations;

    /** Comma-separated: e.g. "ASE,ISO9001" */
    @Column(name = "certifications")
    private String certifications;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column(name = "rating", columnDefinition = "DOUBLE DEFAULT 0.0")
    private Double rating = 0.0;

    @Column(name = "total_reviews", columnDefinition = "INT DEFAULT 0")
    private Integer totalReviews = 0;

    @Column(name = "open_from")
    private LocalTime openFrom;

    @Column(name = "open_to")
    private LocalTime openTo;

    /** Comma-separated: e.g. "MON,TUE,WED,THU,FRI,SAT" */
    @Column(name = "working_days")
    private String workingDays;

    @Column(name = "active", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
    private boolean active = true;

    @OneToMany(mappedBy = "garage", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ServiceOffering> services = new ArrayList<>();
}

