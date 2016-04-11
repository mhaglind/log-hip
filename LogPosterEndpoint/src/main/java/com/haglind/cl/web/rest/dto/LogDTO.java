package com.haglind.cl.web.rest.dto;

import java.time.ZonedDateTime;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


/**
 * A DTO for the Log entity.
 */
public class LogDTO implements Serializable {

    private Long id;

    private ZonedDateTime createdTime;


    @NotNull
    private String text;


    private Long predecessorId;
    private Long successorId;
    private Long contextId;
    private Long flowId;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public ZonedDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(ZonedDateTime createdTime) {
        this.createdTime = createdTime;
    }
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getPredecessorId() {
        return predecessorId;
    }

    public void setPredecessorId(Long logId) {
        this.predecessorId = logId;
    }
    public Long getSuccessorId() {
        return successorId;
    }

    public void setSuccessorId(Long logId) {
        this.successorId = logId;
    }
    public Long getContextId() {
        return contextId;
    }

    public void setContextId(Long contextId) {
        this.contextId = contextId;
    }
    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LogDTO logDTO = (LogDTO) o;

        if ( ! Objects.equals(id, logDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "LogDTO{" +
            "id=" + id +
            ", createdTime='" + createdTime + "'" +
            ", text='" + text + "'" +
            '}';
    }
}
