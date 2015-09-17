/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.underdog.jersey.hk2.validate.jpa;

import java.net.URI;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author PaulSamsotha
 */
@Path("persons")
public class PersonResource {
    
    @Inject
    PersonService personService;
    
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response post(Form form, @Context UriInfo uriInfo) {
        String firstName = form.asMap().getFirst("firstName");
        String lastName = form.asMap().getFirst("lastName");
        Person person = new Person(firstName, lastName);
        person = personService.save(person);
        
        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(person.getId())).build();
        return Response.created(uri).entity(person).build();
    }
}
