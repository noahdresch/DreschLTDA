package com.onboardingrsd.empresas.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

/**
 * Produz o {@link DataSource} HikariCP a partir de {@code db.properties}.
 */
@ApplicationScoped
public class DataSourceProducer {

    private HikariDataSource dataSource;

    @PostConstruct
    void init() {
        Properties props = carregarPropriedades();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(props.getProperty("jdbc.url"));
        config.setUsername(props.getProperty("jdbc.user"));
        config.setPassword(props.getProperty("jdbc.password"));
        config.setDriverClassName(props.getProperty("jdbc.driver", "org.postgresql.Driver"));
        config.setMaximumPoolSize(
                Integer.parseInt(props.getProperty("hikari.maximumPoolSize", "10")));
        config.setPoolName(props.getProperty("hikari.poolName", "EmpresasHikariPool"));

        dataSource = new HikariDataSource(config);

        // ===== DEBUG =====
        System.out.println("====================================");
        System.out.println("DataSource criado com sucesso");
        System.out.println("JDBC URL : " + config.getJdbcUrl());
        System.out.println("Usuário  : " + config.getUsername());
        System.out.println("Driver   : " + config.getDriverClassName());
        System.out.println("====================================");
    }

    @Produces
    @ApplicationScoped
    public DataSource dataSource() {
        return dataSource;
    }

    @PreDestroy
    void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    private static Properties carregarPropriedades() {
        Properties props = new Properties();

        try (InputStream in = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("db.properties")) {

            if (in == null) {
                throw new IllegalStateException(
                        "Arquivo db.properties não encontrado no classpath.");
            }

            props.load(in);

        } catch (IOException e) {
            throw new IllegalStateException("Falha ao ler db.properties", e);
        }

        return props;
    }
}