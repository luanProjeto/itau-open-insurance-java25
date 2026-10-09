package br.com.desafio.insurance.application.service;

import br.com.desafio.insurance.application.dto.*;
import br.com.desafio.insurance.domain.model.*;
import br.com.desafio.insurance.domain.exception.*;
import br.com.desafio.insurance.domain.port.in.PolicyUseCase;
import br.com.desafio.insurance.domain.port.out.PolicyPersistencePort;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PolicyService implements PolicyUseCase {
    private final PolicyPersistencePort repository;

    public PolicyService(PolicyPersistencePort repository) {
        this.repository = repository;
    }

    @Transactional
    public PolicyView create(PolicyCommand r) {
        validate(r);
        if (repository.existsById(r.policyId())) throw new ConflictException("Apólice já cadastrada");
        try {
            return PolicyView.from(repository.saveAndFlush(new Policy(r.policyId(), r.proposalId(), r.documentType(), r.issuanceType(), r.issuanceDate(), r.termStartDate(), r.termEndDate(), r.maxLMG(), r.insuredName())));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Apólice já cadastrada");
        }
    }

    @Transactional(readOnly = true)
    public PolicyView get(String id) {
        return PolicyView.from(find(id));
    }

    @Transactional(readOnly = true)
    public Page<PolicyView> list(Pageable pageable) {
        return repository.findAll(pageable).map(PolicyView::from);
    }

    @Transactional
    public PolicyView update(String id, PolicyCommand r) {
        validate(r);
        if (!id.equals(r.policyId())) throw new IllegalArgumentException("policyId é imutável");
        Policy p = find(id);
        ensureActive(p);
        p.update(r.proposalId(), r.documentType(), r.issuanceType(), r.issuanceDate(), r.termStartDate(), r.termEndDate(), r.maxLMG(), r.insuredName());
        return PolicyView.from(repository.saveAndFlush(p));
    }

    @Transactional
    public void cancel(String id) {
        Policy p = find(id);
        ensureActive(p);
        p.cancel();
        repository.saveAndFlush(p);
    }

    private Policy find(String id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Apólice não encontrada: " + id));
    }

    private void ensureActive(Policy p) {
        if (p.getStatus() == PolicyStatus.CANCELLED)
            throw new ConflictException("Apólice cancelada não pode ser alterada");
    }

    private void validate(PolicyCommand r) {
        if (r.termEndDate().isBefore(r.termStartDate()))
            throw new IllegalArgumentException("Fim da vigência anterior ao início");
    }
}
