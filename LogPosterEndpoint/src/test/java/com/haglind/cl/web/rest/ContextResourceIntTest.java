package com.haglind.cl.web.rest;

import com.haglind.cl.LogPosterEndpointApp;
import com.haglind.cl.domain.Context;
import com.haglind.cl.repository.ContextRepository;
import com.haglind.cl.repository.search.ContextSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.haglind.cl.domain.enumeration.ContextType;

/**
 * Test class for the ContextResource REST controller.
 *
 * @see ContextResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = LogPosterEndpointApp.class)
@WebAppConfiguration
@IntegrationTest
public class ContextResourceIntTest {


    private static final ContextType DEFAULT_CONTEXT_TYPE = ContextType.GEO_TRACE;
    private static final ContextType UPDATED_CONTEXT_TYPE = ContextType.EVENT_FLOW;
    private static final String DEFAULT_TEXT = "AAAAA";
    private static final String UPDATED_TEXT = "BBBBB";

    @Inject
    private ContextRepository contextRepository;

    @Inject
    private ContextSearchRepository contextSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restContextMockMvc;

    private Context context;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ContextResource contextResource = new ContextResource();
        ReflectionTestUtils.setField(contextResource, "contextSearchRepository", contextSearchRepository);
        ReflectionTestUtils.setField(contextResource, "contextRepository", contextRepository);
        this.restContextMockMvc = MockMvcBuilders.standaloneSetup(contextResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        contextSearchRepository.deleteAll();
        context = new Context();
        context.setContextType(DEFAULT_CONTEXT_TYPE);
        context.setText(DEFAULT_TEXT);
    }

    @Test
    @Transactional
    public void createContext() throws Exception {
        int databaseSizeBeforeCreate = contextRepository.findAll().size();

        // Create the Context

        restContextMockMvc.perform(post("/api/contexts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(context)))
                .andExpect(status().isCreated());

        // Validate the Context in the database
        List<Context> contexts = contextRepository.findAll();
        assertThat(contexts).hasSize(databaseSizeBeforeCreate + 1);
        Context testContext = contexts.get(contexts.size() - 1);
        assertThat(testContext.getContextType()).isEqualTo(DEFAULT_CONTEXT_TYPE);
        assertThat(testContext.getText()).isEqualTo(DEFAULT_TEXT);

        // Validate the Context in ElasticSearch
        Context contextEs = contextSearchRepository.findOne(testContext.getId());
        assertThat(contextEs).isEqualToComparingFieldByField(testContext);
    }

    @Test
    @Transactional
    public void checkTextIsRequired() throws Exception {
        int databaseSizeBeforeTest = contextRepository.findAll().size();
        // set the field null
        context.setText(null);

        // Create the Context, which fails.

        restContextMockMvc.perform(post("/api/contexts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(context)))
                .andExpect(status().isBadRequest());

        List<Context> contexts = contextRepository.findAll();
        assertThat(contexts).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllContexts() throws Exception {
        // Initialize the database
        contextRepository.saveAndFlush(context);

        // Get all the contexts
        restContextMockMvc.perform(get("/api/contexts?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(context.getId().intValue())))
                .andExpect(jsonPath("$.[*].contextType").value(hasItem(DEFAULT_CONTEXT_TYPE.toString())))
                .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT.toString())));
    }

    @Test
    @Transactional
    public void getContext() throws Exception {
        // Initialize the database
        contextRepository.saveAndFlush(context);

        // Get the context
        restContextMockMvc.perform(get("/api/contexts/{id}", context.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(context.getId().intValue()))
            .andExpect(jsonPath("$.contextType").value(DEFAULT_CONTEXT_TYPE.toString()))
            .andExpect(jsonPath("$.text").value(DEFAULT_TEXT.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingContext() throws Exception {
        // Get the context
        restContextMockMvc.perform(get("/api/contexts/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateContext() throws Exception {
        // Initialize the database
        contextRepository.saveAndFlush(context);
        contextSearchRepository.save(context);
        int databaseSizeBeforeUpdate = contextRepository.findAll().size();

        // Update the context
        Context updatedContext = new Context();
        updatedContext.setId(context.getId());
        updatedContext.setContextType(UPDATED_CONTEXT_TYPE);
        updatedContext.setText(UPDATED_TEXT);

        restContextMockMvc.perform(put("/api/contexts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedContext)))
                .andExpect(status().isOk());

        // Validate the Context in the database
        List<Context> contexts = contextRepository.findAll();
        assertThat(contexts).hasSize(databaseSizeBeforeUpdate);
        Context testContext = contexts.get(contexts.size() - 1);
        assertThat(testContext.getContextType()).isEqualTo(UPDATED_CONTEXT_TYPE);
        assertThat(testContext.getText()).isEqualTo(UPDATED_TEXT);

        // Validate the Context in ElasticSearch
        Context contextEs = contextSearchRepository.findOne(testContext.getId());
        assertThat(contextEs).isEqualToComparingFieldByField(testContext);
    }

    @Test
    @Transactional
    public void deleteContext() throws Exception {
        // Initialize the database
        contextRepository.saveAndFlush(context);
        contextSearchRepository.save(context);
        int databaseSizeBeforeDelete = contextRepository.findAll().size();

        // Get the context
        restContextMockMvc.perform(delete("/api/contexts/{id}", context.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean contextExistsInEs = contextSearchRepository.exists(context.getId());
        assertThat(contextExistsInEs).isFalse();

        // Validate the database is empty
        List<Context> contexts = contextRepository.findAll();
        assertThat(contexts).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchContext() throws Exception {
        // Initialize the database
        contextRepository.saveAndFlush(context);
        contextSearchRepository.save(context);

        // Search the context
        restContextMockMvc.perform(get("/api/_search/contexts?query=id:" + context.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(context.getId().intValue())))
            .andExpect(jsonPath("$.[*].contextType").value(hasItem(DEFAULT_CONTEXT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT.toString())));
    }
}
