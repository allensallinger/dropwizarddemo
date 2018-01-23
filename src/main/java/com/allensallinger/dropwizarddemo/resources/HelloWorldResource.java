package com.allensallinger.dropwizarddemo.resources;

import com.allensallinger.dropwizarddemo.api.Saying;
import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Path("/hello-world")
@Produces(MediaType.APPLICATION_JSON)
public class HelloWorldResource {
    private final String template;
    private final String defaultName;
    private final AtomicLong counter;

    private HashMap<Long, String> sayingMap = new HashMap<>();

    public HelloWorldResource(String template, String defaultName) {
        this.template = template;
        this.defaultName = defaultName;
        this.counter = new AtomicLong();
    }

    // Create
    @POST
    @Timed
    public Saying createHello(@QueryParam("name") Optional<String> name, @QueryParam("content") Optional<String> content) {
        System.out.println(sayingMap);
        final String value = String.format(template, name.orElse(defaultName));

        Saying s = new Saying(counter.incrementAndGet(), value);

        String prevKey = sayingMap.put(s.getId(), s.getContent());

        System.out.println("Get is being triggered");
        return s;
    }

    // Read
    @GET
    @Timed
    public Saying sayHello(@QueryParam("id") Optional<Long> id) {
        System.out.println(sayingMap);
        Long unwrapedId = id.orElse((long) -1);

        System.out.println(sayingMap.get(unwrapedId));

        Long returnId = (long) 404;
        String returnContent = "Content not found";

        if (sayingMap.containsKey(unwrapedId)) {
            System.out.println("key was found in sayingMap");
            returnId = unwrapedId;
            returnContent = sayingMap.get(unwrapedId);
        }

        Saying s = new Saying(returnId, returnContent);

        return s;
    }

    // Update
    @PUT
    @Timed
    public Saying updateHello(@QueryParam("id") Optional<Long> id, @QueryParam("content") Optional<String> content) {
        System.out.print(sayingMap);
        if (id.isPresent()) {
            Long unwrapedId = id.orElse((long) -1);
            String unwrappedContent = content.orElse("mocked content");

            String sayingValue = sayingMap.get(unwrapedId);

            sayingMap.put(unwrapedId, unwrappedContent);

            // Won't acutally be triggered unless the optional value is present
            Saying s = new Saying(unwrapedId, unwrappedContent);
            return s;
        } else {
            Saying errorSaying;
            errorSaying = new Saying(404, "ID not found, please try again");
            return errorSaying;
        }

    }

    // Delete
}
