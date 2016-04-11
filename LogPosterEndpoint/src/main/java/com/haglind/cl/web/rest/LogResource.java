package com.haglind.cl.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.haglind.cl.domain.Log;
import com.haglind.cl.service.LogService;
import com.haglind.cl.web.rest.util.HeaderUtil;
import com.haglind.cl.web.rest.util.PaginationUtil;
import com.haglind.cl.web.rest.dto.LogDTO;
import com.haglind.cl.web.rest.mapper.LogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Log.
 */
@RestController
@RequestMapping("/api")
public class LogResource {

    private final Logger log = LoggerFactory.getLogger(LogResource.class);
        
    @Inject
    private LogService logService;
    
    @Inject
    private LogMapper logMapper;
    
    /**
     * POST  /logs : Create a new log.
     *
     * @param logDTO the logDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new logDTO, or with status 400 (Bad Request) if the log has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/logs",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<LogDTO> createLog(@Valid @RequestBody LogDTO logDTO) throws URISyntaxException {
        log.debug("REST request to save Log : {}", logDTO);
        if (logDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("log", "idexists", "A new log cannot already have an ID")).body(null);
        }
        LogDTO result = logService.save(logDTO);
        return ResponseEntity.created(new URI("/api/logs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("log", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /logs : Updates an existing log.
     *
     * @param logDTO the logDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated logDTO,
     * or with status 400 (Bad Request) if the logDTO is not valid,
     * or with status 500 (Internal Server Error) if the logDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/logs",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<LogDTO> updateLog(@Valid @RequestBody LogDTO logDTO) throws URISyntaxException {
        log.debug("REST request to update Log : {}", logDTO);
        if (logDTO.getId() == null) {
            return createLog(logDTO);
        }
        LogDTO result = logService.save(logDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("log", logDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /logs : get all the logs.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of logs in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/logs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<LogDTO>> getAllLogs(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Logs");
        Page<Log> page = logService.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/logs");
        return new ResponseEntity<>(logMapper.logsToLogDTOs(page.getContent()), headers, HttpStatus.OK);
    }

    /**
     * GET  /logs/:id : get the "id" log.
     *
     * @param id the id of the logDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the logDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/logs/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<LogDTO> getLog(@PathVariable Long id) {
        log.debug("REST request to get Log : {}", id);
        LogDTO logDTO = logService.findOne(id);
        return Optional.ofNullable(logDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /logs/:id : delete the "id" log.
     *
     * @param id the id of the logDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/logs/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteLog(@PathVariable Long id) {
        log.debug("REST request to delete Log : {}", id);
        logService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("log", id.toString())).build();
    }

    /**
     * SEARCH  /_search/logs?query=:query : search for the log corresponding
     * to the query.
     *
     * @param query the query of the log search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/logs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<LogDTO>> searchLogs(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Logs for query {}", query);
        Page<Log> page = logService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/logs");
        return new ResponseEntity<>(logMapper.logsToLogDTOs(page.getContent()), headers, HttpStatus.OK);
    }

}
