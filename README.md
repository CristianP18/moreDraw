# backend λ
<img src="https://img.shields.io/badge/java-v11-orange?logo=java&style=flat-square"/>
<img src="https://img.shields.io/badge/aws-v1.67.0-blue?style=flat-square&logo=amazon-aws"/>
<img src="https://img.shields.io/badge/swagger-v3.0.0-green?style=flat-square&logo=swagger"/>

## Estrutura de diretórios
### api:
- **common**: Arquivos de configurações compartilhados entre rest, http e doc.
- **schemas**: Definição de objetos de cada entidade.
- **http**: Componentes e caminhos da definição em http.
- **rest**: Componentes e caminhos da definição em rest.
- **doc**: Componentes e referências para caminhos já estabelecidos em http e rest.
- **test**: Definição em scanapi para testes de unitários de integração.

O diretório dist contido em http, rest e doc armazena os arquivos yaml e json de modo concatenado após realizar a construção. O arquivo que deve ser implantado e disponibilizado é o *doc/dist/index.yml.*

### lambda/moreDraw:
O diretório configuration detêm recursos que são compartilhados entre todas as entidades, de forma global.

Cada diretório representa uma entidade no banco de dados, adentrando um específico existem os diretórios que representam as funções lambdas, cada lambda detêm um único diretório, que por sua vez possui as classes de manipulador e fachada. Ainda no diretório da entidade, possui o configuration_entidade, que tem como finalidade prover os modelos das classes base, requisição e resposta, definir os atributos no DynamoDB, uma classe de serviço que possui finalidades gerais da determinada entidade e uma classe de repositório cuja finalidade é conectar ao banco de dados, interagir com S3 e demais serviços AWS.

### scripts:
Os diretórios apigateway, dynamodb e sns possuem scripts, em geral que interagem com os respectivos serviços AWS.

## Controle de dependências
Para gestão de dependências é utilizado o Maven, entregando um POM para cada diretório com a maioria das dependências mínimas para que as funções rodem em todas as tecnologias com sucesso.  

É muito importante que as dependências e hierarquia nos módulos hoje existentes no POM sejam atualizadas e mantidas.

## Construção e implantação

Para conseguir executar o script de `build.sh`, alguns pré requisitos são necessários:
- Ter a versão do Java11 instalada.
- Ter o Maven instalado.
- Ter o [AWS CLI](https://docs.aws.amazon.com/pt_br/cli/latest/userguide/getting-started-install.html) instalado.
- Ter o [AWS SAM](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/install-sam-cli.html) instalado.
- Ter o node em versão LTS instalado.
- Ter o npm instalado.
- Instalar o pacote **multi-file-swagger** usando `npm i -g multi-file-swagger`
- Instalar o pacote **json2yaml** usando `npm i -g json2yaml`
- Instalar o pacote **minify-json** usando `npm i -g minify-json`
- Instalar o pacote **cfn-include** usando `npm i -g cfn-include`



Para executar a construção e implantação de modo local é necessário:
```
cd backend/scripts/
./build.sh
./deploy.sh
```
Também existe um CI/CD definido e executável pelo gitlab, que está definido no arquivo .gitlab-ci.yml, responsável por orquestrar a pipeline do projeto.  

Master status: [![pipeline status](https://gitlab.com/sparkag/moreDraw/backend/badges/master/pipeline.svg)](https://gitlab.com/sparkag/moreDraw/backend/-/commits/master)

Por padrão, a pipeline vem com os seguintes estágios:
- **build**: Constrói JAR das lambdas e os move para pasta build-lambda. Instala dependências node, faz a junção dos arquivos yaml, minifica e converte para json, por fim constrói documentação da API e definição da API HTTP e REST e as move para pasta build-api.
- **package**: Realiza o empacotamento dos recursos definidos no template modelo de aplicação serverless.
- **minify**: Converte saída minificada do cloudformation para json.
- **deploy**: Implanta funções lambdas e APIs na AWS. Atualiza bucket com arquivos da especificação da API e disponibiliza [documentação da API interativa swagger.](https://admin.moreDraw.com.br/docs/)
- **destroy**: Limpa arquivos binários contidos no bucket.
# moreDraw
# moreDraw
<<<<<<< HEAD
# moreDraw
=======
>>>>>>> 5c1c2be73b56aa68fbff12c88535ad2738f22b81
