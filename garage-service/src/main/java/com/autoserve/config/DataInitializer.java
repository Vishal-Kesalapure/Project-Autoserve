package com.autoserve.config;



import com.autoserve.entity.Garage;
import com.autoserve.entity.ServiceOffering;
import com.autoserve.enums.ServiceType;
import com.autoserve.repository.GarageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final GarageRepository garageRepository;

    @Override
    public void run(String... args) {
        if (garageRepository.count() > 0) {
            log.info("DataInitializer: garages already seeded — skipping.");
            return;
        }

        log.info("DataInitializer: seeding garages and service offerings...");

        // ── Garage 1 — Sharma Auto Works (mechanicId = 2) ──────────────────
        Garage g1 = buildGarage(
                2L, "Raju Sharma", "Sharma Auto Works",
                "12 Service Road, Kothrud, Pune",
                "Engine,Brakes,AC", "ASE,ISO9001", 12,
                4.5, 38,
                LocalTime.of(8, 0), LocalTime.of(20, 0),
                "MON,TUE,WED,THU,FRI,SAT", true);
        g1.getServices().addAll(List.of(
                buildService(g1, "Full Service",          ServiceType.MAINTENANCE, 3, "2500.00", "Complete engine and fluid check with oil change"),
                buildService(g1, "AC Gas Refill",         ServiceType.MAINTENANCE, 1, "1200.00", "AC gas recharge with leak detection check"),
                buildService(g1, "Brake Pad Replacement", ServiceType.REPAIR,      2, "1800.00", "Front and rear brake pad replacement with rotor inspection"),
                buildService(g1, "Engine Diagnostics",    ServiceType.INSPECTION,  1, "600.00",  "OBD2 scan and full engine health report")
        ));
        garageRepository.save(g1);

        // ── Garage 2 — Patil Motors & Garage (mechanicId = 3) ───────────────
        Garage g2 = buildGarage(
                3L, "Vikram Patil", "Patil Motors & Garage",
                "45 Nagar Road, Hadapsar, Pune",
                "Engine,Transmission,Suspension", "ASE", 8,
                4.2, 21,
                LocalTime.of(9, 0), LocalTime.of(19, 0),
                "MON,TUE,WED,THU,FRI,SAT", true);
        g2.getServices().addAll(List.of(
                buildService(g2, "Oil Change",              ServiceType.MAINTENANCE, 1, "800.00",  "Engine oil and oil filter replacement"),
                buildService(g2, "Transmission Service",    ServiceType.MAINTENANCE, 4, "3500.00", "Transmission fluid flush and gear inspection"),
                buildService(g2, "Suspension Overhaul",     ServiceType.REPAIR,      5, "4500.00", "Full suspension inspection and worn parts replacement"),
                buildService(g2, "Pre-Purchase Inspection", ServiceType.INSPECTION,  2, "1000.00", "Thorough inspection report before buying a used car")
        ));
        garageRepository.save(g2);

        // ── Garage 3 — Nair Auto Care (mechanicId = 4) ──────────────────────
        Garage g3 = buildGarage(
                4L, "Suresh Nair", "Nair Auto Care",
                "7 MG Road, Shivajinagar, Pune",
                "AC,Electrical,Brakes", "ISO9001", 15,
                4.8, 62,
                LocalTime.of(8, 30), LocalTime.of(20, 30),
                "MON,TUE,WED,THU,FRI,SAT,SUN", true);
        g3.getServices().addAll(List.of(
                buildService(g3, "AC Full Service",       ServiceType.MAINTENANCE, 2, "2000.00", "AC cleaning, gas refill, filter replacement and performance test"),
                buildService(g3, "Electrical Diagnosis",  ServiceType.INSPECTION,  2, "700.00",  "Battery, alternator, wiring and ECU check"),
                buildService(g3, "Brake Fluid Flush",     ServiceType.MAINTENANCE, 1, "900.00",  "Complete brake fluid replacement for safe stopping"),
                buildService(g3, "Headlight Restoration", ServiceType.REPAIR,      1, "500.00",  "Polishing and UV coating of faded headlights")
        ));
        garageRepository.save(g3);

        // ── Garage 4 — Deshmukh Garage (mechanicId = 5) ─────────────────────
        Garage g4 = buildGarage(
                5L, "Amol Deshmukh", "Deshmukh Garage",
                "23 Warje Road, Warje, Pune",
                "Engine,Oil Change,Tyres", "ASE", 6,
                3.9, 14,
                LocalTime.of(9, 0), LocalTime.of(18, 0),
                "MON,TUE,WED,THU,FRI", true);
        g4.getServices().addAll(List.of(
                buildService(g4, "Oil & Filter Change",   ServiceType.MAINTENANCE, 1, "750.00",  "Synthetic oil and OEM filter replacement"),
                buildService(g4, "Tyre Rotation",         ServiceType.MAINTENANCE, 1, "400.00",  "Rotate all four tyres for even wear"),
                buildService(g4, "Wheel Balancing",       ServiceType.MAINTENANCE, 1, "500.00",  "Dynamic wheel balancing for smooth ride"),
                buildService(g4, "Exterior Wash & Clean", ServiceType.CLEANING,    1, "300.00",  "Full exterior hand wash with foam treatment")
        ));
        garageRepository.save(g4);

        // ── Garage 5 — Joshi Quick Fix Center (mechanicId = 6) ──────────────
        Garage g5 = buildGarage(
                6L, "Rahul Joshi", "Joshi Quick Fix Center",
                "88 FC Road, Deccan Gymkhana, Pune",
                "Brakes,Suspension,Alignment", "ISO9001,ASE", 10,
                4.6, 45,
                LocalTime.of(8, 0), LocalTime.of(21, 0),
                "MON,TUE,WED,THU,FRI,SAT,SUN", true);
        g5.getServices().addAll(List.of(
                buildService(g5, "Wheel Alignment",         ServiceType.MAINTENANCE, 1, "700.00",  "4-wheel computerised alignment check and adjustment"),
                buildService(g5, "Brake Inspection",        ServiceType.INSPECTION,  1, "500.00",  "Full brake system check — pads, rotors, callipers"),
                buildService(g5, "Shock Absorber Replace",  ServiceType.REPAIR,      3, "3200.00", "Replace worn shock absorbers on all four wheels"),
                buildService(g5, "Steering Check",          ServiceType.INSPECTION,  1, "450.00",  "Power steering fluid and rack & pinion inspection")
        ));
        garageRepository.save(g5);

        // ── Garage 6 — Kulkarni Auto Service (mechanicId = 7) ───────────────
        Garage g6 = buildGarage(
                7L, "Deepak Kulkarni", "Kulkarni Auto Service",
                "3 Baner Road, Baner, Pune",
                "Engine,AC,Electrical,Tyres", "ASE", 20,
                4.9, 104,
                LocalTime.of(7, 30), LocalTime.of(19, 30),
                "MON,TUE,WED,THU,FRI,SAT", true);
        g6.getServices().addAll(List.of(
                buildService(g6, "Premium Full Service",  ServiceType.MAINTENANCE, 4, "3500.00", "Oil, filters, spark plugs, brakes, AC and full diagnostics"),
                buildService(g6, "AC Deep Clean",         ServiceType.CLEANING,    2, "1500.00", "Evaporator foam clean, cabin filter change, gas top-up"),
                buildService(g6, "Engine Carbon Clean",   ServiceType.MAINTENANCE, 3, "2800.00", "Hydrogen carbon cleaning for improved fuel efficiency"),
                buildService(g6, "Tyre Replacement",      ServiceType.REPAIR,      2, "600.00",  "Tyre removal, new tyre fitting and balancing (per tyre)")
        ));
        garageRepository.save(g6);

        // ── Garage 7 — Gharge EV & Multi-Brand (mechanicId = 8) ─────────────
        Garage g7 = buildGarage(
                8L, "Nitin Gharge", "Gharge EV & Multi-Brand",
                "56 Hinjewadi Phase 1, Hinjewadi, Pune",
                "Electric,Engine,Brakes", "EV-Certified,ASE", 5,
                4.3, 9,
                LocalTime.of(10, 0), LocalTime.of(20, 0),
                "MON,TUE,WED,THU,FRI,SAT", true);
        g7.getServices().addAll(List.of(
                buildService(g7, "EV Battery Health Check", ServiceType.INSPECTION,  2, "1200.00", "State of charge, degradation and cell balance report"),
                buildService(g7, "EV Brake Service",        ServiceType.MAINTENANCE, 2, "1500.00", "Regenerative brake system check and pad inspection"),
                buildService(g7, "Software Update",         ServiceType.MAINTENANCE, 1, "800.00",  "OTA and dealer-level ECU firmware update"),
                buildService(g7, "General Inspection",      ServiceType.INSPECTION,  2, "900.00",  "Full multi-point vehicle health inspection report")
        ));
        garageRepository.save(g7);

        // ── Garage 8 — More Brothers Garage (mechanicId = 9, INACTIVE) ──────
        Garage g8 = buildGarage(
                9L, "Santosh More", "More Brothers Garage",
                "19 Katraj Road, Katraj, Pune",
                "Engine,Transmission,Brakes", "ISO9001", 9,
                4.1, 27,
                LocalTime.of(9, 0), LocalTime.of(19, 0),
                "MON,TUE,WED,THU,FRI,SAT", false);   // ← inactive
        g8.getServices().addAll(List.of(
                buildService(g8, "Basic Service",      ServiceType.MAINTENANCE, 2, "1200.00", "Oil change, air filter, basic checks"),
                buildService(g8, "Brake Replacement",  ServiceType.REPAIR,      2, "1600.00", "Front brake pad and disc replacement")
        ));
        garageRepository.save(g8);

        log.info("DataInitializer: seeded 8 garages with 30 service offerings.");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Builder helpers
    // ─────────────────────────────────────────────────────────────────────────

    private Garage buildGarage(Long mechanicId, String mechanicName, String garageName,
                                String garageAddress, String specializations, String certifications,
                                int experienceYears, double rating, int totalReviews,
                                LocalTime openFrom, LocalTime openTo,
                                String workingDays, boolean active) {
        return Garage.builder()
                .mechanicId(mechanicId)
                .mechanicName(mechanicName)
                .garageName(garageName)
                .garageAddress(garageAddress)
                .specializations(specializations)
                .certifications(certifications)
                .experienceYears(experienceYears)
                .rating(rating)
                .totalReviews(totalReviews)
                .openFrom(openFrom)
                .openTo(openTo)
                .workingDays(workingDays)
                .active(active)
                .build();
    }

    private ServiceOffering buildService(Garage garage, String serviceName,
                                          ServiceType serviceType, int durationHours,
                                          String basePrice, String description) {
        return ServiceOffering.builder()
                .garage(garage)
                .serviceName(serviceName)
                .serviceType(serviceType)
                .estimatedDurationHours(durationHours)
                .basePrice(new BigDecimal(basePrice))
                .description(description)
                .build();
    }
}
