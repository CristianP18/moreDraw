cd C:\Users\Spark\Desktop\moreDraw-DEV-PRO\backend\scheduled\lambda
mvn install


# Solicita ao usuário para escolher o estágio
Write-Host "Enter stage:"
Write-Host "0: DEV"
Write-Host "1: PROD"
$opt = Read-Host "Select option"
$STAGE = if ($opt -eq "0") { "DEV" } elseif ($opt -eq "1") { "PROD" } else { "UNKNOWN" }

# Se o estágio não for reconhecido, terminar o script
if ($STAGE -eq "UNKNOWN") {
    Write-Host "Invalid option selected. Exiting..."
    exit
}

# Função para realizar deploy no S3 e verificar a existência do arquivo
function Deploy-To-S3 {
    param (
        [String]$FilePath,
        [String]$S3Path
    )
    if (Test-Path $FilePath) {
        aws s3 cp $FilePath s3://$S3Path
        Write-Host "Uploaded $FilePath to $S3Path"
    } else {
        Write-Host "File $FilePath does not exist."
    }
}

# Informações de Configuração
$SERVICE = "moreDraw"
$AWS_ID = "170387992571"
$BUCKET = "docs.moreDraw.com.br"
$BUCKET_DEPLOY = "deploy"
$REGION = "us-east-1"
$index = "C:\Users\Spark\Desktop\moreDraw-DEV-PRO\backend\scheduled\lambda\docs\index.yml"
# Solicita ao usuário para digitar o nome da função Lambda para deploy
Write-Host "Enter the Lambda function name for deployment:"
$lambdaName = Read-Host

# Deploy da API HTTP
Deploy-To-S3 -FilePath $index -S3Path "$BUCKET/$BUCKET_DEPLOY/index.yml"

# Define o caminho para o template SAM local
$templateLocalPath = "C:\Users\Spark\Desktop\moreDraw-DEV-PRO\backend\scheduled\lambda\infrastructure.yml"
# Define o caminho onde o template SAM empacotado será salvo
$samOutputTemplatePath = "C:\Users\Spark\Desktop\moreDraw-DEV-PRO\backend\scheduled\lambda\${STAGE}-${lambdaName}-packaged-template.yml"

# Verifica se o template SAM local existe
if (-Not (Test-Path $templateLocalPath)) {
    Write-Host "Error: Template file not found at $templateLocalPath"
    exit
}

# Empacota os artefatos e carrega o template SAM para o S3
sam package --template-file $templateLocalPath --output-template-file $samOutputTemplatePath --s3-bucket $BUCKET --s3-prefix "$BUCKET_DEPLOY/$STAGE/$lambdaName" --region $REGION

# Verifica se o arquivo de template de saída existe após o empacotamento
if (Test-Path $samOutputTemplatePath) {
    Write-Host "SAM packaging complete. File located at: $samOutputTemplatePath"

    # Realiza o deploy do template SAM do S3
    $s3TemplateUrl = "https://$BUCKET.s3.$REGION.amazonaws.com/$BUCKET_DEPLOY/$STAGE/${lambdaName}-template.yml"
    Write-Host "Deploying SAM application from $s3TemplateUrl..."

    aws cloudformation deploy --template-file C:\Users\Spark\Desktop\moreDraw-DEV-PRO\backend\scheduled\lambda\$STAGE-getTestApis-packaged-template.yml --stack-name "${STAGE}-${lambdaName}" --capabilities CAPABILITY_IAM CAPABILITY_AUTO_EXPAND --parameter-overrides Stage=$STAGE AwsId=$AWS_ID Region=$REGION Env=$STAGE

} else {
    Write-Host "Error: Packaged SAM template does not exist after packaging."
}
## Continuação do script PowerShell existente
#
## Criar API HTTP
#$apiResponse = aws apigatewayv2 create-api --name "${STAGE}-${lambdaName}-api" --protocol-type 'HTTP' --region $REGION | ConvertFrom-Json
#$apiId = $apiResponse.ApiId
#Write-Host "Created HTTP API with ID: $apiId"
#
#
## Criar a integração da Lambda com a API HTTP
#$integrationResponse = aws apigatewayv2 create-integration --api-id $apiId --integration-type AWS_PROXY --integration-uri "arn:aws:lambda:${REGION}:${AWS_ID}:function:${lambdaName}" --payload-format-version '2.0' --region $REGION | ConvertFrom-Json
#
#$integrationId = $integrationResponse.IntegrationId
#Write-Host "Created integration with ID: $integrationId"
#
## Criar a rota para a integração
#$routeKey = "ANY /{proxy+}" # Altere conforme necessário, "ANY /{proxy+}" permite qualquer método HTTP e caminho
#$routeResponse = aws apigatewayv2 create-route --api-id $apiId --route-key $routeKey --target integrations/$integrationId --region $REGION | ConvertFrom-Json
#$routeId = $routeResponse.RouteId
#Write-Host "Created route with ID: $routeId"
#
## Criar o deployment da API
#$deploymentResponse = aws apigatewayv2 create-deployment --api-id $apiId --region $REGION | ConvertFrom-Json
#$deploymentId = $deploymentResponse.DeploymentId
#Write-Host "Created deployment with ID: $deploymentId"
#
## Associar o deployment a um stage
#$stageName = "prod" # Ou use $STAGE para usar o mesmo que a função Lambda
#$stageResponse = aws apigatewayv2 create-stage --api-id $apiId --deployment-id $deploymentId --stage-name $stageName --region $REGION | ConvertFrom-Json
#Write-Host "Created stage: $stageName for API $apiId"
#
## Final do script PowerShell

