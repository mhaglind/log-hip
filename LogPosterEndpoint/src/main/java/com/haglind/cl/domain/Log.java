package com.haglind.cl.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A Log.
 */
@Entity
@Table(name = "log")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "log")
public class Log implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "created_time")
    private ZonedDateTime createdTime;

    @NotNull
    @Column(name = "text", nullable = false)
    private String text;

    @OneToOne
    @JoinColumn(unique = true)
    private Log predecessor;

    @OneToOne
    @JoinColumn(unique = true)
    private Log successor;

    @ManyToOne
    private Context context;

    @ManyToOne
    private Flow flow;

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

    public Log getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(Log log) {
        this.predecessor = log;
    }

    public Log getSuccessor() {
        return successor;
    }

    public void setSuccessor(Log log) {
        this.successor = log;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Flow getFlow() {
        return flow;
    }

    public void setFlow(Flow flow) {
        this.flow = flow;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Log log = (Log) o;
        if(log.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, log.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Log{" +
            "id=" + id +
            ", createdTime='" + createdTime + "'" +
            ", text='" + text + "'" +
            '}';
    }
}
