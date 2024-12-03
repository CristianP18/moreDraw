#!/usr/bin/env bash
set -e

echo "Enter stage:"
echo "0: DEV"
echo "1: V1"

read opt

if [ -z "$opt" ]
  then
    echo "Argument stage is required"
    exit 1
fi

if [ "$opt" -eq "1" ]; then
    STAGE="V1"
    ENV="PROD"
else
    STAGE="DEV"
    ENV="DEV"
fi

echo -e "Deploying stage ${STAGE}..."

SERVICE="moreDraw"
AWS_ID="170387992571"
BUCKET="moredraw"
BUCKET_DEPLOY="deploy"
REGION="us-east-1"

## deploy http api to s3
#cd ../build-api/api/http/dist || exit
#echo -e "\nDeploy HTTP API to S3..."
#aws s3 cp ./index.json s3://${BUCKET}/${BUCKET_DEPLOY}/api_http.json

## deploy rest api to s3
#cd ../../rest/dist || exit
#echo -e "\nDeploy REST API to S3..."
#aws s3 cp ./index.json s3://${BUCKET}/${BUCKET_DEPLOY}/api_rest.json

cd ../lambda || exit

# package binaries
echo -e "\nPackaging jars..."
sam package \
  --template-file infra_api_rest.yml \
  --output-template-file ${STAGE}-output-template.yml \
  --s3-bucket ${BUCKET} \
  --s3-prefix ${BUCKET_DEPLOY} \
  --region ${REGION}

# minify output template
echo -e "\nMinify infrastructure template and convert to json..."
cfn-flip ./${STAGE}-output-template.yml ./${STAGE}-output.json


# remove output template
echo -e "\nRemove output infrastructure template yaml..."
rm ./${STAGE}-output-template.yml

# deploy binaries to S3
echo -e "\nDeploy jars to s3..."
echo -e "Stage=${STAGE} AwsId=${AWS_ID} Region=${REGION} Env=${ENV}"
sam deploy \
  --template-file ${STAGE}-output.json \
  --s3-bucket ${BUCKET} \
  --s3-prefix ${BUCKET_DEPLOY} \
  --stack-name ${STAGE}-${SERVICE} \
  --capabilities CAPABILITY_IAM \
  --region ${REGION} \
  --parameter-overrides Stage=${STAGE} AwsId=${AWS_ID} Region=${REGION} Env=${ENV}


# remove binaries from S3
echo -e "\nRemove binaries from S3..."
aws s3 rm s3://${BUCKET}/${BUCKET_DEPLOY}/ --recursive --quiet

# remove minify output infrestructure template
echo -e "\nRemove output infrestructure template json..."
rm ./${STAGE}-output.json

# deploy api documentation to S3
cd ../../../build-api/api/doc/dist || exit
echo -e "\nDeploy API documentation to S3..."
aws s3 cp ./index.yml s3://admin.moreDraw.com.br/docs/

cd ../../../../
rm -r build-lambda
rm -r build-api