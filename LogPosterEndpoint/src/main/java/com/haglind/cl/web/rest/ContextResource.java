package com.haglind.cl.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.haglind.cl.domain.Context;
import com.haglind.cl.repository.ContextRepository;
import com.haglind.cl.repository.search.ContextSearchRepository;
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
 * REST controller for managing Context.
 */
@RestController
@RequestMapping("/api")
public class ContextResource {

    private final Logger log = LoggerFactory.getLogger(ContextResource.class);
        
    @Inject
    private ContextRepository contextRepository;
    
    @Inject
    private ContextSearchRepository contextSearchRepository;
    
    /**
     * POST  /contexts : Create a new context.
     *
     * @param context the context to create
     * @return the ResponseEntity with status 201 (Created) and with body the new context, or with status 400 (Bad Request) if the context has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/contexts",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Context> createContext(@Valid @RequestBody Context context) throws URISyntaxException {
        log.debug("REST request to save Context : {}", context);
        if (context.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("context", "idexists", "A new context cannot already have an ID")).body(null);
        }
        Context result = contextRepository.save(context);
        contextSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/contexts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("context", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /contexts : Updates an existing context.
     *
     * @param context the context to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated context,
     * or with status 400 (Bad Request) if the context is not valid,
     * or with status 500 (Internal Server Error) if the context couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/contexts",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Context> updateContext(@Valid @RequestBody Context context) throws URISyntaxException {
        log.debug("REST request to update Context : {}", context);
        if (context.getId() == null) {
            return createContext(context);
        }
        Context result = contextRepository.save(context);
        contextSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("context", context.getId().toString()))
            .body(result);
    }

    /**
     * GET  /contexts : get all the contexts.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of contexts in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/contexts",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Context>> getAllContexts(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Contexts");
        Page<Context> page = contextRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/contexts");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /contexts/:id : get the "id" context.
     *
     * @param id the id of the context to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the context, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/contexts/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Context> getContext(@PathVariable Long id) {
        log.debug("REST request to get Context : {}", id);
        Context context = contextRepository.findOne(id);
        return Optional.ofNullable(context)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /contexts/:id : delete the "id" context.
     *
     * @param id the id of the context to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/contexts/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteContext(@PathVariable Long id) {
        log.debug("REST request to delete Context : {}", id);
        contextRepository.delete(id);
        contextSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("context", id.toString())).build();
    }

    /**
     * SEARCH  /_search/contexts?query=:query : search for the context corresponding
     * to the query.
     *
     * @param query the query of the context search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/contexts",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Context>> searchContexts(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Contexts for query {}", query);
        Page<Context> page = contextSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/contexts");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
