
package com.underdog.jersey.hk2.validate;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("model")
public class ModelResource {
    
    @Inject
    ModelService modelService;
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response post(Model model) {
        Model saved = modelService.save(model);
        return Response.ok(saved).build();
    }
    
    @POST
    @Path("two")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response post(Form form) {
        String fname1 = form.asMap().getFirst("fname1");
        String lname1 = form.asMap().getFirst("lname1");
        Model model1 = new Model(fname1, lname1);
        
        String fname2 = form.asMap().getFirst("fname2");
        String lname2 = form.asMap().getFirst("lname2");
        Model model2 = new Model(fname2, lname2);
        
        modelService.saveTwo(model1, model2);
        
        return Response.ok().build();
    }
}
