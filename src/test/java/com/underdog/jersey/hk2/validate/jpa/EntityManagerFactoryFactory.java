
package com.underdog.jersey.hk2.validate.jpa;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.glassfish.hk2.api.Factory;


public class EntityManagerFactoryFactory implements Factory<EntityManagerFactory> {
    
    private final EntityManagerFactory emf;
    
    public EntityManagerFactoryFactory() {
        emf = Persistence.createEntityManagerFactory("ValidationFeature-PU");
    }

    @Override
    public EntityManagerFactory provide() {
        return emf;
    }

    @Override
    public void dispose(EntityManagerFactory t) {}
    
}
