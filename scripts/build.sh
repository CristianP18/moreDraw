cd ../

mkdir -p build-api  # Cria o diretório 'build-api' se não existir

# build REST Api
cd api/rest/src/ || exit
echo -e "\nBuilding REST Api..."

mkdir -p ../dist  # Cria o diretório 'dist' se não existir
multi-file-swagger index.yml > ../dist/index.json
cd ../dist/ || exit

# Convert REST json to yaml
echo -e "Convert REST Api json to yaml..."
json2yaml index.json > index.yml

# Minify REST json
echo -e "Minify REST Api json..."
minify-json index.json

# build HTTP Api
cd ../../http/src/ || exit
echo -e "\nBuilding HTTP Api..."
mkdir -p ../dist
multi-file-swagger index.yml > ../dist/index.json
cd ../dist/ || exit

# Convert HTTP Api json to yaml
echo -e "Convert HTTP Api json to yaml..."
json2yaml index.json > index.yml

# Minify HTTP json
echo -e "Minify HTTP Api json..."
minify-json index.json

# build Api Documentation
cd ../../doc/src/ || exit
echo -e "\nBuilding Documentation..."
mkdir -p ../dist
multi-file-swagger index.yml > ../dist/index.json
cd ../dist/ || exit

# Convert Api Documentation json to yaml
echo -e "Convert Documentation json to yaml..."
json2yaml index.json > index.yml

# Minify Documentation json
echo -e "Minify Documentation json..."
minify-json index.json

# Copy api files to build-api
cd ../../../
cp -r api build-api

mkdir -p build-lambda

# build lambdas
cd lambda/moreDraw/ || exit
echo -e "\nBuilding lambdas..."
sudo mvn clean package -q

if [ $? -eq 0 ]
then
  echo -e "\nBuild completed successfully"
  cd ../../
  find lambda/moreDraw/ -name "*.jar" -and ! -name "*original*" -print0 | xargs -0 cp -iv --parents -t build-lambda
  cp ./lambda/moreDraw/infrastructure.yml build-lambda/lambda/moreDraw/infrastructure.yml
else
  echo -e "\nError on build, removing folders..."
  cd ../../
  rm -r build-lambda
  rm -r build-api
  echo -e "\nBuild folders successfully removed"
fi
