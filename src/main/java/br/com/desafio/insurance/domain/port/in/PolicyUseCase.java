package br.com.desafio.insurance.domain.port.in;

import br.com.desafio.insurance.application.dto.PolicyCommand;
import br.com.desafio.insurance.application.dto.PolicyView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PolicyUseCase {
    PolicyView create(PolicyCommand command);

    PolicyView get(String id);

    Page<PolicyView> list(Pageable pageable);

    PolicyView update(String id, PolicyCommand command);

    void cancel(String id);
}
