package de.reinhard.merlin.app.rest;

import de.reinhard.merlin.app.json.JsonUtils;
import de.reinhard.merlin.app.logging.Log4jMemoryAppender;
import de.reinhard.merlin.app.logging.LogFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/logging")
public class LoggingRest {
    private Logger log = LoggerFactory.getLogger(LoggingRest.class);

    @POST
    @Path("query")
    @Produces(MediaType.APPLICATION_JSON)
    public String query(String jsonLogFilter) {
        LogFilter filter = JsonUtils.fromJson(LogFilter.class, jsonLogFilter);
        Log4jMemoryAppender appender = Log4jMemoryAppender.getInstance();
        return JsonUtils.toJson(appender.query(filter));
    }
}
