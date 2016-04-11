package com.haglind.cl.repository;

import com.haglind.cl.domain.Context;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Context entity.
 */
public interface ContextRepository extends JpaRepository<Context,Long> {

}
