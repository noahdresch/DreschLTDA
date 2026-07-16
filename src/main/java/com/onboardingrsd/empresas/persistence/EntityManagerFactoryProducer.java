package com.onboardingrsd.empresas.persistence;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Cria a {@link EntityManagerFactory} RESOURCE_LOCAL ligada ao DataSource Hikari.
 */
@ApplicationScoped
public class EntityManagerFactoryProducer {

    public static final String PU_NAME = "empresasPU";

    @Inject
    private DataSource dataSource;

    private EntityManagerFactory entityManagerFactory;

    @PostConstruct
    void init() {
        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.connection.datasource", dataSource);
        props.put("jakarta.persistence.nonJtaDataSource", dataSource);
        entityManagerFactory = Persistence.createEntityManagerFactory(PU_NAME, props);
    }

    @Produces
    @ApplicationScoped
    public EntityManagerFactory entityManagerFactory() {
        return entityManagerFactory;
    }

    @PreDestroy
    void close() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }
}
