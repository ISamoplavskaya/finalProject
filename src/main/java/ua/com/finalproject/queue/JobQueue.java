package ua.com.finalproject.queue;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
@Getter
@Slf4j
public class JobQueue {
    private final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(100);
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    public void put(Runnable task) {
        try {
            queue.put(task);
            log.info("Task added to the queue");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Scheduled(fixedRate = 3L, timeUnit = TimeUnit.SECONDS)
    public void executeQueue() {
        while (!queue.isEmpty()) {
            try {
                executor.execute(queue.take());
                log.info("Task executed");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
