# Define o caminho base onde o script será executado
#$basePath = "C:\Users\Spark\Desktop\moreDraw-DEV-PRO\backend"
$basePath = Split-Path -Parent (Get-Location)

# Navega para o diretório base
Set-Location -Path $basePath

# Cria as pastas build-api e build-lambda dentro do diretório base se não existirem
$buildApiPath = Join-Path $basePath "build-api"
$buildLambdaPath = Join-Path $basePath "build-lambda"
if (-not (Test-Path -Path $buildApiPath)) { New-Item -Path $buildApiPath -ItemType Directory -Force }
if (-not (Test-Path -Path $buildLambdaPath)) { New-Item -Path $buildLambdaPath -ItemType Directory -Force }

# Função para converter JSON para YAML e minificar o JSON
function Convert-ymlToJsonAndMinify {
    param (
        [string]$sourceJsonPath,
        [string]$destinationYamlPath
    )
    $sourceJsonFullPath = Join-Path $basePath $sourceJsonPath
    $destinationYamlFullPath = Join-Path $basePath $destinationYamlPath

    # Converte JSON para YAML
    node -e "let fs=require('fs'),yaml=require('js-yaml');let json=fs.readFileSync('$sourceJsonFullPath', 'utf8');let obj=JSON.parse(json);fs.writeFileSync('$destinationYamlFullPath', yaml.dump(obj));"


    # Minifica o JSON e substitui o arquivo original
    node -e "let fs=require('fs');let json=fs.readFileSync('$sourceJsonFullPath', 'utf8');let minified=JSON.stringify(JSON.parse(json));fs.writeFileSync('$sourceJsonFullPath', minified);"

}

# Inicia o processo de build para a REST Api
Write-Host "`nBuilding REST Api..."
Set-Location -Path (Join-Path $basePath "api\rest\src")
# Executa o comando para construir a REST Api
##onvert-JsonToYamlAndMinify "../dist/index.json" "../dist/index.yml"

# Converte JSON da REST para YAML e minifica o JSON
#Convert-JsonToYamlAndMinify "../dist/index.json" "../dist/index.yml"

Set-Location -Path $basePath

# Copia os arquivos da API para build-api
Write-Host "Copying API files to build-api..."
#Copy-Item -Path (Join-Path $basePath "api") -Destination $buildApiPath -Recurse


# Define os caminhos completos para os arquivos da API REST
$originalRestApiDistPath = "C:\Users\Spark\Desktop\moreDraw-DEV-PRO\backend\build-api\api\rest\dist\index.json"
$minifiedRestApiDistPath = "C:\Users\Spark\Desktop\moreDraw-DEV-PRO\backend\build-api\api\rest\dist\index.min.json"

# Remove o arquivo JSON original da API REST, se ele existir
if (Test-Path $originalRestApiDistPath) {
    #Remove-Item $originalRestApiDistPath -Force
}

# Renomeia o arquivo JSON minificado da API REST para substituir o original
if (Test-Path $minifiedRestApiDistPath) {
    #Rename-Item -Path $minifiedRestApiDistPath -NewName $originalRestApiDistPath
}
# Interação com o usuário para selecionar as opções de build
Write-Host "1 - Build por Módulo"
Write-Host "2 - Build do projeto inteiro"
$buildOption = Read-Host -Prompt "Selecione uma opção (1 ou 2)"

switch ($buildOption) {
    "1" {
        # Solicita ao usuário o módulo e a classe para build específico
        $modulo = Read-Host -Prompt "Insira um módulo"
        $class = Read-Host -Prompt "Insira uma classe específica se desejar"
        $pomPath = Join-Path $basePath "lambda\moreDraw\$modulo"
        if (-not [string]::IsNullOrEmpty($class)) {
            $pomPath = Join-Path $pomPath $class
        }
        $pomPath = Join-Path $pomPath "pom.xml"

        if (Test-Path -Path $pomPath) {
            Write-Host "Iniciando o build do Maven para: $pomPath"
            & mvn -f $pomPath clean package
        } else {
            Write-Host "O arquivo POM não foi encontrado em: $pomPath"
        }
    }
    "2" {

        # Inicia o processo de build para os Lambdas
        Write-Host "`nBuilding lambdas..."
        Set-Location -Path (Join-Path $basePath "lambda\moreDraw")
        # Executa o comando para construir os Lambdascd
        mvn clean package -e
        # Verifica se o build dos lambdas foi bem-sucedido
        if ($LASTEXITCODE -eq 0) {
            Write-Host "Build completed successfully"
            # Copia os arquivos .jar para a pasta build-lambda, mantendo a estrutura de diretórios
            Write-Host "Iniciando a busca e cópia dos arquivos .jar (excluindo *original*):"
            $filesCopied = 0
            Get-ChildItem -Path "$basePath\lambda\moreDraw" -Filter "*.jar" -Exclude "*original*" -Recurse | ForEach-Object {
                $relativePath = $_.FullName.Substring($basePath.Length)
                $destinationPath = Join-Path $buildLambdaPath $relativePath
                $destinationDir = [System.IO.Path]::GetDirectoryName($destinationPath)

                # Verifica e cria a estrutura de diretórios no destino se necessário
                if (-not (Test-Path -Path $destinationDir)) {
                    New-Item -Path $destinationDir -ItemType Directory -Force
                }

                Write-Host "Arquivo encontrado para cópia: $($_.FullName)"
                Copy-Item -Path $_.FullName -Destination $destinationPath -Force
                Write-Host "Copiado para: $destinationPath"
                $filesCopied++
            }

            if ($filesCopied -eq 0) {
                Write-Host "Nenhum arquivo .jar elegível para cópia foi encontrado."
            } else {
                Write-Host "$filesCopied arquivo(s) .jar copiado(s) com sucesso, mantendo a estrutura de diretórios."
            }

            Write-Host "A busca e cópia dos arquivos .jar foi concluída, mantendo a estrutura de diretórios."
        } else {
            Write-Host "Error on build, removing folders..."
#            Remove-Item -Path $buildLambdaPath, $buildApiPath -Recurse -Force
        }

    }
    default {
        Write-Host "Opção inválida. Por favor, selecione 1 ou 2."
    }
}

# Retorna para o diretório base ao final do script
Set-Location -Path $basePath
Write-Host "Script concluído."
