
package com.underdog.jersey.hk2.validate;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import static org.junit.Assert.*;

public class ValidationFeatureTest extends JerseyTest {
    
    @Override
    public ResourceConfig configure() {
        ValidationFeature validationFeature = new ValidationFeature.Builder()
                .addSingletonClass(ModelServiceImpl.class, ModelService.class).build();
        ResourceConfig config = new ResourceConfig(ModelResource.class)
                .register(validationFeature);
        return config;
    }
    
    @Test
    public void should_fail_with_400_on_missing_firstname() {
        Model model = new Model();
        model.setLastName("Overflow");
        
        Response response = target("model").request().post(Entity.json(model));
        assertEquals(400, response.getStatus());
        
        response.close();
    }
    
    @Test
    public void should_fail_with_400_on_one_valid_and_one_invalid_model() {
        Form form = new Form();
        form.param("fname1", "Stack");
        form.param("fname2", "Stack");
        form.param("lname2", "Overflow");
        
        Response response = target("model/two").request().post(Entity.form(form));
        assertEquals(400, response.getStatus());
        response.close();
    }
    
    @Test
    public void should_succeed_with_200_on_two_valid_models() {
        Form form = new Form();
        form.param("fname1", "Stack");
        form.param("lname1", "Overflow");
        form.param("fname2", "Stack");
        form.param("lname2", "Overflow");
        
        Response response = target("model/two").request().post(Entity.form(form));
        assertEquals(200, response.getStatus());
        response.close();
    }
    
    @Test
    public void should_succeed_with_200_on_having_all_required_fields() {
        Model model = new Model();
        model.setLastName("Overflow");
        model.setFirstName("Stack");
        
        Response response = target("model").request().post(Entity.json(model));
        assertEquals(200, response.getStatus());
        assertTrue(response.readEntity(String.class).contains("100")); // generated id
        
        response.close();
    }
}
