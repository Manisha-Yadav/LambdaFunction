package com.lambda.invoke.sdk;

import junit.framework.TestCase;

public class InvokeLambdaTest extends TestCase {

    public void testExecute() {
        System.out.println(InvokeLambda.execute("FirstLambda", null));
    }
}