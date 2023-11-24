
. "./config.ps1"

Write-Host " "
Write-Host "*************************************************************"
Write-Host "*               Building the container image                *"
Write-Host "*************************************************************"
Write-Host " "

Set-Location ${workspacePath}

(Get-ECRLoginCommand).Password | docker login `
    --username AWS `
    --password-stdin https://${account}.dkr.ecr.${region}.amazonaws.com

docker build -q -t "${serviceLower}-${environmentLower}" --pull -f Dockerfile .

docker tag `
    "${serviceLower}-${environmentLower}:latest" `
    "${account}.dkr.ecr.${region}.amazonaws.com/${serviceLower}-${environmentLower}:latest"

Write-Host " "
Write-Host "*************************************************************"
Write-Host "*            Pushing the image to the repository            *"
Write-Host "*************************************************************"
Write-Host " "

docker push "${account}.dkr.ecr.${region}.amazonaws.com/${serviceLower}-${environmentLower}:latest"

Write-Host " "
Write-Host "*************************************************************"
Write-Host "*                 Running the maven build                   *"
Write-Host "*************************************************************"
Write-Host " "

mvn clean install -q -f "${workspacePath}/main/pom.xml"

Write-Host " "
Write-Host "*************************************************************"
Write-Host "*              Uploading the Lambda jar files               *"
Write-Host "*************************************************************"
Write-Host " "

Write-S3Object `
    -BucketName ${bucket} `
    -File "${workspacePath}/main/lambda/target/lambda-LAMBDA-SNAPSHOT.jar" `
    -Key "${service}/${environment}/lambda-${version}.jar"

Write-Host " "
