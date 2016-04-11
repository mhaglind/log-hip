package com.haglind.cl.web.rest.mapper;

import com.haglind.cl.domain.*;
import com.haglind.cl.web.rest.dto.LogDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Log and its DTO LogDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface LogMapper {

    @Mapping(source = "predecessor.id", target = "predecessorId")
    @Mapping(source = "successor.id", target = "successorId")
    @Mapping(source = "context.id", target = "contextId")
    @Mapping(source = "flow.id", target = "flowId")
    LogDTO logToLogDTO(Log log);

    List<LogDTO> logsToLogDTOs(List<Log> logs);

    @Mapping(source = "predecessorId", target = "predecessor")
    @Mapping(source = "successorId", target = "successor")
    @Mapping(source = "contextId", target = "context")
    @Mapping(source = "flowId", target = "flow")
    Log logDTOToLog(LogDTO logDTO);

    List<Log> logDTOsToLogs(List<LogDTO> logDTOs);

    default Log logFromId(Long id) {
        if (id == null) {
            return null;
        }
        Log log = new Log();
        log.setId(id);
        return log;
    }

    default Context contextFromId(Long id) {
        if (id == null) {
            return null;
        }
        Context context = new Context();
        context.setId(id);
        return context;
    }

    default Flow flowFromId(Long id) {
        if (id == null) {
            return null;
        }
        Flow flow = new Flow();
        flow.setId(id);
        return flow;
    }
}
