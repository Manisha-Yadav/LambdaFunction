package com.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;

// 1. The input will be of type SQSEvent because this function will be triggered from SQS

/** Adding SQS as Trigger for Lambda
 *  1. Configure Lambda function triggers under 'Lambda triggers' for the SQS queue that you want as trigger
 *  2. 'Add Trigger' on the Function overview page of your function
 *
 *  IAM Policies :-
 *      Make sure you have AWSLambdaSQSQueueExecutionRole added to the IAM Role associated to the
 *      Lambda function
 *      In case of stream AWSLambdaKinesisExecutionRole for Stream
 *
 *  Invoking Lambda EVENT SOURCE MAPPING
 *  1. SQS Trigger - Success - Lambda deletes the message from SQS queue
 *                 - Failure/Error -  Lambda service continues to process the failed message until
 *                                      a. message is successfully processed
 *                                      b. message retention period is reached and SQS deletes it
 *                                      c. SQS sends the message to Dead Letter queue (SQS DLQ)
 *                 - Lambda Service polls SQS, once it gets a message from SQS, it calls the Lambda function SYNCHRONOUSLY
 *                 - Therefore, error handling and retries are not managed by Lambda.
 *                 - Error handling and retries for failure has to be managed by SQS in this case
 *                 - Best Practice (https://zaccharles.medium.com/reproducing-the-sqs-trigger-and-lambda-concurrency-limit-issue-f4c09d384a18)
 *  2. AWS CLI (ASYNCHRONOUS call)
 *     - Command - aws lambda invoke --function-name FirstLambda  --invocation-type Event --payload '' --region us-east-2 response.json
 *     - Success
 *     - Failure - To prevent loss of message, we need to configure 'Asynchronous invocation' under configuration in Lambda function
 *                 Select Dead-letter queue service (Lambda DLQ) to dump messages that fail after configured retry attempts.
 *               - Message is sent to Lambda DLQ
 *
 *  SQS DLQ & Lambda DLQ are different.
 */
public class SqsSynchronouslyTriggeredLambda implements RequestHandler<SQSEvent, String> {

    @Override
    public String handleRequest(SQSEvent sqsEvent, Context context) {
        StringBuilder sb = new StringBuilder();
        for (SQSMessage messages : sqsEvent.getRecords()) {
            // Read the message
            String body = messages.getBody();
            // Simulating failure
            if (body.contains(" ")) {
                System.out.format("Message with body ' %s ' failed!!", body);
                throw new RuntimeException("Spaces not allowed in the body");
            }
            sb.append(body);
            sb.append(" ^ ");
            // PLEASE NOTE:
            // We don't need to explicitly delete the messages because
            // Lambda does that automatically
        }
        String output = sb.toString();
        System.out.println(output);
        return output;
    }
}
