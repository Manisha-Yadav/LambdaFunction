Write-Host "Starting upload to ECR repository"

<#Retrieve an authentication token and 
authenticate your Docker client to your registry.#>
aws ecr get-login-password --region us-east-2 | docker login --username AWS --password-stdin 799968346123.dkr.ecr.us-east-2.amazonaws.com

#Build the docker image from the folder containing Dockerfile
docker build -t 799968346123.dkr.ecr.us-east-2.amazonaws.com/lambdacontainerdemo:1.0.0 .

#Refer - https://docs.docker.com/docker-hub/repos/#pushing-a-docker-container-image-to-docker-hub

#Push image to repository
docker push 799968346123.dkr.ecr.us-east-2.amazonaws.com/lambdacontainerdemo:1.0.0

# After completion of this script, create a Lambda Function by selecting Container image in AWS console