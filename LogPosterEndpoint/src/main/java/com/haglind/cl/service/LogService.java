package com.haglind.cl.service;

import com.haglind.cl.domain.Log;
import com.haglind.cl.repository.LogRepository;
import com.haglind.cl.repository.search.LogSearchRepository;
import com.haglind.cl.web.rest.dto.LogDTO;
import com.haglind.cl.web.rest.mapper.LogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Log.
 */
@Service
@Transactional
public class LogService {

    private final Logger log = LoggerFactory.getLogger(LogService.class);
    
    @Inject
    private LogRepository logRepository;
    
    @Inject
    private LogMapper logMapper;
    
    @Inject
    private LogSearchRepository logSearchRepository;
    
    /**
     * Save a log.
     * 
     * @param logDTO the entity to save
     * @return the persisted entity
     */
    public LogDTO save(LogDTO logDTO) {
        log.debug("Request to save Log : {}", logDTO);
        Log log = logMapper.logDTOToLog(logDTO);
        log = logRepository.save(log);
        LogDTO result = logMapper.logToLogDTO(log);
        logSearchRepository.save(log);
        return result;
    }

    /**
     *  Get all the logs.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Log> findAll(Pageable pageable) {
        log.debug("Request to get all Logs");
        Page<Log> result = logRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one log by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public LogDTO findOne(Long id) {
        log.debug("Request to get Log : {}", id);
        Log log = logRepository.findOne(id);
        LogDTO logDTO = logMapper.logToLogDTO(log);
        return logDTO;
    }

    /**
     *  Delete the  log by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Log : {}", id);
        logRepository.delete(id);
        logSearchRepository.delete(id);
    }

    /**
     * Search for the log corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Log> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Logs for query {}", query);
        return logSearchRepository.search(queryStringQuery(query), pageable);
    }
}
