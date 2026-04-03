package com.autoserve.repository;



import com.autoserve.entity.ServiceOffering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceOfferingRepository extends JpaRepository<ServiceOffering, Long> {

    List<ServiceOffering> findByGarageId(Long garageId);

    Optional<ServiceOffering> findByIdAndGarageId(Long id, Long garageId);
}
