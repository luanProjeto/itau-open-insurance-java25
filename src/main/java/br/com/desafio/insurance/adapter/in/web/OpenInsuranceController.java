package br.com.desafio.insurance.adapter.in.web;

import br.com.desafio.insurance.domain.port.in.PolicyUseCase;
import br.com.desafio.insurance.application.dto.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.*;

@RestController
@RequestMapping("/insurance-patrimonial")
public class OpenInsuranceController {
    private final PolicyUseCase service;

    public OpenInsuranceController(PolicyUseCase service) {
        this.service = service;
    }

    @GetMapping
    public Page<PolicyIdentification> list(@PageableDefault(size = 20) Pageable pageable) {
        return service.list(pageable).map(p -> new PolicyIdentification(p.policyId(), p.proposalId()));
    }

    @GetMapping("/{policyId}/policy-info")
    public PolicyView policyInfo(@PathVariable String policyId) {
        return service.get(policyId);
    }

    public record PolicyIdentification(String policyId, String proposalId) {
    }
}
