package br.com.desafio.insurance.application.dto;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.math.BigDecimal;
public record PolicyCommand(
@NotBlank String policyId,@NotBlank String proposalId,@NotBlank String documentType,@NotBlank String issuanceType,
@NotNull LocalDate issuanceDate,@NotNull LocalDate termStartDate,@NotNull LocalDate termEndDate,
@NotNull @DecimalMin("0.01") BigDecimal maxLMG,@NotBlank String insuredName) {}
