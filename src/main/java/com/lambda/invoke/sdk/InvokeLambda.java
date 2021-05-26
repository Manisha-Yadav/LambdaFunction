package com.lambda.invoke.sdk;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.model.ServiceException;
import com.amazonaws.util.StringUtils;

import java.nio.charset.StandardCharsets;

/**
 * As the name suggests, this class would invoke aws lambda function
 * using InvokeRequest Api of Amazon SDK
 *
 * IMPORTANT NOTE :-
 * AWS Lambda doesn't expose any HTTP/S endpoint
 * We can invoke lambda either by
 * 1. SDK (shown below)
 * 2. CLI
 *      aws lambda invoke --function-name FirstLambda --cli-binary-format raw-in-base64-out --payload ''
 *      --region us-east-2 response.json
 * 3. Invoking ApplicationLoadBalancer in front of the Lambda function
 * 4. Invoking ApiGateway in front of the Lambda function
 */
public class InvokeLambda {
    public static String execute(String functionName, String payLoad) {
        // 1. Provide function name (mandatory) and payload (optional)
        InvokeRequest invokeRequest = new InvokeRequest()
                .withFunctionName(functionName);

        if (!StringUtils.isNullOrEmpty(payLoad)) {
            invokeRequest = invokeRequest.withPayload(payLoad);
        }

        InvokeResult invokeResult = null;

        try {
            // 2. Create AWS Lambda Client
            AWSLambda awsLambda = AWSLambdaClientBuilder.standard()
                    .withCredentials(new ProfileCredentialsProvider())
                    .withRegion(Regions.US_EAST_2).build();

            // 3. INVOKE
            invokeResult = awsLambda.invoke(invokeRequest);

            return new String(invokeResult.getPayload().array(), StandardCharsets.UTF_8);
        } catch (ServiceException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
