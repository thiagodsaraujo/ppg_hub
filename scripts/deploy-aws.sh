#!/bin/bash
# =============================================================================
# Script de Deploy para AWS
# =============================================================================
# Autor: PPG Hub Team
# Descrição: Deploy automatizado da aplicação PPG Hub na AWS
# Uso: ./scripts/deploy-aws.sh [environment]
# Exemplo: ./scripts/deploy-aws.sh production
# =============================================================================

set -e  # Exit on error
set -u  # Exit on undefined variable

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Funções auxiliares
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Banner
echo "========================================"
echo "  PPG Hub - Deploy AWS"
echo "========================================"
echo ""

# Verificar argumentos
ENVIRONMENT=${1:-production}
log_info "Ambiente: $ENVIRONMENT"

# Verificar se AWS CLI está instalado
if ! command -v aws &> /dev/null; then
    log_error "AWS CLI não está instalado"
    log_info "Instale: https://aws.amazon.com/cli/"
    exit 1
fi

# Verificar se o arquivo .env existe
if [ ! -f .env ]; then
    log_error "Arquivo .env não encontrado!"
    log_info "Copie .env.example para .env e configure as variáveis"
    exit 1
fi

# Carregar variáveis de ambiente
log_info "Carregando variáveis de ambiente..."
export $(cat .env | grep -v '^#' | xargs)

# Configurações AWS
AWS_REGION=${AWS_REGION:-sa-east-1}
ECR_REPOSITORY=${ECR_REPOSITORY:-ppghub}
AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
ECR_REGISTRY="${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
IMAGE_TAG="${ENVIRONMENT}-$(date +%Y%m%d-%H%M%S)"

log_info "AWS Region: $AWS_REGION"
log_info "ECR Repository: $ECR_REPOSITORY"
log_info "Image Tag: $IMAGE_TAG"

# Verificar variáveis obrigatórias
REQUIRED_VARS=("AWS_REGION" "AWS_RDS_ENDPOINT" "AWS_ELASTICACHE_ENDPOINT" "OPENALEX_EMAIL")
for var in "${REQUIRED_VARS[@]}"; do
    if [ -z "${!var:-}" ]; then
        log_error "Variável $var não está definida no .env"
        exit 1
    fi
done

log_success "Variáveis de ambiente carregadas"

# 1. Build da aplicação
log_info "Compilando aplicação..."
mvn clean package -DskipTests -Dspring.profiles.active=aws

if [ $? -eq 0 ]; then
    log_success "Build concluído com sucesso"
else
    log_error "Falha no build"
    exit 1
fi

# 2. Login no ECR
log_info "Autenticando no ECR..."
aws ecr get-login-password --region $AWS_REGION | \
    docker login --username AWS --password-stdin $ECR_REGISTRY

if [ $? -eq 0 ]; then
    log_success "Autenticado no ECR"
else
    log_error "Falha na autenticação"
    exit 1
fi

# 3. Build da imagem Docker
log_info "Construindo imagem Docker..."
docker build \
    -f Dockerfile.prod \
    -t ppghub:$IMAGE_TAG \
    -t ppghub:latest \
    .

if [ $? -eq 0 ]; then
    log_success "Imagem Docker criada"
else
    log_error "Falha na criação da imagem"
    exit 1
fi

# 4. Tag para ECR
log_info "Fazendo tag da imagem..."
docker tag ppghub:$IMAGE_TAG $ECR_REGISTRY/$ECR_REPOSITORY:$IMAGE_TAG
docker tag ppghub:latest $ECR_REGISTRY/$ECR_REPOSITORY:latest

# 5. Push para ECR
log_info "Enviando imagem para ECR..."
docker push $ECR_REGISTRY/$ECR_REPOSITORY:$IMAGE_TAG
docker push $ECR_REGISTRY/$ECR_REPOSITORY:latest

if [ $? -eq 0 ]; then
    log_success "Imagem enviada para ECR"
else
    log_error "Falha no push"
    exit 1
fi

