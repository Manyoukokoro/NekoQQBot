package org.nekotori.job;

import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
public class DelayTaskJob {

    private static final DelayQueue<DelayTask> DELAY_TASKS = new DelayQueue<>();
    private static final ExecutorService parallelTaskService = Executors.newFixedThreadPool(5);

    public abstract static class DelayTask implements Delayed {

        private final Long expireTime;
        private String taskName;

        public abstract void run();

        public DelayTask(Integer timeForSeconds) {

            this.expireTime = timeForSeconds * 1000L + System.currentTimeMillis();
        }

        public DelayTask(String taskName, Integer timeForSeconds) {
            this.taskName = taskName;
            this.expireTime = timeForSeconds * 1000L + System.currentTimeMillis();
        }


        @Override
        public long getDelay(@NotNull TimeUnit unit) {
            return expireTime - System.currentTimeMillis();
        }

        @Override
        public int compareTo(@NotNull Delayed o) {
            if (this.getDelay(TimeUnit.MILLISECONDS) > o.getDelay(TimeUnit.MILLISECONDS)) {
                return 1;
            } else {
                return -1;
            }
        }

        @Override
        public String toString() {
            return taskName + " : " + expireTime.toString();
        }
    }


    public void addDelayTask(DelayTask delayTask) {
        DELAY_TASKS.offer(delayTask);
    }

    @Scheduled(cron = "0/10 * * * * ? ")
    public void runTask() {
        if (DELAY_TASKS.isEmpty()) return;
        DelayTask delayTask = DELAY_TASKS.poll();
        while (delayTask != null) {
            DelayTask finalDelayTask = delayTask;
            parallelTaskService.execute(finalDelayTask::run);
            delayTask = DELAY_TASKS.poll();
        }
    }
}