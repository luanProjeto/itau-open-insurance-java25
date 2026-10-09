# Desafio Técnico Itaú — Seguro Patrimonial (Java 25)

API REST de cadastro local de apólices patrimoniais, com consultas **inspiradas** no Open Insurance Brasil e CRUD administrativo fictício. **Não é uma implementação certificada ou regulatoriamente aderente.** Dados de demonstração são fictícios.

## Requisitos
- JDK **25** (obrigatório no desafio)
- Maven 3.9+
- Sem credenciais corporativas e sem AWS

## Executar (H2 em arquivo, sem Docker)
```bash
mvn clean test
mvn spring-boot:run
```
A API estará em `http://localhost:8080`, Swagger UI em `http://localhost:8080/swagger-ui.html` e healthcheck em `/actuator/health`. H2 cria um banco persistente na pasta `./data` automaticamente, por Flyway.

## Executar com PostgreSQL (opcional)
```bash
docker compose up -d
export DB_URL=jdbc:postgresql://localhost:5432/insurance
export DB_USER=insurance
export DB_PASSWORD=local_dev_only
mvn spring-boot:run
```
No Windows PowerShell, configure `$env:DB_URL`, `$env:DB_USER` e `$env:DB_PASSWORD` em vez de `export`. A senha do compose é **somente para desenvolvimento local**.

## Endpoints
| Método | Rota | Resultado |
|---|---|---|
| POST | `/api/v1/policies` | 201 + Location |
| GET | `/api/v1/policies/{id}` | 200 ou 404 |
| GET | `/api/v1/policies?page=0&size=20` | 200 |
| PUT | `/api/v1/policies/{id}` | 200 ou 400/404/409 |
| DELETE | `/api/v1/policies/{id}` | 204 ou 404/409 |
| GET | `/insurance-patrimonial?page=0&size=20` | 200 |
| GET | `/insurance-patrimonial/{policyId}/policy-info` | 200 ou 404 |

## Exemplo completo com curl
Crie `policy.json`:
```json
{
  "policyId": "PAT-2026-001",
  "proposalId": "PROP-2026-001",
  "documentType": "POLICY",
  "issuanceType": "NEW",
  "issuanceDate": "2026-10-08",
  "termStartDate": "2026-10-08",
  "termEndDate": "2027-10-08",
  "maxLMG": 350000.00,
  "insuredName": "Cliente Exemplo Fictício"
}
```
```bash
curl -i -X POST http://localhost:8080/api/v1/policies -H 'Content-Type: application/json' --data-binary @policy.json
curl -i http://localhost:8080/api/v1/policies/PAT-2026-001
curl -i http://localhost:8080/insurance-patrimonial/PAT-2026-001/policy-info
curl -i 'http://localhost:8080/insurance-patrimonial?page=0&size=10'
# Altere os campos editáveis em policy.json, mantendo policyId
curl -i -X PUT http://localhost:8080/api/v1/policies/PAT-2026-001 -H 'Content-Type: application/json' --data-binary @policy.json
curl -i -X DELETE http://localhost:8080/api/v1/policies/PAT-2026-001
```

## Regras de negócio
1. `policyId` é imutável e é chave primária com unicidade garantida no banco; verificação prévia melhora a mensagem, mas a restrição do banco é a garantia sob concorrência.
2. `termEndDate >= termStartDate`, também garantido por CHECK SQL.
3. `maxLMG > 0`; campos obrigatórios validados via Bean Validation.
4. DELETE faz **cancelamento lógico** (`status=CANCELLED`), nunca apaga a linha. GET e listagens continuam retornando apólices canceladas com o status explícito.
5. Não é permitido atualizar nem cancelar novamente uma apólice cancelada (`409`).
6. Controle de concorrência em atualização com `@Version` (optimistic locking).
7. Erros de payload inválido: 400; inexistência: 404; duplicidade e conflito de estado: 409.

