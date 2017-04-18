package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by alesnax on 27.01.2017.
 */
@Suite.SuiteClasses({PostValidationTest.class, UserValidationTest.class, ComplaintValidationTest.class, CategoryValidationTest.class})
@RunWith(Suite.class)
public class ValidationTest {
}