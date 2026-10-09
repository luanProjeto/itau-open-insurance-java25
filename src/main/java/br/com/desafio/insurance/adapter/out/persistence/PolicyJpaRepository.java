package br.com.desafio.insurance.adapter.out.persistence;
import br.com.desafio.insurance.domain.model.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
interface PolicyJpaRepository extends JpaRepository<Policy,String> {}
