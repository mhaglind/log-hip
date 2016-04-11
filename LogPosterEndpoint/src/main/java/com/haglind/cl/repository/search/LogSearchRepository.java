package com.haglind.cl.repository.search;

import com.haglind.cl.domain.Log;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Log entity.
 */
public interface LogSearchRepository extends ElasticsearchRepository<Log, Long> {
}
