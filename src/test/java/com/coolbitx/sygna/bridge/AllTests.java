package com.coolbitx.sygna.bridge;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
    ApiTest.class, 
    CryptoTest.class, 
    EcdsaTest.class, 
    EciesTest.class, 
    ValidatorTest.class })
public class AllTests {

    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(AllTests.class);
        int counter = 1;
        while (result.getFailureCount() <= 0) {
            result = JUnitCore.runClasses(AllTests.class);
            System.out.printf("%d Run:%s\n", counter, (result.wasSuccessful()) ? "SUCCESS" : "FAIL");
            counter++;
        }

        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }
    }
}
