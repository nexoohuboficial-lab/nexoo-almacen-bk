package com.nexoohub.almacen.crm.repository;

import com.nexoohub.almacen.crm.entity.CampanaMarketing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampanaMarketingRepository extends JpaRepository<CampanaMarketing, Integer> {
}
