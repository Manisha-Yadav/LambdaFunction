package com.lambda;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import junit.framework.TestCase;
import org.junit.Test;


public class DynamoDbTriggeredLambdaTest extends TestCase {

    @Test
    public void testhandleRequest(){
        String email = "manishayadav266199@gmail.com";
        String name = "XYZ";
        System.out.println("Start Sending email ");
        AmazonSimpleEmailService client =
                AmazonSimpleEmailServiceClientBuilder.standard()
                        .withRegion(Regions.US_EAST_2).build();

        String HTMLBODY = "<h1>Welcoming new User</h1>"
                + "<p>This email was sent with <a href='https://aws.amazon.com/ses/'>"
                + "Amazon SES</a> using the <a href='https://aws.amazon.com/sdk-for-java/'>"
                + "AWS SDK for Java</a>";

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(
                        new Destination().withToAddresses(email))
                .withMessage(new Message()
                        .withBody(new Body()
                                .withHtml(new Content()
                                        .withCharset("UTF-8").withData(HTMLBODY))
                                .withText(new Content()
                                        .withCharset("UTF-8").withData("Welcome to the best website ever")))
                        .withSubject(new Content()
                                .withCharset("UTF-8").withData("Hi "+name)))
                .withSource(email);
                //.withConfigurationSetName("ConfigSet");
        System.out.println("Sending email to "+ email);
        //SendEmailResult result = client.sendEmail(request);
    }

    @Test
    public void testupdateDynamoDBTable(){
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_EAST_2)
                .build();

        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("userCount");

        Item item = table.getItem(new PrimaryKey("status","Active"));
        Integer oldCount = item.getInt("count");
        System.out.println("oldCount " + oldCount);

        Item newItem = new Item().withPrimaryKey("status","Active")
                .with("count",oldCount + 1);
        table.putItem(newItem);

    }

}