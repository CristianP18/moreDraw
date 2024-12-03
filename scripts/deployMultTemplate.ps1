$ErrorActionPreference = "Stop"

Write-Host "Enter stage:"
Write-Host "0: DEV"
Write-Host "1: V1"

$opt = Read-Host

if ([string]::IsNullOrWhiteSpace($opt)) {
    Write-Host "Argument stage is required"
    exit 1
}

$STAGE = "DEV"
$ENV = "DEV"
if ($opt -eq "1") {
    $STAGE = "V1"
    $ENV = "PROD"
}

Write-Host "Enter deploy:"
Write-Host "0: Rest"
Write-Host "1: Http"
Write-Host "2: Rest and Http"

$optDeploy = Read-Host

Write-Host "Deploying stage $STAGE..."

$SERVICE = "moreDraw"
$AWS_ID = "170387992571"
$BUCKET = "docs.moreDraw.com.br"
$BUCKET_DEPLOY = "deploy"
$REGION = "us-east-1"

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

# Função para converter o template para JSON e minificar
function Convert-and-Minify-Template {
    param (
        [String]$TemplatePath,
        [String]$OutputPath
    )
    if (Test-Path $TemplatePath) {
        # Executa o script Node.js para converter e minificar o arquivo YAML
        node "C:\Users\Spark\Desktop\moreDraw-DEV-PRO\backend\scripts\convert-and-minify.js" $TemplatePath $OutputPath
        if (Test-Path $OutputPath) {
            Write-Host "Converted and minified template: $TemplatePath -> $OutputPath"
        } else {
            Write-Host "Error: Failed to convert and minify template."
        }
    } else {
        Write-Host "Error: Template file $TemplatePath does not exist."
    }
}


Set-Location "../build-api/api/http/dist" -ErrorAction Stop
Write-Host "`nDeploy HTTP API to S3..."
aws s3 cp ./index.json s3://$BUCKET/$BUCKET_DEPLOY/api_http.json

Set-Location "../../rest/dist" -ErrorAction Stop
Write-Host "`nDeploy REST API to S3..."
aws s3 cp ./index.json s3://$BUCKET/$BUCKET_DEPLOY/api_rest.json

Set-Location "../../../../lambda/moreDraw" -ErrorAction Stop

function Deploy-API {
    Param([string]$apiType)
    $formattedApiType = $apiType.ToUpper()
    Write-Host "`nPackaging $formattedApiType jars..."
    sam package --template-file "infra-$apiType.yml" --output-template-file "${STAGE}-output-$apiType.yml" --s3-bucket $BUCKET --s3-prefix $BUCKET_DEPLOY --region $REGION

    $outputPath = "${STAGE}-output-$apiType.json"
    Write-Host "`nMinify infrastructure template and convert to json..."
    Convert-and-Minify-Template -TemplatePath "${STAGE}-output-$apiType.yml" -OutputPath $outputPath

    Write-Host "`nRemove output infrastructure template yaml..."
    Remove-Item "${STAGE}-output-$apiType.yml"

    Write-Host "`nDeploy jars to s3..."
    sam deploy --template-file $outputPath --s3-bucket $BUCKET --s3-prefix $BUCKET_DEPLOY --stack-name "${STAGE}-${SERVICE}-$apiType" --capabilities CAPABILITY_IAM --region $REGION --parameter-overrides Stage=$STAGE AwsId=$AWS_ID Region=$REGION Env=$ENV
}


function Deploy-API {
    Param([string]$apiType)
    $formattedApiType = $apiType.ToUpper()

    # Caminho do arquivo de template original
    $templatePath = "infra_api_$apiType.yml"

    # Caminho para o arquivo de saída do SAM package
    $outputTemplatePath = "${STAGE}-output-$apiType.yml"

    Write-Host "`nPackaging $formattedApiType jars..."
    sam package --template-file $templatePath --output-template-file $outputTemplatePath --s3-bucket $BUCKET --s3-prefix "$BUCKET_DEPLOY/$STAGE" --region $REGION

    $outputJsonPath = "${STAGE}-output-$apiType.json"

    Write-Host "`nMinify infrastructure template and convert to json..."
    if (Test-Path $outputTemplatePath) {
        Convert-and-Minify-Template -TemplatePath $outputTemplatePath -OutputPath $outputJsonPath

        Write-Host "Error: YAML template file $outputTemplatePath does not exist."

    Deploy-To-S3 -FilePath $outputJsonPath  -S3Path "$BUCKET/$BUCKET_DEPLOY/$STAGE/${SERVICE}-template.json"
    # Executar o deploy SAM com o template JSON
    Write-Host "`nDeploying $formattedApiType API using SAM..."
#    aws cloudformation deploy --stack-name "${STAGE}-${SERVICE}-$apiType" --template-url $s3TemplateUrl --capabilities CAPABILITY_IAM --parameters ParameterKey=Stage,ParameterValue=$STAGE ParameterKey=AwsId,ParameterValue=$AWS_ID ParameterKey=Region,ParameterValue=$REGION ParameterKey=Env,ParameterValue=$ENV --region $REGION
        sam deploy --template-file ${STAGE}-output-$apiType.json --s3-bucket ${BUCKET} --s3-prefix ${BUCKET_DEPLOY} --stack-name "${STAGE}-${SERVICE}-$apiType" --capabilities CAPABILITY_IAM --region ${REGION} --parameter-overrides Stage=${STAGE} AwsId=${AWS_ID} Region=${REGION} Env=${ENV}
    } else {
    Write-Host "Error: Failed to create JSON template for $formattedApiType."
}
}



if ($optDeploy -eq "0" -or $optDeploy -eq "2") {
    Deploy-API -apiType "rest"
}

if ($optDeploy -eq "1" -or $optDeploy -eq "2") {
    Deploy-API -apiType "http"
}

Write-Host "`nRemove binaries from S3..."
# aws s3 rm s3://$BUCKET/$BUCKET_DEPLOY/ --recursive --quiet

Write-Host "`nRemove output infrastructure template json..."
 Remove-Item "${STAGE}-output-*.json"

Set-Location "../../build-api/api/doc/dist" -ErrorAction Stop
Write-Host "`nDeploy API documentation to S3..."
aws s3 cp ./index.yml s3://admin.moreDraw.com.br/docs/

Set-Location "../../../../"
#Remove-Item -Recurse -Force "build-lambda"
#Remove-Item -Recurse -Force "build-api"
