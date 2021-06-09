package com.lambda;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.StreamRecord;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;

import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;

/**
 * PRE-REQUISITE
 * 1. Enable DynamoDb Stream : Table Overview -> Dynamo Db Stream Details -> Select view type (New, Old, New and Old, Key only)
 * 2. Add Triggers : Table -> Triggers -> Create Trigger
 *
 * DynamoDbTriggeredLambda.handleRequest Lambda function
 *          1. Event Source Mapping polls for DynamodbEvent
 *          2. And then synchronously calls Lambda function.
 *          3. processes the request to get email id of newly added user
 *          4. a. send email to using SES(pre-requisite - add a verified email address as source)
 */

public class DynamoDbTriggeredLambda implements RequestHandler<DynamodbEvent, String> {

    private AmazonSimpleEmailService emailService = AmazonSimpleEmailServiceClientBuilder.standard()
            .withRegion(Regions.US_EAST_2).build();

    @Override
    public String handleRequest(DynamodbEvent dynamodbEvent, Context context) {

        List<DynamodbStreamRecord> records = dynamodbEvent.getRecords();

        for (DynamodbStreamRecord rec : records) {

            if("INSERT".equals(rec.getEventName())){
                StreamRecord record = rec.getDynamodb();
                System.out.println("Record Details " + record.getNewImage().toString());
                Map<String, AttributeValue> map = record.getNewImage();
                String userEmail = ofNullable(map.get("email")).orElseThrow(RuntimeException::new).getS();
                String userName = ofNullable(map.get("userName")).orElseThrow(RuntimeException::new).getS();
                System.out.println("userEmail "+ userEmail + "userName " + userName );
                sendEmail(userEmail, userName );
                return "Email sent";
            }
        }
        return "Not an insert event";
    }

    private String sendEmail(String email, String name) {
        System.out.println("Start Sending email ");

        String htmlBody = "<h1>Welcoming new User</h1>";

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(
                        new Destination().withToAddresses(email))
                .withMessage(new Message()
                        .withBody(new Body()
                                .withHtml(new Content()
                                        .withCharset("UTF-8").withData(htmlBody))
                                .withText(new Content()
                                        .withCharset("UTF-8").withData("Welcome to the best website ever")))
                        .withSubject(new Content()
                                .withCharset("UTF-8").withData("Hi " + name)))
                .withSource(email);
            System.out.println("Sending email to "+ email);
        SendEmailResult result = emailService.sendEmail(request);
        return result.getMessageId();
    }
}
