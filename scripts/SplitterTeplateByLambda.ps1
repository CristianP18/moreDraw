
# Caminho do arquivo de origem
$arquivoOriginal = "C:\Users\Spark\Desktop\moreDraw-DEV-PRO\backend\lambda\moreDraw\infraestructure.yml"
New-Item -ItemType Directory -Path "C:\Users\Spark\Desktop\moreDraw-DEV-PRO\backend\build-lambda\lambda\templates"

#Divide a infraestructure por lambda e salva cada arquivo com o nome da lambda
# Ler o conteúdo do arquivo original
$textoOriginal = Get-Content -Path $arquivoOriginal -Raw

# Diretório de destino para os novos arquivos
$diretorioDestino = "C:\Users\Spark\Desktop\moreDraw-DEV-PRO\backend\build-lambda\lambda\templates"

# Inicializar variáveis para controle
$partes = @()
$bloco = ""

# Loop para percorrer o texto e dividir em partes
foreach ($linha in $textoOriginal -split "`n") {
        if ($linha -match '^\s*(get|post|delete|patch)') {
                if ($bloco -ne "") {
                        $partes += $bloco
                        $bloco = ""
                }
        }
        $bloco += "$linha`n"
}

# Adicionar o último bloco
if ($bloco -ne "") {
        $partes += $bloco
}

# Loop para salvar as partes em novos arquivos
for ($i = 0; $i -lt $partes.Count; $i++) {
        # Extrai a primeira palavra até o ':' de cada bloco
        $primeiraPalavra = if ($partes[$i] -match '^\s*([a-zA-Z]+)') {
                $matches[1]
        } else {
                "Parte$($i+1)"
        }
        # Formata o nome do arquivo usando a palavra extraída
        $nomeArquivo = "${primeiraPalavra}.yml"
        $caminhoDestino = Join-Path -Path $diretorioDestino -ChildPath $nomeArquivo
        $partes[$i] | Set-Content -Path $caminhoDestino
        Write-Host "Arquivo $nomeArquivo criado com sucesso."
}




#______________________________________________________________//________________________________________________//



#Divide a infraestructure por lambda e salva cada arquivo com o nome template1 ,2 ,3 assim por diante

 #>
## Caminho do arquivo de origem
##        $arquivoOriginal = "C:\Users\Spark\Desktop\moreDraw-DEV-PRO\backend\build-lambda\lambda\moreDraw\moreDraw-template.yml"
#$arquivoOriginal = "C:\Users\Spark\Desktop\moreDraw-DEV-PRO\backend\build-lambda\lambda\moreDraw\infraestructure.yml"
#
#        # Ler o conteúdo do arquivo original
#        $textoOriginal = Get-Content -Path $arquivoOriginal -Raw
#
#        # Diretório de destino para os novos arquivos
#        $diretorioDestino = "C:\Users\Spark\Desktop\moreDraw-DEV-PRO\backend\build-lambda\lambda\moreDraw\templates"
#
#        # Determinar o número de partes
#        $numeroDePartes = 8
#        $partesPorArquivo = [math]::Ceiling($textoOriginal.Length / $numeroDePartes)
#
#        # Inicializar variáveis para controle
#        $partes = @()
#$inicio = 0
#        $bloco = ""
#
#        # Loop para percorrer o texto e dividir em partes
#        foreach ($linha in $textoOriginal -split "`n") {
#        if ($linha -match '^\s*(get|post|delete|patch)') {
#        if ($bloco -ne "") {
#        $partes += $bloco
#        $bloco = ""
#        }
#        }
#        $bloco += "$linha`n"
#        }
#
#        # Adicionar o último bloco
#        if ($bloco -ne "") {
#        $partes += $bloco
#        }
#
#        # Loop para salvar as partes em novos arquivos
#        for ($i = 0; $i -lt $partes.Count; $i++) {
#        $numeroArquivo = $i + 1
#        $nomeArquivo = "template$numeroArquivo.yml"
#        $caminhoDestino = Join-Path -Path $diretorioDestino -ChildPath $nomeArquivo
#        $partes[$i] | Set-Content -Path $caminhoDestino
#        Write-Host "Arquivo $nomeArquivo criado com sucesso."
#        }
