
echo "Enter stage:"
echo "0: DEV"
echo "1: V2"

read opt

if [ -z "$opt" ]
  then
    echo "Argument stage is required"
    exit 1
fi

if [ "$opt" -eq "1" ]; then
    STAGE="V2"
    ENV="PROD"

else
    STAGE="DEV"
    ENV="DEV"

fi

echo "Enter deploy:"
echo "0: Rest"
echo "1: Http"
echo "2: Rest and Http"

read optDeploy

echo -e "Deploying stage ${STAGE}..."

SERVICE="moreDraw"
AWS_ID="170387992571"
BUCKET="docs.moreDraw.com.br"
BUCKET_DEPLOY="deploy"
REGION="us-east-1"
cd ../build-api/api/rest/dist || exit
# Obter o caminho absoluto do diretório atual
CURRENT_DIR=$(pwd)

# Definir o caminho completo para o arquivo JSON
JSON_FILE_PATH="${CURRENT_DIR}/index.json"

# deploy http api to s3
cd ../../http/dist || exit
echo -e "\nDeploy HTTP API to S3..."
# Substitui apenas a primeira ocorrência de 'stageVariables.Stage' pela variável de ambiente $STAGE no arquivo JSON
sed -i "0,/moreDraw - HTTP/s//${STAGE} - moreDraw - HTTP/" ./index.json
aws s3 cp ./index.json s3://${BUCKET}/${BUCKET_DEPLOY}/api_http.json

# deploy rest api to s3
cd ../../rest || exit
echo -e "\nDeploy REST API to S3..."
aws s3 cp ./index.json s3://${BUCKET}/${BUCKET_DEPLOY}/api_rest.json

cd ../../../lambda/moreDraw || exit

deploy_api_rest(){
#  package binaries
  echo -e "\nPackaging jars..."
  sam package \
      --template-file infra_api_rest.yml \
      --output-template-file ${STAGE}-output-template.yml \
      --s3-bucket ${BUCKET} \
      --s3-prefix ${BUCKET_DEPLOY} \
      --region ${REGION} \


    # minify output template
    echo -e "\nMinify infrastructure template and convert to json..."
    cfn-include ./${STAGE}-output-template.yml -m > ./${STAGE}-output.json

    # remove output template
    echo -e "\nRemove output infrastructure template yaml..."
    rm ./${STAGE}-output-template.yml

  #   deploy binaries to S3
    echo -e "\nDeploy jars to s3..."
    echo -e "Stage=${STAGE} AwsId=${AWS_ID} Region=${REGION} Env=${ENV}"
  sam deploy \
      --template-file ${STAGE}-output.json \
      --s3-bucket ${BUCKET} \
      --s3-prefix ${BUCKET_DEPLOY} \
      --stack-name ${STAGE}-${SERVICE}-rest \
      --capabilities CAPABILITY_IAM \
      --region ${REGION} \
      --parameter-overrides Stage=${STAGE} AwsId=${AWS_ID} Region=${REGION} Env=${ENV}

# Define o estágio baseado em uma variável de ambiente ou padrão
STAGE_NAME=${STAGE}
JSON_FILE_PATH="$CURRENT_DIR/index.json" # Caminho para o arquivo JSON de documentação da API

# Substitui apenas a primeira ocorrência de 'stageVariables.Stage' pela variável de ambiente $STAGE no arquivo JSON
sed -i "0,/stageVariables.Stage/s//${STAGE}/" "$JSON_FILE_PATH"


# Busca o ID da API baseado no nome que inclui o estágio
API_ID=$(aws apigateway get-rest-apis --query "items[?name=='${STAGE_NAME} - moreDraw - REST'].id" --output text)

echo "ID da API para estágio ${STAGE_NAME}: $API_ID"

if [ -z "$API_ID" ]; then
    echo "API não encontrada para o estágio ${STAGE_NAME}."
    exit 1
fi

# Atualizar a API existente usando o arquivo JSON
echo "Atualizando a API..."
UPDATE_RESULT=$(aws apigateway put-rest-api --rest-api-id $API_ID --mode overwrite --body "file://$JSON_FILE_PATH")
DEPLOYMENT_RESULT=$(aws apigateway create-deployment --rest-api-id $API_ID --stage-name $STAGE_NAME)
}

deploy_api_http(){
  # package binaries
  echo -e "\nPackaging jars..."
  sam package \
    --template-file infra_api_http.yml \
    --output-template-file ${STAGE}-output-template.yml \
    --s3-bucket ${BUCKET} \
    --s3-prefix ${BUCKET_DEPLOY} \
    --region ${REGION}

  # minify output template
  echo -e "\nMinify infrastructure template and convert to json..."
  cfn-include ./${STAGE}-output-template.yml -m > ./${STAGE}-output.json

  # remove output template
  echo -e "\nRemove output infrastructure template yaml..."
  rm ./${STAGE}-output-template.yml

  # deploy binaries to S3
  echo -e "\nDeploy jars to s3..."
  echo -e "Stage=${STAGE} AwsId=${AWS_ID} Region=${REGION} Env=${ENV}"
  sam deploy \
    --template-file ${STAGE}-output.json \
    --s3-bucket ${BUCKET} \
    --s3-prefix ${BUCKET_DEPLOY} \
    --stack-name ${STAGE}-${SERVICE}-http \
    --capabilities CAPABILITY_IAM \
    --region ${REGION} \
    --parameter-overrides Stage=${STAGE} AwsId=${AWS_ID} Region=${REGION} Env=${ENV}

}
if [[ "$optDeploy" -eq 0 || "$optDeploy" -eq 2 ]]; then
    deploy_api_rest
fi

if [[ "$optDeploy" -eq 1 || "$optDeploy" -eq 2 ]]; then
    deploy_api_http
fi

# Lista de grupos de logs que contêm /aws/lambda/
log_groups=$(aws logs describe-log-groups --query 'logGroups[?contains(logGroupName, `/aws/lambda/`)].logGroupName' --output text)

# Itera sobre cada grupo de logs
for log_group in $log_groups; do
  # Verifica se o nome do grupo de logs não contém as strings "DEV", "V1", "V2", "StatusEC2_RDS" e "Teste_parar_EC2"
  if [[ $log_group != *"DEV"* && $log_group != *"V1"* && $log_group  != *"V2"* && $log_group != *"StatusEC2_RDS"* && $log_group != *"Teste_parar_EC2"* ]]; then
    echo "Removendo grupo de logs: $log_group"
    # Remove o grupo de logs
    aws logs delete-log-group --log-group-name "$log_group"
  fi
done


# remove binaries from S3
echo -e "\nRemove binaries from S3..."
aws s3 rm s3://${BUCKET}/${BUCKET_DEPLOY}/ --recursive --quiet

# remove minify output infrestructure template
echo -e "\nRemove output infrestructure template json..."
rm ./${STAGE}-output.json

# deploy api documentation to S3
cd ../../build-api/api/doc/dist || exit
echo -e "\nDeploy API documentation to S3..."
aws s3 cp ./index.yml s3://admin.moreDraw.com.br/docs/

cd ../../../../
rm -r build-lambda
rm -r build-api