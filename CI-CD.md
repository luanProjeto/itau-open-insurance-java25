# CI/CD — GitHub Actions e AWS

## CI automática

O workflow `.github/workflows/ci.yml` roda em push para `main` ou `develop`, pull request para `main` e execução manual. Usa Temurin JDK 25, cache Maven, `mvn clean verify` e publica relatórios de teste em caso de falha. Não precisa de conta AWS.

## CD opcional (manual)

O workflow `.github/workflows/deploy-aws.yml` só pode ser iniciado manualmente a partir de `main`, executa testes, constrói imagem Docker, publica no Amazon ECR e atualiza um serviço existente no Amazon ECS. Não cria infraestrutura. A execução exige ambiente AWS previamente provisionado, role OIDC com permissões mínimas e configuração do repositório GitHub.

Configure **GitHub Actions Variables** (preferencialmente no environment `production`):

- `AWS_ROLE_ARN`: ARN da role IAM para GitHub OIDC, com trust policy restrita ao repositório/ambiente.
- `AWS_REGION`: região AWS do ECR/ECS.
- `ECR_REPOSITORY`: nome do repositório ECR existente.
- `ECS_TASK_FAMILY`: família da task definition já registrada.
- `ECS_CONTAINER_NAME`: nome exato do contêiner na task definition.
- `ECS_CLUSTER`: nome do cluster existente.
- `ECS_SERVICE`: nome do serviço existente.

O environment `production` deve exigir aprovação humana. A role IAM deve ter apenas as permissões necessárias para ECR, ECS, `iam:PassRole` das roles da task e consulta/registro de task definition. OIDC evita guardar chaves AWS estáticas no GitHub.

## Fluxo

`git push / PR -> checkout -> Java 25 -> Maven verify -> [aprovação e execução manual] -> OIDC -> ECR -> ECS Fargate`

## Limitações

Não há provisionamento automático de VPC, ALB, RDS, ECR ou ECS. O deploy é opcional, não foi executado em conta AWS, e os workflows devem ser validados no GitHub antes de uso real. O perfil local com H2 continua independente da AWS. O pipeline não substitui monitoramento, rollback, scans de segurança ou estratégia de migração de banco para produção.
