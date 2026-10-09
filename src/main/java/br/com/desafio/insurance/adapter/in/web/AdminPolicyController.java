package br.com.desafio.insurance.adapter.in.web;

import br.com.desafio.insurance.domain.port.in.PolicyUseCase;
import br.com.desafio.insurance.application.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/policies")
public class AdminPolicyController {
    private final PolicyUseCase service;

    public AdminPolicyController(PolicyUseCase service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public PolicyView get(@PathVariable String id) {
        return service.get(id);
    }
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Apólice criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Apólice já cadastrada")
    })
    @PostMapping
    public ResponseEntity<PolicyView> create(@Valid @RequestBody PolicyCommand request) {
        PolicyView p = service.create(request);
        return ResponseEntity.created(URI.create("/api/v1/policies/" + p.policyId())).body(p);
    }

    @GetMapping
    public Page<PolicyView> list(@ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return service.list(pageable);
    }

    @PutMapping("/{id}")
    public PolicyView update(@PathVariable String id, @Valid @RequestBody PolicyCommand request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable String id) {
        service.cancel(id);
    }
}
