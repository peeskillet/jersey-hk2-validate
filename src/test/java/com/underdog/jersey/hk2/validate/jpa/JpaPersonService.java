/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.underdog.jersey.hk2.validate.jpa;

import com.underdog.jersey.hk2.validate.Validatable;
import com.underdog.jersey.hk2.validate.Validated;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.validation.Valid;

/**
 *
 * @author PaulSamsotha
 */
public class JpaPersonService implements PersonService, Validatable {
    
    /**
     * Inject as javax.inject.Provider because we are making the 
     * JpaPersonService a singleton, and the EntityManager will
     * be in a request scope.
     */
    @Inject
    private Provider<EntityManager> entityManager;

    @Override
    @Validated
    public Person save(@Valid Person person) {
        EntityManager em = entityManager.get();
        em.getTransaction().begin();
        em.persist(person);
        em.getTransaction().commit();
        return person;
    }
}
