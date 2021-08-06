package org.nekotori.job;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledJob {




    @Scheduled(cron = "0 0 3 * * ? ")
    public void registerGroupInfo(){
    }
}
