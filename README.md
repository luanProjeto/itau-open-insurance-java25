# Desafio Técnico Itaú — Seguro Patrimonial (Java 25)

API REST desenvolvida com **Java 25, Spring Boot 3.5.7 e arquitetura hexagonal pragmática (Ports & Adapters)** para gerenciamento de apólices de seguro patrimonial.

O projeto contempla cadastro, consulta, atualização e cancelamento lógico de apólices, além de endpoints de consulta inspirados no **Open Insurance Brasil**.

Também inclui persistência com H2 e PostgreSQL, versionamento de banco com Flyway, Docker, testes automatizados, integração contínua com GitHub Actions e arquitetura de referência para AWS.

> **Importante:** esta é uma implementação técnica e educacional, com dados fictícios. Os endpoints de Open Insurance são simplificados e não representam uma implementação certificada ou integralmente aderente aos requisitos regulatórios brasileiros.

## 1. Tecnologias utilizadas

| Tecnologia | Utilização |
|---|---|
| Java 25 | Linguagem principal |
| Spring Boot 3.5.7 | Framework da aplicação |
| Spring Web | Desenvolvimento de APIs REST |
| Spring Data JPA | Persistência |
| Hibernate | ORM e controle de versão |
| Bean Validation | Validação de dados |
| H2 Database | Banco local persistente |
| PostgreSQL | Banco relacional alternativo |
| Flyway | Versionamento do esquema SQL |
| Maven | Gerenciamento de dependências e build |
| JUnit 5 | Testes automatizados |
| Mockito | Testes unitários |
| MockMvc | Testes de endpoints |
| Swagger / OpenAPI | Documentação interativa da API |
| Spring Boot Actuator | Healthcheck |
| Docker / Docker Compose | Conteinerização e infraestrutura local |
| GitHub Actions | CI/CD |
| AWS | Arquitetura opcional de referência |

## 2. Pré-requisitos

Para executar a aplicação localmente:

- JDK **25**
- Maven **3.9+**
- Git (opcional)

Para utilizar PostgreSQL:

- Docker Desktop
- Docker Compose

Não é necessário possuir conta AWS nem credenciais corporativas para executar ou testar o projeto.

### Verificar as versões instaladas

```bash
java -version
```

```bash
mvn -version
```

O Maven deve utilizar o JDK 25.

## 3. Executar localmente com H2

O banco H2 é a configuração padrão e não exige Docker.

### Passo 1 — Clonar o repositório

```bash
git clone https://github.com/luanProjeto/itau-open-insurance-java25.git
```

```bash
cd itau-open-insurance-java25
```

### Passo 2 — Executar os testes

```bash
mvn clean verify
```

Esse comando realiza o build e executa os testes configurados no projeto.

### Passo 3 — Iniciar a aplicação

```bash
mvn spring-boot:run
```

A aplicação estará disponível em:

| Serviço | Endereço |
|---|---|
| API REST | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| Healthcheck | http://localhost:8080/actuator/health |

O H2 utiliza armazenamento em arquivo na pasta `./data`, permitindo preservar os registros entre reinicializações.

O Flyway executa as migrações SQL necessárias na inicialização.

## 4. Executar com PostgreSQL e Docker Compose

O PostgreSQL é opcional e pode ser utilizado para demonstrar persistência em um banco relacional externo.

### Passo 1 — Iniciar o PostgreSQL

Na raiz do projeto:

```bash
docker compose up -d
```

### Passo 2 — Configurar as variáveis de ambiente

**Windows PowerShell:**

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/insurance"
$env:DB_USER="insurance"
$env:DB_PASSWORD="local_dev_only"
```

**Linux ou macOS:**

```bash
export DB_URL="jdbc:postgresql://localhost:5432/insurance"
export DB_USER="insurance"
export DB_PASSWORD="local_dev_only"
```

### Passo 3 — Iniciar a aplicação

```bash
mvn spring-boot:run
```

A aplicação utilizará o PostgreSQL configurado pelas variáveis de ambiente.

**Observação:** `local_dev_only` é uma senha fictícia destinada exclusivamente ao ambiente local. Não deve ser utilizada em produção.

## 5. Endpoints REST

### 5.1 Administração de apólices

| Método | Endpoint | Descrição | Respostas |
|---|---|---|---|
| POST | `/api/v1/policies` | Cadastrar apólice | 201, 400, 409 |
| GET | `/api/v1/policies/{id}` | Consultar apólice | 200, 404 |
| GET | `/api/v1/policies?page=0&size=20` | Listar apólices | 200 |
| PUT | `/api/v1/policies/{id}` | Atualizar apólice | 200, 400, 404, 409 |
| DELETE | `/api/v1/policies/{id}` | Cancelar apólice logicamente | 204, 404, 409 |

### 5.2 Consultas inspiradas no Open Insurance Brasil

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/insurance-patrimonial?page=0&size=20` | Listar identificações de apólices |
| GET | `/insurance-patrimonial/{policyId}/policy-info` | Consultar informações da apólice |

