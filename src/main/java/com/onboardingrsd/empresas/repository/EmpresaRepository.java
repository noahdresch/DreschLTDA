package com.onboardingrsd.empresas.repository;

import java.util.List;
import java.util.Optional;

import com.onboardingrsd.empresas.model.Empresa;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

/**
 * Acesso a dados de {@link Empresa}.
 */
@ApplicationScoped
public class EmpresaRepository {

    @Inject
    private EntityManager entityManager;

    public List<Empresa> findAll() {
        return entityManager
                .createQuery("SELECT e FROM Empresa e ORDER BY e.razaoSocial", Empresa.class)
                .getResultList();
    }

    /**
     * Pesquisa por termo livre em razão social, nome fantasia, CNPJ e ramo.
     * Sem termo (null/blank), equivale a {@link #findAll()}.
     */
    public List<Empresa> findByFiltro(String filtro) {
        if (filtro == null || filtro.isBlank()) {
            return findAll();
        }

        String termoBruto = filtro.trim();
        String termo = "%" + termoBruto.toLowerCase() + "%";
        String digitos = termoBruto.replaceAll("\\D", "");

        StringBuilder jpql = new StringBuilder("""
                SELECT e FROM Empresa e
                WHERE LOWER(e.razaoSocial) LIKE :termo
                   OR LOWER(e.nomeFantasia) LIKE :termo
                   OR e.cnpj LIKE :termo
                   OR LOWER(e.ramoAtividade) LIKE :termo
                """);
        if (!digitos.isEmpty()) {
            jpql.append(" OR e.cnpj LIKE :cnpjDigitos");
        }
        jpql.append(" ORDER BY e.razaoSocial");

        TypedQuery<Empresa> query = entityManager.createQuery(jpql.toString(), Empresa.class);
        query.setParameter("termo", termo);
        if (!digitos.isEmpty()) {
            query.setParameter("cnpjDigitos", "%" + digitos + "%");
        }
        return query.getResultList();
    }

    public Optional<Empresa> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(entityManager.find(Empresa.class, id));
    }

    public void salvar(Empresa empresa) {
        entityManager.persist(empresa);
    }

    public Empresa atualizar(Empresa empresa) {
        return entityManager.merge(empresa);
    }

    public void remover(Empresa empresa) {
        Empresa gerenciada = entityManager.contains(empresa)
                ? empresa
                : entityManager.merge(empresa);
        entityManager.remove(gerenciada);
    }

    public void removerPorId(Long id) {
        findById(id).ifPresent(this::remover);
    }

    /**
     * Indica se já existe empresa com o CNPJ informado.
     * Em edição, {@code excludeId} ignora o próprio registro.
     */
    public boolean existsByCnpj(String cnpj, Long excludeId) {
        if (cnpj == null || cnpj.isBlank()) {
            return false;
        }
        if (excludeId == null) {
            Long count = entityManager.createQuery(
                            "SELECT COUNT(e) FROM Empresa e WHERE e.cnpj = :cnpj", Long.class)
                    .setParameter("cnpj", cnpj)
                    .getSingleResult();
            return count != null && count > 0;
        }
        Long count = entityManager.createQuery(
                        "SELECT COUNT(e) FROM Empresa e WHERE e.cnpj = :cnpj AND e.id <> :excludeId",
                        Long.class)
                .setParameter("cnpj", cnpj)
                .setParameter("excludeId", excludeId)
                .getSingleResult();
        return count != null && count > 0;
    }
}
