package com.onboardingrsd.empresas.persistence;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * {@link EntityManager} por requisição HTTP — compartilhado com o interceptor de TX.
 */
public class EntityManagerProducer {

    @Inject
    private EntityManagerFactory entityManagerFactory;

    @Produces
    @RequestScoped
    public EntityManager entityManager() {
        return entityManagerFactory.createEntityManager();
    }

    public void close(@Disposes EntityManager entityManager) {
        if (entityManager.isOpen()) {
            entityManager.close();
        }
    }
}
