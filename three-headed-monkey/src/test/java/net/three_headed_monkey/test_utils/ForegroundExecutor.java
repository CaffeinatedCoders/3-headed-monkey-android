package net.three_headed_monkey.test_utils;

import java.util.concurrent.Executor;

/**
 * Used so that AndroidAnnotations @Background methods work in foreground
 */
public class ForegroundExecutor implements Executor {
    @Override
    public void execute(Runnable runnable) {
        runnable.run();
    }
}
