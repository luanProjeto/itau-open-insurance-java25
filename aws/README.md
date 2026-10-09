# AWS — implantação opcional (referência arquitetural)

O desafio **não exige AWS**. A aplicação continua executável localmente com H2 ou PostgreSQL. Os artefatos nesta pasta são um **template** de implantação, não infraestrutura provisionada nem deploy validado.

## Arquitetura proposta

Cliente HTTPS → Application Load Balancer (ACM/TLS) → ECS Fargate (Docker/Java 25) → RDS PostgreSQL privado.

Amazon ECR armazena a imagem; CloudWatch Logs recebe logs de contêiner; AWS Secrets Manager fornece DB_USER/DB_PASSWORD por injeção de segredos do ECS. CloudWatch/ALB health check em `/actuator/health`. Nenhuma credencial é colocada no Git.

A aplicação mantém sua arquitetura hexagonal: a infraestrutura AWS não altera as portas de domínio. O adaptador JPA usa PostgreSQL por JDBC.

## Construir e executar localmente

```bash
mvn clean test
docker build -t insurance-patrimonial:local .
docker run --rm -p 8080:8080 insurance-patrimonial:local
```

Sem variáveis de banco, a imagem usa H2 em arquivo dentro do contêiner, **não persistente**. Para dados persistentes, usar PostgreSQL externo e variáveis `DB_URL`, `DB_USER`, `DB_PASSWORD`. O Dockerfile depende de imagens Java 25 publicadas no registro de contêiner; confirme disponibilidade e versões antes do build.

## Preparação para AWS (passos manuais)

1. Provisionar VPC, sub-redes, security groups, ALB, target group HTTP:8080, listener HTTPS com ACM, cluster/serviço ECS Fargate e RDS PostgreSQL. Colocar RDS em sub-redes privadas e permitir porta 5432 somente a partir do security group do ECS.
2. Criar repositório ECR, grupo de logs `/ecs/insurance-patrimonial`, segredos `DB_USER` e `DB_PASSWORD` no Secrets Manager. Conceder à **execution role** permissão de leitura dos segredos e de publicação de logs, além de pull no ECR. A **task role** pode permanecer sem permissões de AWS se a aplicação só usar JDBC.
3. Fazer login no ECR, construir, marcar e enviar a imagem (substitua conta, região e repositório):

```bash
aws ecr get-login-password --region REGION | docker login --username AWS --password-stdin ACCOUNT_ID.dkr.ecr.REGION.amazonaws.com
docker build -t insurance-patrimonial:latest .
docker tag insurance-patrimonial:latest ACCOUNT_ID.dkr.ecr.REGION.amazonaws.com/insurance-patrimonial:latest
docker push ACCOUNT_ID.dkr.ecr.REGION.amazonaws.com/insurance-patrimonial:latest
```

4. Substituir os valores `REPLACE_*` em `aws/ecs-task-definition.template.json`, registrar a definição e criar/atualizar o serviço ECS com sub-redes e security groups adequados. Configurar health check do target group em `/actuator/health` e grace period de inicialização.
5. Configurar `DB_URL` com o endpoint privado do RDS; as credenciais devem ser injetadas por Secrets Manager. O Flyway cria as tabelas ao iniciar. Para produção, considerar usuário de migração separado e políticas de backup/restore.

## Segurança, custos e limitações

- **Não é um módulo Terraform completo**: o template ECS não provisiona recursos de rede, RDS, ECR ou ALB. Requer configuração AWS manual e conta com custos.
- O ALB deve terminar TLS, e o ECS deve ser acessível apenas a partir do ALB. RDS não deve ser público.
- O exemplo não implementa OAuth2/consentimento Open Insurance; endpoints administrativos não devem ficar publicamente expostos sem autenticação e autorização. Em produção, adicionar proteção antes de abrir acesso externo.
- Não foi executado deploy real na AWS; valide imagem, roles, redes e endpoints no seu ambiente.
