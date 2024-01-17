package org.wolpertinger.hidden.forms.http;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("submit")
public class SubmitResource {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response submitForm(@RequestBody String form) {
        logger.info(form);
        return Response.ok(form).build();
    }
}
