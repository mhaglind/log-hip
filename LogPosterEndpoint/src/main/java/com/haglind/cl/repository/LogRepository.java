package com.haglind.cl.repository;

import com.haglind.cl.domain.Log;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Log entity.
 */
public interface LogRepository extends JpaRepository<Log,Long> {

}
