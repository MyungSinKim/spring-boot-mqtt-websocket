package jp.pigumer.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

public class IntervalJob {

    private static Logger log = LoggerFactory.getLogger(IntervalJob.class);
    
    @Scheduled(fixedDelay = 5000)
    public void job() {
        log.info("job");
    }

}
