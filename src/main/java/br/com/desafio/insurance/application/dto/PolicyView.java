package br.com.desafio.insurance.application.dto;
import br.com.desafio.insurance.domain.model.*;
import java.math.BigDecimal;import java.time.LocalDate;
public record PolicyView(String policyId,String proposalId,String documentType,String issuanceType,LocalDate issuanceDate,LocalDate termStartDate,LocalDate termEndDate,BigDecimal maxLMG,String insuredName,PolicyStatus status) {
public static PolicyView from(Policy p){return new PolicyView(p.getPolicyId(),p.getProposalId(),p.getDocumentType(),p.getIssuanceType(),p.getIssuanceDate(),p.getTermStartDate(),p.getTermEndDate(),p.getMaxLMG(),p.getInsuredName(),p.getStatus());}
}
