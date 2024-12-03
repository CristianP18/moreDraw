# Solicita ao usuário para escolher o estágio
Write-Host "Enter stage:"
Write-Host "0: DEV"
Write-Host "1: V1"
$opt = Read-Host "Select option"

# Define os estágios com base na escolha do usuário
if ($opt -eq "0") {
    $STAGE = "DEV"
    $ENV = "DEV"
} elseif ($opt -eq "1") {
    $STAGE = "V1"
    $ENV = "PROD"
} else {
    Write-Host "Invalid option. Exiting..."
    exit
}

# Informações de Configuração
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

# Diretórios de API
$httpApiDistPath = "../build-api/api/http/dist/index.json"
$restApiDistPath = "../build-api/api/rest/dist/index.json"
$apiDocDistPath = "../api/doc/dist/index.json"

# Deploy da API HTTP
Deploy-To-S3 -FilePath $httpApiDistPath -S3Path "$BUCKET/$BUCKET_DEPLOY/api_http.json"

# Deploy da API REST
Deploy-To-S3 -FilePath $restApiDistPath -S3Path "$BUCKET/$BUCKET_DEPLOY/api_rest.json"

# Caminho para infraestrutura do SAM
$samTemplatePath = "../lambda/$SERVICE/infrastructure.yml"
$samOutputTemplatePath = "../build-lambda/lambda/$SERVICE/${STAGE}-output-template.yml"
$samOutputJsonPath = "../build-lambda/lambda/$SERVICE/${STAGE}-output.json"

# Verifica e empacota arquivos JAR se o template SAM existir
if (Test-Path $samTemplatePath) {
    # Empacota os artefatos e carrega o template do SAM para o S3
    sam package --template-file $samTemplatePath --output-template-file $samOutputTemplatePath --s3-bucket $BUCKET --s3-prefix "$BUCKET_DEPLOY/$STAGE" --region $REGION

    # Verifica se o arquivo de template de saída existe após o empacotamento
    if (Test-Path $samOutputTemplatePath) {
        # Converte e minifica o template para JSON
        Convert-and-Minify-Template -TemplatePath $samOutputTemplatePath -OutputPath $samOutputJsonPath

        # Verifica se o arquivo JSON de saída foi criado
        if (Test-Path $samOutputJsonPath) {
            # Informa ao usuário sobre o próximo passo
            Write-Host "Deploying SAM application from $samOutputJsonPath..."

            #            Realiza o upload do template SAM original e do JSON para o S3 no mesmo local
            Deploy-To-S3 -FilePath $samOutputTemplatePath -S3Path "$BUCKET/$BUCKET_DEPLOY/$STAGE/${SERVICE}-template.yml"
#            Deploy-To-S3 -FilePath $samOutputJsonPath -S3Path "$BUCKET/$BUCKET_DEPLOY/$STAGE/${SERVICE}-template.json"

            # Realiza o deploy do template SAM do S3
            aws cloudformation update-stack --stack-name "DEV-moreDraw" --template-url https://s3.amazonaws.com/docs.moreDraw.com.br/deploy/DEV/moreDraw-template.json --capabilities CAPABILITY_IAM CAPABILITY_AUTO_EXPAND --parameters ParameterKey=Stage,ParameterValue=DEV ParameterKey=AwsId,ParameterValue=170387992571 ParameterKey=Region,ParameterValue=us-east-1 ParameterKey=Env,ParameterValue=DEV --region us-east-1

            # Deploy direto no terminal
#            aws cloudformation update-stack --stack-name "DEV-moreDraw" --template-url "https://s3.us-east-1.amazonaws.com/docs.moreDraw.com.br/deploy/DEV/moreDraw-template.yml" --capabilities CAPABILITY_IAM CAPABILITY_AUTO_EXPAND --parameters ParameterKey=Stage,ParameterValue=DEV ParameterKey=AwsId,ParameterValue=170387992571 ParameterKey=Region,ParameterValue=us-east-1 ParameterKey=Env,ParameterValue=DEV --region us-east-1


            aws cloudformation deploy --stack-name "AN-moreDraw" --template-url "https://s3.us-east-1.amazonaws.com/docs.moreDraw.com.br/deploy/DEV/moreDraw-template.yml" --capabilities CAPABILITY_IAM CAPABILITY_AUTO_EXPAND --parameters ParameterKey=Stage,ParameterValue=DEV ParameterKey=AwsId,ParameterValue=170387992571 ParameterKey=Region,ParameterValue=us-east-1 ParameterKey=Env,ParameterValue=AN --region us-east-1



        } else {
            Write-Host "Error: Falha ao converter e minificar o template SAM."
        }
    } else {
        Write-Host "Error: Template de saída do SAM não existe após o empacotamento."
    }
} else {
    Write-Host "Error: O template SAM em $samTemplatePath não existe."
}

# Deploy da documentação da API
Deploy-To-S3 -FilePath $apiDocDistPath -S3Path "admin.moreDraw.com.br/docs/index.json"

# Limpeza de binários e templates
#Remove-Item "../build-lambda" -Recurse -Force -ErrorAction SilentlyContinue
#Remove-Item "../build-api" -Recurse -Force -ErrorAction SilentlyContinue



#aws cloudformation update-stack --stack-name "DEV-moreDraw" --template-url https://s3.amazonaws.com/docs.moreDraw.com.br/deploy/DEV/moreDraw-template.yml --capabilities CAPABILITY_IAM CAPABILITY_AUTO_EXPAND --parameters ParameterKey=Stage,ParameterValue=DEV ParameterKey=AwsId,ParameterValue=170387992571 ParameterKey=Region,ParameterValue=us-east-1 ParameterKey=Env,ParameterValue=DEV --region us-east-1

