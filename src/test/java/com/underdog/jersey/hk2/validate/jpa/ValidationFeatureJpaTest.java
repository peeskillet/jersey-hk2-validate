package com.underdog.jersey.hk2.validate.jpa;

import com.underdog.jersey.hk2.validate.ValidationFeature;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import javax.ws.rs.client.Entity;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.server.ResourceConfig;

import javax.inject.Singleton;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;

import static org.junit.Assert.*;
import org.junit.Test;

public class ValidationFeatureJpaTest extends JerseyTest {

    @Override
    protected ResourceConfig configure() {
        ValidationFeature validationFeature = new ValidationFeature.Builder()
                .addSingletonClass(JpaPersonService.class, PersonService.class).build();
        ResourceConfig config = new ResourceConfig(PersonResource.class);
        config.register(validationFeature);

        // bind our JPA stuff to HK2
        config.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(EntityManagerFactoryFactory.class)
                        .to(EntityManagerFactory.class).in(Singleton.class);
                bindFactory(EntityManagerHK2Factory.class)
                        .to(EntityManager.class).in(RequestScoped.class);
            }
        });
        return config;
    }

    @Test
    public void should_fail_with_400_on_missing_firstname() {
        Form form = new Form();
        form.param("lastName", "Overflow");
        Response response = target("persons").request().post(Entity.form(form));

        assertEquals(400, response.getStatus());
    }
    
    @Test
    public void should_success_with_201_on_valid_person() {
        Form form = new Form();
        form.param("lastName", "Overflow");
        form.param("firstName", "Stack");
        Response response = target("persons").request().post(Entity.form(form));

        assertEquals(201, response.getStatus());
        Person person = response.readEntity(Person.class);
        assertNotNull(person.getId());
        
        System.out.println(person);
        response.close();
    }
}
