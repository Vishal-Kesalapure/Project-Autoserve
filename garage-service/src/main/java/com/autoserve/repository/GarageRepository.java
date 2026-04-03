package com.autoserve.repository;



import com.autoserve.entity.Garage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GarageRepository extends JpaRepository<Garage, Long> {

    Optional<Garage> findByMechanicId(Long mechanicId);

    List<Garage> findByActiveTrue();

    List<Garage> findByGarageNameContainingIgnoreCaseAndActiveTrue(String name);

    List<Garage> findByGarageAddressContainingIgnoreCaseAndActiveTrue(String address);

    List<Garage> findBySpecializationsContainingIgnoreCaseAndActiveTrue(String specialization);
}

