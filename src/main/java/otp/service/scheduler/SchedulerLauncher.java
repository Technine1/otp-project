package otp.service.scheduler;

import java.util.concurrent.*;

public class SchedulerLauncher {
    public static void start() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(new OtpExpirationTask(), 0, 1, TimeUnit.MINUTES);
    }
}
