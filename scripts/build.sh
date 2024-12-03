cd ../
mkdir build-api
# build REST Api
cd api/rest/src/ || exit
echo -e "\nBuilding REST Api..."
multi-file-swagger index.yml > ../dist/index.json
cd ../dist/ || exit

# convert REST json to yml
echo -e "Convert REST Api json to yaml..."
json2yaml index.json > index.yml

# minify REST json
echo -e "Minify REST Api json..."
minify-json index.json

# build HTTP Api
cd ../../http/src/ || exit
echo -e "\nBuilding HTTP Api..."
multi-file-swagger index.yml > ../dist/index.json
cd ../dist/ || exit

# convert HTTP Api json to yml
echo -e "Convert HTTP Api json to yaml..."
json2yaml index.json > index.yml

# minify HTTP json
echo -e "Minify HTTP Api json..."
minify-json index.json

# build Api Documentation
cd ../../doc/src/ || exit
echo -e "\nBuilding Documentation..."
multi-file-swagger index.yml > ../dist/index.json
cd ../dist/ || exit

# convert Api Documentation json to yml
echo -e "Convert Documentation json to yaml..."
json2yaml index.json > index.yml

# minify Documentation json
echo -e "Minify Documentation json..."
minify-json index.json

# copy api files to build-api
cd ../../../
cp -r api build-api

mkdir build-lambda

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