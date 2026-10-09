CREATE TABLE policies (
 policy_id VARCHAR(80) PRIMARY KEY,
 proposal_id VARCHAR(80) NOT NULL,
 document_type VARCHAR(40) NOT NULL,
 issuance_type VARCHAR(40) NOT NULL,
 issuance_date DATE NOT NULL,
 term_start_date DATE NOT NULL,
 term_end_date DATE NOT NULL,
 max_lmg NUMERIC(18,2) NOT NULL,
 insured_name VARCHAR(160) NOT NULL,
 status VARCHAR(20) NOT NULL,
 version BIGINT,
 CONSTRAINT uk_policy_id UNIQUE (policy_id),
 CONSTRAINT ck_valid_term CHECK (term_end_date >= term_start_date),
 CONSTRAINT ck_positive_lmg CHECK (max_lmg > 0)
);
