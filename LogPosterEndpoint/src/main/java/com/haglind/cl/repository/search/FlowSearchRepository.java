package com.haglind.cl.repository.search;

import com.haglind.cl.domain.Flow;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Flow entity.
 */
public interface FlowSearchRepository extends ElasticsearchRepository<Flow, Long> {
}
