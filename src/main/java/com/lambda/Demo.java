package com.lambda;

import com.amazonaws.services.lambda.runtime.Context;
//import com.amazonaws.services.lambda.runtime.RequestHandler;
//import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
// RequestHandler and RequestStreamHandler are part of aws-lambda-java-core and is available on the class path of the deployed function.
import com.amazonaws.services.lambda.runtime.events.ApplicationLoadBalancerResponseEvent;
// This is part of aws-lambda-java-events and is NOT AVAILABLE on the class path of the deployed function.
// Hence we added maven-assembly-plugin to package the dependencies with the jar so that its available on the class path
// of the deployed function.

import java.util.HashMap;
import java.util.Map;

/**
 * Let's assume that we are ONLY concerned about Demo (and its public method)
 * Now let's compare the efforts with Lambda and without Lambda
 *
 * WITHOUT LAMBDA
 * 1. We would need to have a servlet mapping (Get/Post/Put etc)
 * 2. Then within the mapping create an instance of the class and then call the method
 * 3. We would need a server with servlet container like Jetty/Tomcat
 * 4. Then we would have to deploy the jar to the servlet
 *
 * WITH LAMBDA
 * 1. Create Lambda function, Upload the jar and specify fully qualified className::methodName
 * 2. Create a ALB and add the above function as target group
 *
 */

public class Demo {
    // We can make our class implement RequestHandler interface and its method handleRequest,
    // but its not mandatory
    //implements RequestHandler<Object, ApplicationLoadBalancerResponseEvent> {

    // We can also make our class implement RequestStreamHandler interface and its method handleRequest
    // void handleRequest(InputStream inputStream, OutputStream outputStream, Context context)


    public ApplicationLoadBalancerResponseEvent myMethod(Object o, Context context) {
        System.out.println(o);

        String body = "Hello from myFirstAWSLambdaFunction!";
        Map<String , String> headers = new HashMap<>();
        headers.put("Content-Type","text/plain");

        // ApplicationLoadBalancerResponseEvent is whats expected by the load balancer
        // We won't necessarily need ApplicationLoadBalancerResponseEvent for a Lambda function
        // which would be consumed by SDK/CLI
        ApplicationLoadBalancerResponseEvent responseEvent = new ApplicationLoadBalancerResponseEvent();
        responseEvent.setStatusCode(200);
        responseEvent.setBase64Encoded(false);
        responseEvent.setBody(body);
        responseEvent.setHeaders(headers);

        return responseEvent;
    }
}