Os endpoints são adaptações simplificadas e não reproduzem integralmente os contratos oficiais.

## 6. Exemplo de utilização

### 6.1 Criar uma apólice

Crie um arquivo chamado `policy.json`:

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

Execute:

```bash
curl -i -X POST http://localhost:8080/api/v1/policies -H "Content-Type: application/json" --data-binary @policy.json
```

**Resultado esperado:** HTTP `201 Created`, com cabeçalho `Location`.

### 6.2 Consultar uma apólice

```bash
curl -i http://localhost:8080/api/v1/policies/PAT-2026-001
```

**Resultado esperado:** HTTP `200 OK`.

### 6.3 Listar apólices

```bash
curl -i "http://localhost:8080/api/v1/policies?page=0&size=20"
```

### 6.4 Consultar pelo endpoint Open Insurance simplificado

```bash
curl -i http://localhost:8080/insurance-patrimonial/PAT-2026-001/policy-info
```

### 6.5 Listar identificações pelo Open Insurance simplificado

```bash
curl -i "http://localhost:8080/insurance-patrimonial?page=0&size=10"
```

### 6.6 Atualizar uma apólice

Altere os campos editáveis no arquivo `policy.json`, mantendo o mesmo `policyId`.

```bash
curl -i -X PUT http://localhost:8080/api/v1/policies/PAT-2026-001 -H "Content-Type: application/json" --data-binary @policy.json
```

**Resultado esperado:** HTTP `200 OK`.

### 6.7 Cancelar uma apólice

```bash
curl -i -X DELETE http://localhost:8080/api/v1/policies/PAT-2026-001
```

**Resultado esperado:** HTTP `204 No Content`.

O registro não é removido fisicamente do banco de dados. Seu status passa para `CANCELLED`.

**Nota para PowerShell:** caso `curl` seja interpretado como um alias, utilize `curl.exe` nos exemplos.

## 7. Regras de negócio

1. **Identificador único:** `policyId` é a chave primária e não pode ser alterado. A unicidade é garantida por restrição no banco de dados.

2. **Validação de vigência:** `termEndDate` deve ser maior ou igual a `termStartDate`. Essa condição também é protegida por restrição SQL.

3. **Valor máximo de garantia:** `maxLMG` deve ser maior que zero.

4. **Campos obrigatórios:** validados utilizando Bean Validation.

5. **Cancelamento lógico:** a operação DELETE altera o status para `CANCELLED`, preservando o registro.

6. **Histórico consultável:** apólices canceladas continuam disponíveis nas consultas, com seu status explícito.

7. **Imutabilidade após cancelamento:** uma apólice cancelada não pode ser atualizada ou cancelada novamente.

8. **Concorrência:** atualizações utilizam `@Version`, com optimistic locking.

9. **Tratamento de erros:** respostas HTTP apropriadas para entradas inválidas, recursos inexistentes e conflitos de negócio.

### Códigos HTTP

| Código | Significado |
|---|---|
| 200 | Operação de consulta ou atualização realizada |
| 201 | Recurso criado |
| 204 | Cancelamento realizado |
| 400 | Dados inválidos |
| 404 | Recurso não encontrado |
| 409 | Duplicidade ou conflito de estado |

## 8. Arquitetura hexagonal — Ports & Adapters

O projeto utiliza uma arquitetura hexagonal pragmática, com separação entre adaptadores HTTP, casos de uso e persistência.

```text
                  Cliente HTTP / Swagger
                            |
                            v
                  adapter.in.web
               Controllers / Exceptions
                            |
                            v
                domain.port.in.PolicyUseCase
                            ^
                            |
               application.service.PolicyService
                            |
                            v
             domain.port.out.PolicyPersistencePort
                            ^
                            |
                            |
          adapter.out.persistence.PolicyPersistenceAdapter
                            |
                            v
                    Spring Data JPA
                            |
                            v
                    H2 / PostgreSQL
```

### Organização dos pacotes

| Pacote | Responsabilidade |
|---|---|
| `domain.model` | Modelo de apólice e status |
| `domain.exception` | Exceções de domínio |
| `domain.port.in` | Contratos de entrada dos casos de uso |
| `domain.port.out` | Contratos de persistência |
| `application.service` | Regras, transações e orquestração |
| `application.dto` | Comandos e visões dos casos de uso |
| `adapter.in.web` | Controllers e tratamento de erros |
| `adapter.out.persistence` | Implementação de persistência |

### Decisões arquiteturais

**Separação de responsabilidades**

Os controllers recebem requisições HTTP e delegam a execução aos casos de uso. A aplicação utiliza uma porta de persistência, cuja implementação é fornecida pelo adaptador JPA.

**Persistência desacoplada por contrato**

O serviço de aplicação depende de `PolicyPersistencePort`, não diretamente de um repositório Spring Data.

**DTOs**

Os contratos REST não expõem diretamente a entidade JPA.

**Transações**

As operações de escrita utilizam controle transacional para manter a consistência dos dados.

