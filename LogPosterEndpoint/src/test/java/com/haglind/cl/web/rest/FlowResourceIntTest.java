package com.haglind.cl.web.rest;

import com.haglind.cl.LogPosterEndpointApp;
import com.haglind.cl.domain.Flow;
import com.haglind.cl.repository.FlowRepository;
import com.haglind.cl.repository.search.FlowSearchRepository;

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


/**
 * Test class for the FlowResource REST controller.
 *
 * @see FlowResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = LogPosterEndpointApp.class)
@WebAppConfiguration
@IntegrationTest
public class FlowResourceIntTest {

    private static final String DEFAULT_TEXT = "AAAAA";
    private static final String UPDATED_TEXT = "BBBBB";

    @Inject
    private FlowRepository flowRepository;

    @Inject
    private FlowSearchRepository flowSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restFlowMockMvc;

    private Flow flow;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        FlowResource flowResource = new FlowResource();
        ReflectionTestUtils.setField(flowResource, "flowSearchRepository", flowSearchRepository);
        ReflectionTestUtils.setField(flowResource, "flowRepository", flowRepository);
        this.restFlowMockMvc = MockMvcBuilders.standaloneSetup(flowResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        flowSearchRepository.deleteAll();
        flow = new Flow();
        flow.setText(DEFAULT_TEXT);
    }

    @Test
    @Transactional
    public void createFlow() throws Exception {
        int databaseSizeBeforeCreate = flowRepository.findAll().size();

        // Create the Flow

        restFlowMockMvc.perform(post("/api/flows")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(flow)))
                .andExpect(status().isCreated());

        // Validate the Flow in the database
        List<Flow> flows = flowRepository.findAll();
        assertThat(flows).hasSize(databaseSizeBeforeCreate + 1);
        Flow testFlow = flows.get(flows.size() - 1);
        assertThat(testFlow.getText()).isEqualTo(DEFAULT_TEXT);

        // Validate the Flow in ElasticSearch
        Flow flowEs = flowSearchRepository.findOne(testFlow.getId());
        assertThat(flowEs).isEqualToComparingFieldByField(testFlow);
    }

    @Test
    @Transactional
    public void checkTextIsRequired() throws Exception {
        int databaseSizeBeforeTest = flowRepository.findAll().size();
        // set the field null
        flow.setText(null);

        // Create the Flow, which fails.

        restFlowMockMvc.perform(post("/api/flows")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(flow)))
                .andExpect(status().isBadRequest());

        List<Flow> flows = flowRepository.findAll();
        assertThat(flows).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllFlows() throws Exception {
        // Initialize the database
        flowRepository.saveAndFlush(flow);

        // Get all the flows
        restFlowMockMvc.perform(get("/api/flows?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(flow.getId().intValue())))
                .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT.toString())));
    }

    @Test
    @Transactional
    public void getFlow() throws Exception {
        // Initialize the database
        flowRepository.saveAndFlush(flow);

        // Get the flow
        restFlowMockMvc.perform(get("/api/flows/{id}", flow.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(flow.getId().intValue()))
            .andExpect(jsonPath("$.text").value(DEFAULT_TEXT.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingFlow() throws Exception {
        // Get the flow
        restFlowMockMvc.perform(get("/api/flows/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFlow() throws Exception {
        // Initialize the database
        flowRepository.saveAndFlush(flow);
        flowSearchRepository.save(flow);
        int databaseSizeBeforeUpdate = flowRepository.findAll().size();

        // Update the flow
        Flow updatedFlow = new Flow();
        updatedFlow.setId(flow.getId());
        updatedFlow.setText(UPDATED_TEXT);

        restFlowMockMvc.perform(put("/api/flows")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedFlow)))
                .andExpect(status().isOk());

        // Validate the Flow in the database
        List<Flow> flows = flowRepository.findAll();
        assertThat(flows).hasSize(databaseSizeBeforeUpdate);
        Flow testFlow = flows.get(flows.size() - 1);
        assertThat(testFlow.getText()).isEqualTo(UPDATED_TEXT);

        // Validate the Flow in ElasticSearch
        Flow flowEs = flowSearchRepository.findOne(testFlow.getId());
        assertThat(flowEs).isEqualToComparingFieldByField(testFlow);
    }

    @Test
    @Transactional
    public void deleteFlow() throws Exception {
        // Initialize the database
        flowRepository.saveAndFlush(flow);
        flowSearchRepository.save(flow);
        int databaseSizeBeforeDelete = flowRepository.findAll().size();

        // Get the flow
        restFlowMockMvc.perform(delete("/api/flows/{id}", flow.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean flowExistsInEs = flowSearchRepository.exists(flow.getId());
        assertThat(flowExistsInEs).isFalse();

        // Validate the database is empty
        List<Flow> flows = flowRepository.findAll();
        assertThat(flows).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchFlow() throws Exception {
        // Initialize the database
        flowRepository.saveAndFlush(flow);
        flowSearchRepository.save(flow);

        // Search the flow
        restFlowMockMvc.perform(get("/api/_search/flows?query=id:" + flow.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(flow.getId().intValue())))
            .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT.toString())));
    }
}