# 6. Deploy no ECS (se configurado)
if [ ! -z "${ECS_CLUSTER:-}" ] && [ ! -z "${ECS_SERVICE:-}" ]; then
    log_info "Atualizando serviço ECS..."

    # Atualizar task definition com nova imagem
    TASK_DEFINITION=$(aws ecs describe-task-definition \
        --task-definition $ECS_SERVICE \
        --region $AWS_REGION)

    NEW_TASK_DEF=$(echo $TASK_DEFINITION | \
        jq --arg IMAGE "$ECR_REGISTRY/$ECR_REPOSITORY:$IMAGE_TAG" \
        '.taskDefinition | .containerDefinitions[0].image = $IMAGE | del(.taskDefinitionArn, .revision, .status, .requiresAttributes, .compatibilities, .registeredAt, .registeredBy)')

    # Registrar nova task definition
    NEW_TASK_INFO=$(aws ecs register-task-definition \
        --region $AWS_REGION \
        --cli-input-json "$NEW_TASK_DEF")

    NEW_REVISION=$(echo $NEW_TASK_INFO | jq -r '.taskDefinition.revision')

    # Atualizar serviço
    aws ecs update-service \
        --cluster $ECS_CLUSTER \
        --service $ECS_SERVICE \
        --task-definition ${ECS_SERVICE}:${NEW_REVISION} \
        --region $AWS_REGION

    log_success "Serviço ECS atualizado"

    # Aguardar deployment
    log_info "Aguardando deployment..."
    aws ecs wait services-stable \
        --cluster $ECS_CLUSTER \
        --services $ECS_SERVICE \
        --region $AWS_REGION

    log_success "Deployment concluído"
else
    log_warning "ECS não configurado, imagem disponível no ECR"
fi

# 7. Deploy no Elastic Beanstalk (alternativa)
if [ ! -z "${EB_APPLICATION:-}" ] && [ ! -z "${EB_ENVIRONMENT:-}" ]; then
    log_info "Fazendo deploy no Elastic Beanstalk..."

    # Criar arquivo Dockerrun.aws.json
    cat > Dockerrun.aws.json <<EOF
{
  "AWSEBDockerrunVersion": "1",
  "Image": {
    "Name": "$ECR_REGISTRY/$ECR_REPOSITORY:$IMAGE_TAG",
    "Update": "true"
  },
  "Ports": [
    {
      "ContainerPort": 8080,
      "HostPort": 8080
    }
  ],
  "Environment": [
    {
      "Name": "SPRING_PROFILES_ACTIVE",
      "Value": "aws"
    }
  ]
}
EOF

    # Criar versão da aplicação
    VERSION_LABEL="v-$IMAGE_TAG"

    zip -r app-$VERSION_LABEL.zip Dockerrun.aws.json

    aws s3 cp app-$VERSION_LABEL.zip \
        s3://$EB_APPLICATION-versions/app-$VERSION_LABEL.zip

    aws elasticbeanstalk create-application-version \
        --application-name $EB_APPLICATION \
        --version-label $VERSION_LABEL \
        --source-bundle S3Bucket="$EB_APPLICATION-versions",S3Key="app-$VERSION_LABEL.zip" \
        --region $AWS_REGION

    # Fazer deploy
    aws elasticbeanstalk update-environment \
        --environment-name $EB_ENVIRONMENT \
        --version-label $VERSION_LABEL \
        --region $AWS_REGION

    log_success "Deploy no Elastic Beanstalk iniciado"
fi

# 8. Limpar imagens locais antigas
log_info "Limpando imagens antigas..."
docker image prune -f

# Resumo do deploy
echo ""
echo "========================================"
echo "  Deploy AWS Concluído!"
echo "========================================"
echo ""
log_info "Imagem: $ECR_REGISTRY/$ECR_REPOSITORY:$IMAGE_TAG"
log_info "Tag Latest: $ECR_REGISTRY/$ECR_REPOSITORY:latest"
echo ""

if [ ! -z "${ECS_CLUSTER:-}" ]; then
    log_info "ECS Cluster: $ECS_CLUSTER"
    log_info "ECS Service: $ECS_SERVICE"
fi

if [ ! -z "${EB_APPLICATION:-}" ]; then
    log_info "Elastic Beanstalk: $EB_APPLICATION"
    log_info "Environment: $EB_ENVIRONMENT"
fi

echo ""
log_info "Para verificar logs (ECS):"
log_info "  aws logs tail /aws/ecs/${ECS_SERVICE} --follow"
echo ""
