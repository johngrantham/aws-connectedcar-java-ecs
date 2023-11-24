
. "./config.ps1"

Write-Host " "
Write-Host "*************************************************************"
Write-Host "*               Uploading the OpenAPI files                 *"
Write-Host "*************************************************************"
Write-Host " "

$admin = Get-Content `
    "${workspacePath}/deployment/containers/apis/admin.openapi.yaml",`
    "${workspacePath}/deployment/containers/apis/schemas.openapi.yaml" `
    | Out-String

$vehicle = Get-Content `
    "${workspacePath}/deployment/containers/apis/vehicle.openapi.yaml",`
    "${workspacePath}/deployment/containers/apis/schemas.openapi.yaml" `
    | Out-String

$customer = Get-Content `
    "${workspacePath}/deployment/containers/apis/customer.openapi.yaml",`
    "${workspacePath}/deployment/containers/apis/schemas.openapi.yaml" `
    | Out-String

Write-S3Object `
    -BucketName ${bucket} `
    -Content $admin `
    -Key "${service}/${environment}/admin.openapi.yaml"

Write-S3Object `
    -BucketName ${bucket} `
    -Content $vehicle `
    -Key "${service}/${environment}/vehicle.openapi.yaml"

Write-S3Object `
    -BucketName ${bucket} `
    -Content $customer `
    -Key "${service}/${environment}/customer.openapi.yaml"

Write-Host " "
Write-Host "*************************************************************"
Write-Host "*       Uploading the CloudFormation template files         *"
Write-Host "*************************************************************"
Write-Host " "

Write-S3Object `
    -BucketName ${bucket} `
    -File "${workspacePath}/deployment/containers/templates/network.yaml" `
    -Key "${service}/${environment}/network.yaml"

Write-S3Object `
    -BucketName ${bucket} `
    -File "${workspacePath}/deployment/containers/templates/services.yaml" `
    -Key "${service}/${environment}/services.yaml"

Write-S3Object `
    -BucketName ${bucket} `
    -File "${workspacePath}/deployment/containers/templates/containers.yaml" `
    -Key "${service}/${environment}/containers.yaml"

Write-S3Object `
    -BucketName ${bucket} `
    -File "${workspacePath}/deployment/containers/templates/apis.yaml" `
    -Key "${service}/${environment}/apis.yaml"

$templateBody = Get-Content -Path "${workspacePath}/deployment/containers/templates/master.yaml" -raw

if (-Not (Test-CFNStack -Region ${region} -StackName "${service}${environment}"))
{
  Write-Host " "
  Write-Host "*************************************************************"
  Write-Host "*            Executing the create stack command             *"
  Write-Host "*************************************************************"
  Write-Host " "

  New-CFNStack `
      -Region ${region} `
      -StackName "${service}${environment}" `
      -TemplateBody $templateBody `
      -Parameter @( @{ ParameterKey="BucketName"; ParameterValue="${bucket}" }, `
                    @{ ParameterKey="ServiceName"; ParameterValue="${service}" }, `
                    @{ ParameterKey="ServiceNameLower"; ParameterValue="${serviceLower}" }, `
                    @{ ParameterKey="EnvironmentName"; ParameterValue="${environment}" }, `
                    @{ ParameterKey="EnvironmentNameLower"; ParameterValue="${environmentLower}" }, `
                    @{ ParameterKey="VersionNumber"; ParameterValue="${version}" }, `
                    @{ ParameterKey="StageName"; ParameterValue="${stage}" }, `
                    @{ ParameterKey="UserPoolDomainName"; ParameterValue="${domain}" }) `
                    @{ ParameterKey="CpuArchitectureName"; ParameterValue="${cpu}" }) `
      -Capability CAPABILITY_IAM,CAPABILITY_NAMED_IAM,CAPABILITY_AUTO_EXPAND

  Wait-CFNStack `
      -Region ${region} `
      -StackName "${service}${environment}" `
      -Status CREATE_COMPLETE,ROLLBACK_COMPLETE `
      -Timeout 1200
}
else {
  Write-Host " "
  Write-Host "*************************************************************"
  Write-Host "*            Executing the update stack command             *"
  Write-Host "*************************************************************"
  Write-Host " "

  $domain = ((Get-CFNStack `
      -StackName "${service}${environment}").Outputs `
      | Where-Object {$_.OutputKey -EQ 'UserPoolDomainName'}).OutputValue

  Update-CFNStack `
      -Region ${region} `
      -StackName "${service}${environment}" `
      -TemplateBody $templateBody `
      -Parameter @( @{ ParameterKey="BucketName"; ParameterValue="${bucket}" }, `
                    @{ ParameterKey="ServiceName"; ParameterValue="${service}" }, `
                    @{ ParameterKey="ServiceNameLower"; ParameterValue="${serviceLower}" }, `
                    @{ ParameterKey="ServiceNameLower"; ParameterValue="${serviceLower}" }, `
                    @{ ParameterKey="EnvironmentName"; ParameterValue="${environment}" }, `
                    @{ ParameterKey="EnvironmentNameLower"; ParameterValue="${environmentLower}" }, `
                    @{ ParameterKey="VersionNumber"; ParameterValue="${version}" }, `
                    @{ ParameterKey="StageName"; ParameterValue="${stage}" }, `
                    @{ ParameterKey="UserPoolDomainName"; ParameterValue="${domain}" }) `
                    @{ ParameterKey="CpuArchitectureName"; ParameterValue="${cpu}" }) `
      -Capability CAPABILITY_IAM,CAPABILITY_NAMED_IAM,CAPABILITY_AUTO_EXPAND

  Wait-CFNStack `
      -Region ${region} `
      -StackName "${service}${environment}" `
      -Status UPDATE_COMPLETE,ROLLBACK_COMPLETE `
      -Timeout 1200 
}

Write-Host " "
Write-Host "*************************************************************"
Write-Host "*                Listing the stack outputs                  *"
Write-Host "*************************************************************"
Write-Host " "

$outputs = (Get-CFNStack -Region ${region} -StackName "${service}${environment}").Outputs

Write-Output $outputs

Write-Host " "
