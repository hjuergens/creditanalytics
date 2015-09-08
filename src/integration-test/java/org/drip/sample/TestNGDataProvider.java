package org.drip.sample;

import org.testng.annotations.DataProvider;

/**
 * TODO doc for TestNGDataProvider
 *
 * @author juergens
 * @since 07.09.15
 */
public class TestNGDataProvider {
    @DataProvider(name = "mainparam", parallel = true)
    public static Object[][] mainparam() {
        return new Object[][] {
                new Object[]{ new String[]{ "" } },
        };
    }

}
