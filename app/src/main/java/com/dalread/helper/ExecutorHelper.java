package com.dalread.helper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorHelper {
    private ExecutorService executorService;

    public ExecutorHelper() {
        // 사용 가능한 CPU 코어 수를 가져옵니다.
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        System.out.println("Available processors: " + availableProcessors);

        // 스레드 수를 CPU 코어 수로 설정합니다.
        executorService = Executors.newFixedThreadPool(availableProcessors);
    }

    public void executeTask(Runnable task) {
        executorService.execute(task);
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
