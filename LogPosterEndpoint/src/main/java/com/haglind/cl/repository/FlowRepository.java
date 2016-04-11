package com.haglind.cl.repository;

import com.haglind.cl.domain.Flow;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Flow entity.
 */
public interface FlowRepository extends JpaRepository<Flow,Long> {

}