**Arquitetura pragmática**

A entidade de domínio utiliza anotações JPA, e as portas empregam `Page` e `Pageable` do Spring Data. Isso reduz a complexidade do projeto, mas mantém dependências de framework.

Portanto, a implementação demonstra o padrão Ports & Adapters sem afirmar independência completa de infraestrutura.

Em um sistema de maior porte, seria recomendável separar as entidades de domínio das entidades de persistência e utilizar abstrações próprias para paginação.

## 9. Persistência e Flyway

O projeto utiliza:

- H2 persistente em arquivo como configuração padrão.
- PostgreSQL como alternativa relacional.
- Flyway para migrações e versionamento do esquema.
- Restrições SQL para unicidade e validação de vigência.
- JPA/Hibernate para mapeamento objeto-relacional.
- Controle otimista de concorrência com `@Version`.

A combinação de validações na aplicação e restrições no banco aumenta a consistência dos registros.

## 10. Testes automatizados

O projeto possui testes unitários e de integração, incluindo validação das regras de negócio e comportamento dos endpoints.

### Executar os testes

```bash
mvn clean verify
```

### Cenários verificados

- Cadastro de apólice.
- Consulta por identificador.
- Atualização de apólice.
- Cancelamento lógico.
- Rejeição de cadastro duplicado.
- Rejeição de datas inválidas.
- Consulta de recurso inexistente.
- Rejeição de atualização de apólice cancelada.
- Consultas simplificadas de Open Insurance.

Também foi realizado um teste local de concorrência com 20 requisições de cadastro do mesmo identificador, obtendo uma criação e 19 respostas de conflito.

Esse teste manual não substitui uma suíte automatizada de carga ou concorrência.

## 11. Docker

O projeto inclui um `Dockerfile` com build em múltiplos estágios utilizando Java 25.

O Docker Compose permite iniciar o PostgreSQL para testes locais.

### Iniciar os serviços definidos no Compose

```bash
docker compose up -d
```

### Consultar os containers

```bash
docker compose ps
```

### Encerrar os serviços

```bash
docker compose down
```

A imagem da aplicação também foi validada localmente, com acesso ao PostgreSQL em container.

## 12. Integração contínua e entrega contínua — CI/CD

O projeto utiliza GitHub Actions.

### CI — Java 25

Arquivo:

`.github/workflows/ci.yml`

O pipeline é executado automaticamente em:

- Push na branch `main`.
- Push na branch `develop`.
- Pull requests para `main`.
- Execução manual.

Etapas principais:

1. Checkout do código.
2. Configuração do JDK 25.
3. Execução de `mvn clean verify`.
4. Disponibilização de relatórios de testes em caso de falha.

As ações `actions/checkout` e `actions/setup-java` utilizam a versão `v5`.

**Validação no GitHub:**

| Execução | Commit | Resultado |
|---|---|---|
| CI #1 | `6db546d` | Sucesso |
| CI #2 | `624fbdc` | Sucesso |

A segunda execução concluiu em aproximadamente 23 segundos.

### CD — AWS ECS (opcional)

Arquivo:

`.github/workflows/deploy-aws.yml`

O workflow prevê:

- Disparo manual na branch `main`.
- Autenticação AWS via OIDC.
- Build da aplicação.
- Criação e publicação da imagem Docker no ECR.
- Atualização do serviço no ECS.

**Importante:** o CD depende de recursos e configurações AWS previamente existentes. Não foi executado um deploy real na AWS.

Mais detalhes em [CI-CD.md](CI-CD.md).

## 13. Arquitetura AWS — diferencial opcional

A solução apresenta uma arquitetura de referência utilizando:

| Serviço AWS | Responsabilidade |
|---|---|
| Amazon ECR | Armazenamento das imagens Docker |
| Amazon ECS Fargate | Execução da aplicação em containers |
| Amazon RDS PostgreSQL | Persistência relacional |
| AWS Secrets Manager | Gerenciamento de credenciais |
| Amazon CloudWatch Logs | Centralização de logs |
| Application Load Balancer | Distribuição de tráfego |
| IAM / OIDC | Controle de acesso e autenticação do pipeline |

### Fluxo de referência

```text
                 Cliente HTTP
                      |
                      v
             Application Load Balancer
                      |
                      v
                ECS Fargate
              API Spring Boot
                      |
                      v
               RDS PostgreSQL

GitHub Actions
      |
      v
    OIDC / IAM
      |
      v
   Amazon ECR
      |
      v
   Amazon ECS
```

O repositório contém:

- `Dockerfile`
- `application-aws.yml`
- `aws/ecs-task-definition.template.json`
- `aws/README.md`
- `.github/workflows/deploy-aws.yml`

A arquitetura é demonstrativa e não provisiona automaticamente VPC, sub-redes, grupos de segurança, ALB, RDS ou demais recursos.

**Não houve deploy real na AWS.**

Consulte [aws/README.md](aws/README.md) para instruções, limitações e considerações de
