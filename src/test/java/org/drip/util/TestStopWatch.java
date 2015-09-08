package org.drip.util;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TODO doc for StopWatch
 *
 * @author juergens
 * @since 08.09.15
 */
final public class TestStopWatch implements ITestListener {
    private static final Logger logger = LoggerFactory.getLogger(TestStopWatch.class);
    private final StopWatch stopWatch = new StopWatch();

    @Override
    public void onTestStart(ITestResult result) {
        stopWatch.start();
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        stopWatch.stop();
        logger.info(result.getName() + " runs " +stopWatch.getNanoTime() + " nanos");
        stopWatch.reset();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        stopWatch.reset();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        stopWatch.reset();
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        stopWatch.stop();
    }

    @Override
    public void onStart(ITestContext context) {    }

    @Override
    public void onFinish(ITestContext context) {    }
}
