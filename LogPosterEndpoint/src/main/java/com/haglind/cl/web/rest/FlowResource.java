package com.haglind.cl.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.haglind.cl.domain.Flow;
import com.haglind.cl.repository.FlowRepository;
import com.haglind.cl.repository.search.FlowSearchRepository;
import com.haglind.cl.web.rest.util.HeaderUtil;
import com.haglind.cl.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Flow.
 */
@RestController
@RequestMapping("/api")
public class FlowResource {

    private final Logger log = LoggerFactory.getLogger(FlowResource.class);
        
    @Inject
    private FlowRepository flowRepository;
    
    @Inject
    private FlowSearchRepository flowSearchRepository;
    
    /**
     * POST  /flows : Create a new flow.
     *
     * @param flow the flow to create
     * @return the ResponseEntity with status 201 (Created) and with body the new flow, or with status 400 (Bad Request) if the flow has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/flows",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Flow> createFlow(@Valid @RequestBody Flow flow) throws URISyntaxException {
        log.debug("REST request to save Flow : {}", flow);
        if (flow.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("flow", "idexists", "A new flow cannot already have an ID")).body(null);
        }
        Flow result = flowRepository.save(flow);
        flowSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/flows/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("flow", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /flows : Updates an existing flow.
     *
     * @param flow the flow to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated flow,
     * or with status 400 (Bad Request) if the flow is not valid,
     * or with status 500 (Internal Server Error) if the flow couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/flows",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Flow> updateFlow(@Valid @RequestBody Flow flow) throws URISyntaxException {
        log.debug("REST request to update Flow : {}", flow);
        if (flow.getId() == null) {
            return createFlow(flow);
        }
        Flow result = flowRepository.save(flow);
        flowSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("flow", flow.getId().toString()))
            .body(result);
    }

    /**
     * GET  /flows : get all the flows.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of flows in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/flows",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Flow>> getAllFlows(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Flows");
        Page<Flow> page = flowRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/flows");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /flows/:id : get the "id" flow.
     *
     * @param id the id of the flow to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the flow, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/flows/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Flow> getFlow(@PathVariable Long id) {
        log.debug("REST request to get Flow : {}", id);
        Flow flow = flowRepository.findOne(id);
        return Optional.ofNullable(flow)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /flows/:id : delete the "id" flow.
     *
     * @param id the id of the flow to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/flows/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteFlow(@PathVariable Long id) {
        log.debug("REST request to delete Flow : {}", id);
        flowRepository.delete(id);
        flowSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("flow", id.toString())).build();
    }

    /**
     * SEARCH  /_search/flows?query=:query : search for the flow corresponding
     * to the query.
     *
     * @param query the query of the flow search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/flows",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Flow>> searchFlows(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Flows for query {}", query);
        Page<Flow> page = flowSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/flows");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
