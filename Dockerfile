FROM public.ecr.aws/lambda/java:11

# AWS base image provides the environment variable LAMBDA_TASK_ROOT i.e. /var/task
# The base image needs compiled classes in /var/task , hence we are copying the compiled files from target/classes
COPY target/classes ${LAMBDA_TASK_ROOT}
# The base image looks for runtime dependencies in /var/task/lib folder, so we copy runtime dependencies
COPY target/java/lib/* ${LAMBDA_TASK_ROOT}/lib/

# Set the CMD to your handler (could also be done as a parameter override outside of the Dockerfile)
CMD [ "com.lambda.Demo::myMethod" ]

# To build the image -> go to Dockerfile location -> docker build -t image-name .
# To run image -> docker run -p 9000:8080 image-name
# To test use this endpoint of runtime interface emulator -> Postman -> POST http://localhost:9000/2015-03-31/functions/function/invocations
