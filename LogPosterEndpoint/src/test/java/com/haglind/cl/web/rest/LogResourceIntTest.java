package com.haglind.cl.web.rest;

import com.haglind.cl.LogPosterEndpointApp;
import com.haglind.cl.domain.Log;
import com.haglind.cl.repository.LogRepository;
import com.haglind.cl.service.LogService;
import com.haglind.cl.repository.search.LogSearchRepository;
import com.haglind.cl.web.rest.dto.LogDTO;
import com.haglind.cl.web.rest.mapper.LogMapper;

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
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the LogResource REST controller.
 *
 * @see LogResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = LogPosterEndpointApp.class)
@WebAppConfiguration
@IntegrationTest
public class LogResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));


    private static final ZonedDateTime DEFAULT_CREATED_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_CREATED_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_CREATED_TIME_STR = dateTimeFormatter.format(DEFAULT_CREATED_TIME);
    private static final String DEFAULT_TEXT = "AAAAA";
    private static final String UPDATED_TEXT = "BBBBB";

    @Inject
    private LogRepository logRepository;

    @Inject
    private LogMapper logMapper;

    @Inject
    private LogService logService;

    @Inject
    private LogSearchRepository logSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restLogMockMvc;

    private Log log;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        LogResource logResource = new LogResource();
        ReflectionTestUtils.setField(logResource, "logService", logService);
        ReflectionTestUtils.setField(logResource, "logMapper", logMapper);
        this.restLogMockMvc = MockMvcBuilders.standaloneSetup(logResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        logSearchRepository.deleteAll();
        log = new Log();
        log.setCreatedTime(DEFAULT_CREATED_TIME);
        log.setText(DEFAULT_TEXT);
    }

    @Test
    @Transactional
    public void createLog() throws Exception {
        int databaseSizeBeforeCreate = logRepository.findAll().size();

        // Create the Log
        LogDTO logDTO = logMapper.logToLogDTO(log);

        restLogMockMvc.perform(post("/api/logs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(logDTO)))
                .andExpect(status().isCreated());

        // Validate the Log in the database
        List<Log> logs = logRepository.findAll();
        assertThat(logs).hasSize(databaseSizeBeforeCreate + 1);
        Log testLog = logs.get(logs.size() - 1);
        assertThat(testLog.getCreatedTime()).isEqualTo(DEFAULT_CREATED_TIME);
        assertThat(testLog.getText()).isEqualTo(DEFAULT_TEXT);

        // Validate the Log in ElasticSearch
        Log logEs = logSearchRepository.findOne(testLog.getId());
        assertThat(logEs).isEqualToComparingFieldByField(testLog);
    }

    @Test
    @Transactional
    public void checkTextIsRequired() throws Exception {
        int databaseSizeBeforeTest = logRepository.findAll().size();
        // set the field null
        log.setText(null);

        // Create the Log, which fails.
        LogDTO logDTO = logMapper.logToLogDTO(log);

        restLogMockMvc.perform(post("/api/logs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(logDTO)))
                .andExpect(status().isBadRequest());

        List<Log> logs = logRepository.findAll();
        assertThat(logs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllLogs() throws Exception {
        // Initialize the database
        logRepository.saveAndFlush(log);

        // Get all the logs
        restLogMockMvc.perform(get("/api/logs?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(log.getId().intValue())))
                .andExpect(jsonPath("$.[*].createdTime").value(hasItem(DEFAULT_CREATED_TIME_STR)))
                .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT.toString())));
    }

    @Test
    @Transactional
    public void getLog() throws Exception {
        // Initialize the database
        logRepository.saveAndFlush(log);

        // Get the log
        restLogMockMvc.perform(get("/api/logs/{id}", log.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(log.getId().intValue()))
            .andExpect(jsonPath("$.createdTime").value(DEFAULT_CREATED_TIME_STR))
            .andExpect(jsonPath("$.text").value(DEFAULT_TEXT.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingLog() throws Exception {
        // Get the log
        restLogMockMvc.perform(get("/api/logs/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLog() throws Exception {
        // Initialize the database
        logRepository.saveAndFlush(log);
        logSearchRepository.save(log);
        int databaseSizeBeforeUpdate = logRepository.findAll().size();

        // Update the log
        Log updatedLog = new Log();
        updatedLog.setId(log.getId());
        updatedLog.setCreatedTime(UPDATED_CREATED_TIME);
        updatedLog.setText(UPDATED_TEXT);
        LogDTO logDTO = logMapper.logToLogDTO(updatedLog);

        restLogMockMvc.perform(put("/api/logs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(logDTO)))
                .andExpect(status().isOk());

        // Validate the Log in the database
        List<Log> logs = logRepository.findAll();
        assertThat(logs).hasSize(databaseSizeBeforeUpdate);
        Log testLog = logs.get(logs.size() - 1);
        assertThat(testLog.getCreatedTime()).isEqualTo(UPDATED_CREATED_TIME);
        assertThat(testLog.getText()).isEqualTo(UPDATED_TEXT);

        // Validate the Log in ElasticSearch
        Log logEs = logSearchRepository.findOne(testLog.getId());
        assertThat(logEs).isEqualToComparingFieldByField(testLog);
    }

    @Test
    @Transactional
    public void deleteLog() throws Exception {
        // Initialize the database
        logRepository.saveAndFlush(log);
        logSearchRepository.save(log);
        int databaseSizeBeforeDelete = logRepository.findAll().size();

        // Get the log
        restLogMockMvc.perform(delete("/api/logs/{id}", log.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean logExistsInEs = logSearchRepository.exists(log.getId());
        assertThat(logExistsInEs).isFalse();

        // Validate the database is empty
        List<Log> logs = logRepository.findAll();
        assertThat(logs).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchLog() throws Exception {
        // Initialize the database
        logRepository.saveAndFlush(log);
        logSearchRepository.save(log);

        // Search the log
        restLogMockMvc.perform(get("/api/_search/logs?query=id:" + log.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(log.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdTime").value(hasItem(DEFAULT_CREATED_TIME_STR)))
            .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT.toString())));
    }
}
