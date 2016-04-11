package com.haglind.cl.repository.search;

import com.haglind.cl.domain.Context;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Context entity.
 */
public interface ContextSearchRepository extends ElasticsearchRepository<Context, Long> {
}
