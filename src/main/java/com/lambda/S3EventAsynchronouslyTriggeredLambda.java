package com.lambda;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import java.util.List;

/**
 * To add S3 event as a Lambda Function trigger
 *  1. Create Event notification for S3 bucket with destination as the Lambda Function
 *  2. Attach S3 access policies to Lambda Function IAM role (Configuration -> Permissions)
 *
 *  Use case :- Uploading a file to S3 bucket would trigger our Lambda Function
 *              IF the file name contains 'special' , we process it
 *              ELSE we throw a RuntimeException , the Event notification is sent to DLQ after failed retries
 *
 *  S3 triggers Lambda function ASYNCHRONOUSLY
 *
 *  Since S3 calls/invokes lambda, a RESOURCE-BASED POLICY is created automatically (in lambda)
 *  when we add S3 as the trigger, which allows
 *
 *  Principal
 *     s3.amazonaws.com
 *  Effect
 *      Allow
 *  Action
 *      lambda:InvokeFunction
 * Conditions
 *  {
 *      "StringEquals": {
 *          "AWS:SourceAccount": "799968346123"
 *      },
 *      "ArnLike": {
 *          "AWS:SourceArn": "arn:aws:s3:::mpylambdatrigger2"
 *      }
 *  }
 *
 *
 *
 */
public class S3EventAsynchronouslyTriggeredLambda implements RequestHandler<S3Event, String> {

    @Override
    public String handleRequest(S3Event s3Event, Context context) {
        StringBuilder result = new StringBuilder();
        List<S3EventNotificationRecord> eventNotifications = s3Event.getRecords();


        for (S3EventNotificationRecord record : eventNotifications) {
            String key = record.getS3().getObject().getKey();
            if(!key.contains("special")){
                /*
                Case 1 :-
                    You have configured Retry attempts and DLQ under Configuration -> Asynchronous invocation
                        then, if an event fails all attempts or stays in the asynchronous invocation queue for too long,
                        lambda sends it to the DLQ
                 Case 2 :-
                    Destination defined on the function under Function Overview -> Add Destination
                        then, if an event fails all attempts or stays in the asynchronous invocation queue for too long,
                        lambda sends it to the DESTINATION (SQS, SNS, Another Lambda or Event Bridge Bus)
                 */
                throw new RuntimeException("Not a valid file");
            }else {
                try {
                    String bucketName = record.getS3().getBucket().getName();
                    AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_2).build();
                    /*
                    In the following statements lambda would be reading from S3 (reverse of the S3 triggered lambda)
                    Therefore we would need to add below policy to lambda's role
                        {
                            "Sid": "LambdaToS3",
                            "Effect": "Allow",
                            "Action": [
                                "s3:GetObject"
                            ],
                            "Resource": [
                                "arn:aws:s3:::mpylambdatrigger2/*"
                            ]
                        }
                     */
                    S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, key));
                    S3ObjectInputStream objectData = object.getObjectContent();

                    // Process the objectData stream.
                    System.out.println(new String(objectData.readAllBytes()));

                } catch (Exception e) {
                    e.printStackTrace();
                }
                result.append(key);
            }
        }
        return result.toString();
    }
}
