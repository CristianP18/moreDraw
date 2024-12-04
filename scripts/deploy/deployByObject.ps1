# Solicita ao usuário para escolher o estágio
Write-Host "Enter stage:"
Write-Host "0: DEV"
Write-Host "1: V1"
$opt = Read-Host "Select option"
$STAGE = if ($opt -eq "0") { "DEV" } else { "V1" }

# Informações de Configuração
$SERVICE = "moreDraw"
$AWS_ID = "170387992571"
$BUCKET = "docs.moreDraw.com.br"
$BUCKET_DEPLOY = "deploy"
$REGION = "us-east-1"

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

# Solicita ao usuário para digitar o nome da função Lambda para deployment
Write-Host "Enter the Lambda function name for deployment:"
$lambdaName = Read-Host

# Diretórios de API
$httpApiDistPath = "../build-api/api/http/dist/index.json"
$restApiDistPath = "../build-api/api/rest/dist/index.json"
$apiDocDistPath = "../build-api/api/doc/dist/index.yml"

# Deploy da API HTTP
Deploy-To-S3 -FilePath $httpApiDistPath -S3Path "$BUCKET/$BUCKET_DEPLOY/api_http.json"

# Deploy da API REST
Deploy-To-S3 -FilePath $restApiDistPath -S3Path "$BUCKET/$BUCKET_DEPLOY/api_rest.json"

# Define o caminho onde o novo arquivo de template SAM será salvo
$moreDrawDirectory = "C:\Users\Spark\Desktop\moreDraw-DEV-PRO\backend\build-lambda\lambda\templates"
$newSamTemplatePath = Join-Path -Path "C:\Users\Spark\Desktop\moreDraw-DEV-PRO\backend\build-lambda\lambda\moreDraw\" -ChildPath "${lambdaName}_$STAGE.yml"

# Concatenar o conteúdo do arquivo 'AWSTemplateFormatVersion.yml' e o arquivo específico da função Lambda
$baseTemplatePath = Join-Path -Path $moreDrawDirectory -ChildPath "AWSTemplateFormatVersion.yml"
$lambdaTemplatePath = Join-Path -Path "$moreDrawDirectory" -ChildPath "$lambdaName.yml"

# Verifica se o arquivo da função Lambda existe, se não, saia
if (!(Test-Path $lambdaTemplatePath)) {
    Write-Host "Error: No template file found for function $lambdaName."
    exit
}

# Concatenar e salvar no diretório especificado
(Get-Content $baseTemplatePath) + "`n" + (Get-Content $lambdaTemplatePath) | Set-Content $newSamTemplatePath
Write-Host "New SAM template created at: $newSamTemplatePath"

# Continua com o processo de empacotamento e deploy
$samOutputTemplatePath = Join-Path -Path $moreDrawDirectory -ChildPath "${lambdaName}_$STAGE-output-template.yml"

sam package --template-file $newSamTemplatePath --output-template-file $samOutputTemplatePath --s3-bucket $BUCKET --s3-prefix "$BUCKET_DEPLOY/$STAGE/$lambdaName" --region $REGION

if (Test-Path $samOutputTemplatePath) {
    $lambdaList = @()

    # Pesquisar pelos arquivos de template com base no nome fornecido pelo usuário
    $matchingFiles = Get-ChildItem -Path $moreDrawDirectory -Filter "*$lambdaName*" -File

    foreach ($file in $matchingFiles) {
        $lambdaList += @{
            Name = $file.BaseName
            Location = "LocationFor$($file.BaseName)"  # Ajuste conforme necessário
        }
    }

    # Baixar o template SAM do S3
    $s3TemplatePath = "s3://$BUCKET/deploy/$STAGE/moreDraw-template.yml"
    aws s3 cp $s3TemplatePath .\moreDraw-template.yml

    # Iterar sobre a lista de lambdas para atualizar o atributo Location no template SAM
    foreach ($lambda in $lambdaList) {
        $templateContent = Get-Content .\moreDraw-template.yml -Raw
        $templateContent = $templateContent -replace "(Location:\s+)(.*)$($lambda.Name)(.*)", "`$1 $($lambda.Location) `$3"
        $templateContent | Set-Content .\moreDraw-template.yml
    }

    # Fazer upload do template atualizado de volta para o S3
    aws s3 cp .\moreDraw-template.yml $s3TemplatePath

    # Remover o arquivo temporário
    Remove-Item .\moreDraw-template.yml

    Write-Host "SAM template updated and uploaded to: $s3TemplatePath"

    # Deploy do template atualizado
    aws cloudformation deploy --template-file $s3TemplatePath --stack-name "moreDraw-stack" --capabilities CAPABILITY_IAM CAPABILITY_AUTO_EXPAND --region $REGION --parameter-overrides AwsId=$AWS_ID Stage=$STAGE Region=$REGION Env=$STAGE
    Write-Host "Deployment successful for moreDraw-stack"
} else {
    Write-Host "Error: SAM output template does not exist after packaging."
}
