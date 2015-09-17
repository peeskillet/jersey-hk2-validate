
package com.underdog.jersey.hk2.validate.jpa;

import java.io.Closeable;
import java.io.IOException;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.glassfish.hk2.api.Factory;
import org.glassfish.jersey.server.CloseableService;

public class EntityManagerHK2Factory implements Factory<EntityManager>{
    
    private final EntityManager entityManager;
    
    @Inject
    public EntityManagerHK2Factory(CloseableService closeableService,
                                   EntityManagerFactory emf) {
        this.entityManager = emf.createEntityManager();
        closeableService.add(new Closeable() {
            @Override
            public void close() throws IOException {
                if (entityManager.isOpen()) {
                    entityManager.close();
                }
            }
        });
    }

    @Override
    public EntityManager provide() { return entityManager; }

    @Override
    public void dispose(EntityManager em) {}
    
}