## Decisões e trade-offs
- **Arquitetura hexagonal pragmática**: adaptadores HTTP chamam a porta de entrada `PolicyUseCase`; o serviço de aplicação implementa a porta e depende apenas da porta de saída `PolicyPersistencePort`; o adaptador JPA implementa a persistência. O domínio não depende de Controllers ou do Spring Data. A entidade mantém anotações JPA por simplicidade — trade-off documentado, não uma arquitetura hexagonal estrita.
- **H2 em arquivo por padrão**: executa imediatamente, sem infraestrutura; PostgreSQL via Docker Compose demonstra uma opção relacional de produção. Flyway versiona esquema em ambos.
- **Chave de negócio como PK**: torna a unicidade explícita e transacional. Não depende de consulta prévia vulnerável a corrida.
- **Soft delete**: preserva histórico, permite auditoria básica e mantém consultas consistentes. Em produção seria necessário um fluxo de autorização/auditoria mais robusto.
- **DTOs**: contratos REST não expõem entidade JPA diretamente.
- **Testes**: unitários de regras e integração com MockMvc, JPA e H2.

## Mapeamento para o YAML oficial
Referência: `https://raw.githubusercontent.com/br-openinsurance/areadesenvolvedor/main/documentation/source/files/swagger/current/insurance-patrimonial.yaml`.
- `GET /insurance-patrimonial` retorna identificações `policyId` e `proposalId`, paginadas com formato Spring Page; **não replica os envelopes e metadados oficiais**.
- `GET /insurance-patrimonial/{policyId}/policy-info` retorna o subconjunto: `policyId`, `proposalId`, `documentType`, `issuanceType`, `issuanceDate`, `termStartDate`, `termEndDate`, `maxLMG` e um segurado simplificado (`insuredName`). Os nomes e tipos são uma **adaptação simplificada**, não uma validação completa de conformidade ao YAML.
- `POST`, `PUT`, `DELETE` sob `/api/v1/policies` são extensões **internas fictícias**, não endpoints oficiais do Open Insurance.

## Limitações conhecidas
Sem OAuth2, consentimento, FAPI, integração real com seguradoras, eventos Kafka/SQS ou observabilidade distribuída. `insuredName` simplifica o modelo de segurado. Não há paginação máxima configurada nem regras regulatórias completas. Não usar para produção sem segurança, auditoria, revisão de modelo e testes em PostgreSQL.

## Uso de inteligência artificial
Uma ferramenta de IA foi utilizada como apoio à estruturação inicial, implementação e documentação. O responsável pela entrega deve revisar, executar os testes, compreender as decisões e ajustar o código antes de submeter.

## Arquitetura hexagonal (Ports & Adapters)

```text
HTTP / Swagger
    |
    v
adapter.in.web (controllers + tratamento de erros)
    |
    v
domain.port.in.PolicyUseCase  <-- application.service.PolicyService
                                          |
                                          v
                             domain.port.out.PolicyPersistencePort
                                          ^
                                          |
                             adapter.out.persistence.PolicyPersistenceAdapter
                                          |
                                    Spring Data JPA / SQL
```

- `domain.model`: modelo e status da apólice.
- `domain.exception`: exceções de domínio.
- `domain.port.in`: contrato de casos de uso.
- `domain.port.out`: contrato de persistência.
- `application.service`: orquestra regras, transações e persistência.
- `application.dto`: comandos e visões usados pelo caso de uso (trade-off: validações HTTP no comando para reduzir duplicação).
- `adapter.in.web`: rotas administrativas e consultas inspiradas no Open Insurance.
- `adapter.out.persistence`: implementação de saída com Spring Data JPA.

**Limitações intencionais:** o modelo de domínio usa JPA e as portas usam `Pageable`/`Page` do Spring Data; isso mantém o projeto pequeno e demonstra Ports & Adapters sem afirmar independência total de frameworks. Em um sistema maior, separaríamos entidades de domínio e persistência e criaríamos tipos próprios de paginação.

## AWS (diferencial opcional)
A aplicação oferece `Dockerfile`, perfil `application-aws.yml` e template de task ECS Fargate em `aws/`. A arquitetura de referência usa **ECR + ECS Fargate + RDS PostgreSQL + Secrets Manager + CloudWatch Logs + ALB**. Nenhum recurso AWS é necessário para testes ou execução local. Consulte [`aws/README.md`](aws/README.md) para desenho, passos manuais, segurança, custos e limitações. **Não houve deploy AWS real**; o template não provisiona a infraestrutura automaticamente.

## CI/CD (GitHub Actions)

- CI automática: `.github/workflows/ci.yml` — JDK 25, `mvn clean verify`, relatórios de testes.
- CD opcional: `.github/workflows/deploy-aws.yml` — disparo manual na `main`, OIDC, Docker, ECR e ECS.
- Configuração, permissões e limitações: [CI-CD.md](CI-CD.md).
- A integração AWS requer infraestrutura existente e não foi executada em uma conta real.
