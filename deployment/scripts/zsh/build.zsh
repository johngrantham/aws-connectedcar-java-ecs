#!/bin/zsh

source config.zsh

echo " "
echo "*************************************************************"
echo "*            Building the container image locally           *"
echo "*************************************************************"
echo " "

cd ${workspacePath}

aws ecr get-login-password \
    --region ${region} | docker login \
    --username AWS \
    --password-stdin ${account}.dkr.ecr.${region}.amazonaws.com

docker build --build-arg TOKEN=${token} -t ${serviceLower}-${environmentLower} -f Dockerfile .

docker tag \
    ${serviceLower}-${environmentLower}\:latest \
    ${account}.dkr.ecr.${region}.amazonaws.com/${serviceLower}-${environmentLower}\:latest

echo " "
echo "*************************************************************"
echo "*            Pushing the image to the repository            *"
echo "*************************************************************"
echo " "

docker push ${account}.dkr.ecr.${region}.amazonaws.com/${serviceLower}-${environmentLower}\:latest

echo " "
echo "*************************************************************"
echo "*                 Running the maven build                   *"
echo "*************************************************************"
echo " "

mvn clean install -q -f ${workspacePath}/main/pom.xml

echo " "
echo "*************************************************************"
echo "*      Uploading the Lambda zip files to the S3 folder      *"
echo "*************************************************************"
echo " "

aws s3 cp \
    ${workspacePath}/main/lambda/target/lambda-LAMBDA-SNAPSHOT.jar \
    s3://${bucket}/${service}/${environment}/lambda-${version}.jar

echo " "
