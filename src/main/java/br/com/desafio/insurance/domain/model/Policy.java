package br.com.desafio.insurance.domain.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "policies", uniqueConstraints = @UniqueConstraint(name = "uk_policy_id", columnNames = "policy_id"))
public class Policy {
    @Id
    @Column(name = "policy_id", nullable = false, updatable = false, length = 80)
    private String policyId;
    @Column(name = "proposal_id", nullable = false, length = 80)
    private String proposalId;
    @Column(name = "document_type", nullable = false, length = 40)
    private String documentType;
    @Column(name = "issuance_type", nullable = false, length = 40)
    private String issuanceType;
    @Column(name = "issuance_date", nullable = false)
    private LocalDate issuanceDate;
    @Column(name = "term_start_date", nullable = false)
    private LocalDate termStartDate;
    @Column(name = "term_end_date", nullable = false)
    private LocalDate termEndDate;
    @Column(name = "max_lmg", nullable = false, precision = 18, scale = 2)
    private BigDecimal maxLMG;
    @Column(name = "insured_name", nullable = false, length = 160)
    private String insuredName;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PolicyStatus status;
    @Version
    private Long version;

    protected Policy() {
    }

    public Policy(String policyId, String proposalId, String documentType, String issuanceType, LocalDate issuanceDate, LocalDate termStartDate, LocalDate termEndDate, BigDecimal maxLMG, String insuredName) {
        this.policyId = policyId;
        this.status = PolicyStatus.ACTIVE;
        update(proposalId, documentType, issuanceType, issuanceDate, termStartDate, termEndDate, maxLMG, insuredName);
    }

    public void update(String proposalId, String documentType, String issuanceType, LocalDate issuanceDate, LocalDate termStartDate, LocalDate termEndDate, BigDecimal maxLMG, String insuredName) {
        this.proposalId = proposalId;
        this.documentType = documentType;
        this.issuanceType = issuanceType;
        this.issuanceDate = issuanceDate;
        this.termStartDate = termStartDate;
        this.termEndDate = termEndDate;
        this.maxLMG = maxLMG;
        this.insuredName = insuredName;
    }

    public void cancel() {
        this.status = PolicyStatus.CANCELLED;
    }

    public String getPolicyId() {
        return policyId;
    }

    public String getProposalId() {
        return proposalId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public String getIssuanceType() {
        return issuanceType;
    }

    public LocalDate getIssuanceDate() {
        return issuanceDate;
    }

    public LocalDate getTermStartDate() {
        return termStartDate;
    }

    public LocalDate getTermEndDate() {
        return termEndDate;
    }

    public BigDecimal getMaxLMG() {
        return maxLMG;
    }

    public String getInsuredName() {
        return insuredName;
    }

    public PolicyStatus getStatus() {
        return status;
    }

    public Long getVersion() {
        return version;
    }
}
