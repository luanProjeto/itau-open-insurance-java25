package br.com.desafio.insurance;

import br.com.desafio.insurance.application.dto.*;
import br.com.desafio.insurance.domain.model.*;
import br.com.desafio.insurance.domain.exception.*;
import br.com.desafio.insurance.domain.port.out.*;
import br.com.desafio.insurance.application.service.*;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PolicyServiceTest {
    PolicyPersistencePort repository = mock(PolicyPersistencePort.class);
    PolicyService service = new PolicyService(repository);

    PolicyCommand request() {
        return new PolicyCommand("P-1", "PR-1", "POLICY",
                "NEW", LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2027, 1, 1),
                new BigDecimal("1000.00"), "Pessoa Fictícia");
    }

    @Test
    void rejectsInvalidTerm() {
        PolicyCommand r = request();
        PolicyCommand invalid = new PolicyCommand(r.policyId(), r.proposalId(), r.documentType(),
                r.issuanceType(), r.issuanceDate(), r.termEndDate(), r.termStartDate(), r.maxLMG(),
                r.insuredName());
        assertThrows(IllegalArgumentException.class, () -> service.create(invalid));
    }

    @Test
    void rejectsDuplicate() {
        when(repository.existsById("P-1")).thenReturn(true);
        assertThrows(ConflictException.class, () -> service.create(request()));
    }

    @Test
    void preventsUpdateOfCancelledPolicy() {
        PolicyCommand r = request();
        Policy p = new Policy(r.policyId(), r.proposalId(), r.documentType(), r.issuanceType(),
                r.issuanceDate(), r.termStartDate(), r.termEndDate(), r.maxLMG(), r.insuredName());
        p.cancel();
        when(repository.findById("P-1")).thenReturn(Optional.of(p));
        assertThrows(ConflictException.class, () -> service.update("P-1", r));
    }

    @Test
    void preventsIdentifierChange() {
        assertThrows(IllegalArgumentException.class, () -> service.update("OTHER", request()));
    }
}
