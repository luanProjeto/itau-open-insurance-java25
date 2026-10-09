package br.com.desafio.insurance.adapter.out.persistence;
import br.com.desafio.insurance.domain.model.Policy;
import br.com.desafio.insurance.domain.port.out.PolicyPersistencePort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public class PolicyPersistenceAdapter implements PolicyPersistencePort {
 private final PolicyJpaRepository jpa;
 public PolicyPersistenceAdapter(PolicyJpaRepository jpa) { this.jpa = jpa; }
 public boolean existsById(String id) { return jpa.existsById(id); }
 public Optional<Policy> findById(String id) { return jpa.findById(id); }
 public Page<Policy> findAll(Pageable pageable) { return jpa.findAll(pageable); }
 public Policy saveAndFlush(Policy policy) { return jpa.saveAndFlush(policy); }
}
