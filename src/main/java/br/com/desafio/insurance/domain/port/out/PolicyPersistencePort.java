package br.com.desafio.insurance.domain.port.out;
import br.com.desafio.insurance.domain.model.Policy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
public interface PolicyPersistencePort {
 boolean existsById(String id);
 Optional<Policy> findById(String id);
 Page<Policy> findAll(Pageable pageable);
 Policy saveAndFlush(Policy policy);
}
