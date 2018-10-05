package de.reinhard.merlin.app.rest;

import org.slf4j.Logger;

import javax.ws.rs.core.Response;

public class RestUtils {

    static Response get404Response(Logger log, String errorMessage) {
        log.error(errorMessage);
        Response response = Response.status(404).
                entity(errorMessage).
                type("text/plain").
                build();
        return response;
    }
}
