# api
## Defina as variáveis necessárias
#SERVICE="moreDraw"
#AWS_ID="170387992571"
#BUCKET="docs.moreDraw.com.br"
#BUCKET_DEPLOY="deploy"
#REGION="us-east-1"
#API_NAME="testApiUpdate"  # Nome da nova API a ser criada
#
## Criar uma nova API REST
#echo "Criando uma nova API REST..."
#API_ID=$(aws apigateway create-rest-api --name "${API_NAME}" --region "${REGION}" --query 'id' --output text)
#
## Verificar se a criação da API foi bem-sucedida
#if [ $? -eq 0 ]; then
#    echo "API REST criada com sucesso. ID da API: ${API_ID}"
#
#    # Obter o ID do recurso raiz da API
#    ROOT_RESOURCE_ID=$(aws apigateway get-resources --rest-api-id "${API_ID}" --query 'items[0].id' --output text)
#
#    # Gerar as informações de acesso à API
#    API_ENDPOINT="https://${API_ID}.execute-api.${REGION}.amazonaws.com"
#    API_STAGE="${REGION}/${ROOT_RESOURCE_ID}"
#
#    echo "Endpoint da API: ${API_ENDPOINT}"
#    echo "Estágio da API: ${API_STAGE}"
#else
#    echo "Falha ao criar a API REST. Verifique os logs para mais detalhes."
#fi
#


#Update Api
cd ../build-api/api/rest/dist || exit
# Obter o caminho absoluto do diretório atual
CURRENT_DIR=$(pwd)

# Definir o caminho completo para o arquivo JSON
JSON_FILE_PATH="${CURRENT_DIR}/index.json"

# Defina as variáveis necessárias
API_ID="2fsoqlamz8" # Substitua pelo ID da sua API existente
STAGE_NAME="update3" # O estágio que você deseja atualizar (por exemplo, 'dev', 'prod')

# Atualizar a API existente usando o arquivo JSON
echo "Atualizando a API..."
aws apigateway put-rest-api --rest-api-id $API_ID --mode overwrite --body "file://$JSON_FILE_PATH"

# Verificar se a atualização foi bem-sucedida
if [ $? -eq 0 ]; then
    echo "API atualizada com sucesso."

    # Criar um deployment para aplicar as mudanças
    echo "Criando deployment..."
    aws apigateway create-deployment --rest-api-id $API_ID --stage-name $STAGE_NAME

    if [ $? -eq 0 ]; then
        echo "Deployment criado com sucesso. Mudanças aplicadas ao estágio '$STAGE_NAME'."
    else
        echo "Falha ao criar o deployment. Verifique os logs para mais detalhes."
    fi
else
    echo "Falha ao atualizar a API. Verifique os logs para mais detalhes."
fi



## Define o estágio baseado em uma variável de ambiente ou padrão
#STAGE_NAME=${STAGE}
#JSON_FILE_PATH="file://$JSON_FILE_PATH" # Caminho para o arquivo JSON de documentação da API
#
## Busca o ID da API baseado no nome que inclui o estágio
#API_ID=$(aws apigateway get-rest-apis --query "items[?name=='moreDraw - REST'].id" --output text)
#
#echo "ID da API para estágio ${STAGE_NAME}: $API_ID"
#
#if [ -z "$API_ID" ]; then
#    echo "API não encontrada para o estágio ${STAGE_NAME}."
#    exit 1
#fi
#
## Atualizar a API existente usando o arquivo JSON
#echo "Atualizando a API..."
#UPDATE_RESULT=$(aws apigateway put-rest-api --rest-api-id $API_ID --mode overwrite --body "file://$JSON_FILE_PATH")
#
## Verificar se a atualização foi bem-sucedida
#if [ $? -eq 0 ]; then
#    echo "API atualizada com sucesso."
#
#    # Criar um deployment para aplicar as mudanças
#    echo "Criando deployment..."
#    DEPLOYMENT_RESULT=$(aws apigateway create-deployment --rest-api-id $API_ID --stage-name $STAGE_NAME)
#
#    if [ $? -eq 0 ]; then
#        echo "Deployment criado com sucesso. Mudanças aplicadas ao estágio '$STAGE_NAME'."
#    else
#        echo "Falha ao criar o deployment. Verifique os logs para mais detalhes."
#    fi
#else
#    echo "Falha ao atualizar a API. Verifique os logs para mais detalhes."
#fi