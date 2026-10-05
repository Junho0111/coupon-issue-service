package com.apiece.coupon.infrastructure.messaging;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InMemoryIssuanceWorker {

    private static final Logger log = LoggerFactory.getLogger(InMemoryIssuanceWorker.class);

    private final InMemoryIssuanceQueue inMemoryIssuanceQueue;
    private final IssuanceWriter issuanceWriter;

    private Thread workerThread;

    public InMemoryIssuanceWorker(
            InMemoryIssuanceQueue inMemoryIssuanceQueue,
            IssuanceWriter issuanceWriter
    ) {
        this.inMemoryIssuanceQueue = inMemoryIssuanceQueue;
        this.issuanceWriter = issuanceWriter;
    }

    @PostConstruct
    public void start() {
        workerThread = new Thread(this::runLoop, "issuance-worker");
        workerThread.setDaemon(true);
        workerThread.start();
    }

    private void runLoop() {
        while (!Thread.currentThread().isInterrupted()) {
            IssuanceRequested event;
            try {//메시지가 있으면
                event = inMemoryIssuanceQueue.poll();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            if (event == null) {
                continue;
            }
            try {
                issuanceWriter.write(event);//여기서 처리
            } catch (Exception e) {
                log.error(
                        "Worker write 실패: couponId={}, userId={}",
                        event.getCouponId(),
                        event.getUserId(),
                        e
                );
            }
        }
        log.info("issuance-worker 종료");
    }

    @PreDestroy // 더 이상 빈이 이 메모리에 있지 않을때 이 전에 해당 메서드를 실행시켜달라
    public void stop() {
        if (workerThread != null) {
            workerThread.interrupt();
        }
    }
}
