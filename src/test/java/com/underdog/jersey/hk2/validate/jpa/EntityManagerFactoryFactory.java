/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.underdog.jersey.hk2.validate.jpa;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.glassfish.hk2.api.Factory;

/**
 *
 * @author PaulSamsotha
 */
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
