package com.autoserve.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.autoserve.client.GarageClient;
import com.autoserve.dto.RegisterGarageRequest;
import com.autoserve.entity.Admin;
import com.autoserve.entity.Mechanic;
import com.autoserve.entity.User;
import com.autoserve.entity.VehicleOwner;
import com.autoserve.repository.AdminRepository;
import com.autoserve.repository.MechanicRepository;
import com.autoserve.repository.UserRepository;
import com.autoserve.repository.VehicleOwnerRepository;

import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private static final String INTERNAL_ADMIN_ROLE = "ROLE_ADMIN";

    private final UserRepository userRepository;
    private final VehicleOwnerRepository vehicleOwnerRepository;
    private final MechanicRepository mechanicRepository;
    private final AdminRepository adminRepository;
    private final GarageClient garageClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
    }

    public VehicleOwner saveOwner(VehicleOwner owner) {
        return vehicleOwnerRepository.save(owner);
    }

    public List<VehicleOwner> findAllOwners() {
        return vehicleOwnerRepository.findAll();
    }

    public VehicleOwner findOwnerById(Long id) {
        return vehicleOwnerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle owner not found: " + id));
    }

    public Optional<VehicleOwner> findOwnerByUsername(String username) {
        return vehicleOwnerRepository.findByUsername(username);
    }

    public VehicleOwner disableOwner(Long id) {
        VehicleOwner owner = findOwnerById(id);
        owner.setAccountEnabled(false);
        return vehicleOwnerRepository.save(owner);
    }

    public VehicleOwner enableOwner(Long id) {
        VehicleOwner owner = findOwnerById(id);
        owner.setAccountEnabled(true);
        return vehicleOwnerRepository.save(owner);
    }

    public void deleteOwner(Long id) {
        if (!vehicleOwnerRepository.existsById(id)) {
            throw new EntityNotFoundException("Vehicle owner not found: " + id);
        }
        vehicleOwnerRepository.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public Mechanic saveMechanic(Mechanic mechanic) {
        Mechanic saved = mechanicRepository.save(mechanic);
        RegisterGarageRequest garageReq = buildGarageRequest(saved);

        try {
            garageClient.registerGarage("true", garageReq);
            garageClient.deactivateGarage(INTERNAL_ADMIN_ROLE, saved.getId());
        } catch (Exception e) {
            log.error("Failed to create garage profile for mechanic {}: {}", saved.getId(), e.getMessage());
            throw new IllegalStateException("Could not create garage profile. Please try again.", e);
        }

        return saved;
    }

    public void ensureGarageProfile(Mechanic mechanic) {
        RegisterGarageRequest garageReq = buildGarageRequest(mechanic);
        try {
            garageClient.registerGarage("true", garageReq);
            log.info("Garage profile created for mechanic {} during ensure flow.", mechanic.getId());
        } catch (Exception e) {
            if (isGarageAlreadyExistsError(e)) {
                log.debug("Garage already exists for mechanic {}.", mechanic.getId());
                return;
            }
            throw new IllegalStateException("Could not ensure garage profile for mechanic " + mechanic.getId(), e);
        }
    }

    public List<Mechanic> findAllMechanics() {
        return mechanicRepository.findAll();
    }

    public Mechanic findMechanicById(Long id) {
        return mechanicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mechanic not found: " + id));
    }

    public Optional<Mechanic> findMechanicByUsername(String username) {
        return mechanicRepository.findByUsername(username);
    }

    public Mechanic verifyMechanic(Long id) {
        Mechanic mechanic = findMechanicById(id);
        if (mechanic.isVerified()) {
            throw new IllegalStateException("Mechanic " + id + " is already verified");
        }
        mechanic.setVerified(true);
        Mechanic saved = mechanicRepository.save(mechanic);
        garageClient.activateGarage(INTERNAL_ADMIN_ROLE, saved.getId());
        return saved;
    }

    @Transactional(rollbackFor = Exception.class)
    public Mechanic disableMechanic(Long id) {
        Mechanic mechanic = findMechanicById(id);
        mechanic.setAccountEnabled(false);
        Mechanic saved = mechanicRepository.save(mechanic);

        garageClient.deactivateGarage(INTERNAL_ADMIN_ROLE, saved.getId());
        return saved;
    }

    @Transactional(rollbackFor = Exception.class)
    public Mechanic enableMechanic(Long id) {
        Mechanic mechanic = findMechanicById(id);
        mechanic.setAccountEnabled(true);
        Mechanic saved = mechanicRepository.save(mechanic);
        garageClient.activateGarage(INTERNAL_ADMIN_ROLE, saved.getId());
        return saved;
    }

    public void deleteMechanic(Long id) {
        if (!mechanicRepository.existsById(id)) {
            throw new EntityNotFoundException("Mechanic not found: " + id);
        }

        try {
            garageClient.deleteGarageByMechanic(INTERNAL_ADMIN_ROLE, id);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Could not delete mechanic because garage cleanup failed for mechanic id: " + id, e);
        }
        mechanicRepository.deleteById(id);
    }

    public Admin saveAdmin(Admin admin) {
        return adminRepository.save(admin);
    }

    public List<Admin> findAllAdmins() {
        return adminRepository.findAll();
    }

    private RegisterGarageRequest buildGarageRequest(Mechanic mechanic) {
        RegisterGarageRequest garageReq = new RegisterGarageRequest();
        garageReq.setMechanicId(mechanic.getId());
        garageReq.setMechanicName((mechanic.getFirstName() + " " + mechanic.getLastName()).trim());
        garageReq.setGarageName(mechanic.getGarageName());
        garageReq.setGarageAddress(mechanic.getGarageAddress());
        garageReq.setSpecializations(mechanic.getSpecializations());
        garageReq.setCertifications(mechanic.getCertifications());
        garageReq.setExperienceYears(mechanic.getExperienceYears());
        return garageReq;
    }

    private boolean isGarageAlreadyExistsError(Exception e) {
        if (e instanceof FeignException fe) {
            if (fe.status() == 400 || fe.status() == 409) {
                String body = fe.contentUTF8();
                String msg = (body == null || body.isBlank()) ? fe.getMessage() : body;
                return msg != null && msg.toLowerCase().contains("already exists");
            }
        }
        String msg = e.getMessage();
        return msg != null && msg.toLowerCase().contains("already exists");
    }
}

