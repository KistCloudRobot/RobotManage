package net.ion.mdk.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

public abstract class CyclicTaskScheduler<TASK extends CyclicTask> {

    private static final Logger logger = LoggerFactory.getLogger(CyclicTaskScheduler.class);
    private final CyclicTask.JQLRepository<TASK> taskRepo;
    private final boolean DEBUG = false;
    private int MIN_INTERVAL = DEBUG ? 2 * 60 : 2 * 60_000;

    public CyclicTaskScheduler(CyclicTask.JQLRepository<TASK> taskRepo) {
        this.taskRepo = taskRepo;
    }

    protected void setMinTaskInterval(int milliSecond) {
        if (milliSecond < 100) {
            throw new RuntimeException("Illegal task interval: " + milliSecond);
        }
        this.MIN_INTERVAL = MIN_INTERVAL;
    }

    protected void startScheduler() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(MIN_INTERVAL);
                        executeTasks(false);
                    }
                    catch (InterruptedException e) {
                        return;
                    }
                }
            }
        }, "CyclicTaskScheduler").start();
    }

    /**
     * second, minute, hour, day of month, month, and day of week.
     * 매일 새벽 1시에 실행. (Application 실행 시 DefaultTimeZone 을 설정할 것)
     */
    @Scheduled(cron = "0 0 1 * * *")
    void executeDailyTasks() {
        executeTasks(true);
    }

    private void executeTasks(boolean isDaily) {
        LocalDateTime now = LocalDateTime.now();
        for (TASK task : taskRepo.list()) {
            if (isDaily != task.isDailyTask()) {
                continue;
            }
            LocalDateTime last_t = task.getLastExecutionTs();
            if (last_t != null) {
                LocalDateTime next_t = last_t.plus(task.calcInterval());
                if (!DEBUG && next_t.isAfter(now)) continue;
            }

            try {
                executeTask(task);
            }
            catch (Exception e) {
                logger.error(this.getClass().getName() + " " + e.getMessage(), e);
            }
            task.setLastExecutionTs(now);
            taskRepo.update(task);
        }
    }

    protected abstract void executeTask(TASK task) throws Exception;


    public CyclicTask.JQLRepository<TASK> getRepository() {
        return this.taskRepo;
    }
}
