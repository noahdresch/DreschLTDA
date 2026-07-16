package com.onboardingrsd.empresas.service;

import java.util.List;
import java.util.Optional;

import com.onboardingrsd.empresas.model.Empresa;
import com.onboardingrsd.empresas.repository.EmpresaRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class EmpresaService {

    @Inject
    private EmpresaRepository empresaRepository;

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Empresa> listar() {
        return empresaRepository.findAll();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Empresa> pesquisar(String filtro) {
        return empresaRepository.findByFiltro(filtro);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<Empresa> buscarPorId(Long id) {
        return empresaRepository.findById(id);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public boolean existeCnpj(String cnpj, Long excludeId) {
        return empresaRepository.existsByCnpj(cnpj, excludeId);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void cadastrar(Empresa empresa) {
        empresaRepository.salvar(empresa);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void atualizar(Empresa empresa) {
        empresaRepository.atualizar(empresa);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void excluir(Long id) {
        empresaRepository.removerPorId(id);
    }
}
