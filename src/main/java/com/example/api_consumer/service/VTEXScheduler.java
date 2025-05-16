package com.example.api_consumer.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class VTEXScheduler {

    private final VTEXService vtexService;

    public VTEXScheduler(VTEXService vtexService) {
        this.vtexService = vtexService;
    }

    /**
     * Se ejecuta cada hora.
     * ajustar fixedRate o usar cron:
     */
    @Scheduled(fixedRateString = "${vtex.scheduler.rate:3600000}")
    public void runEveryHour() {
        vtexService.processProductRouteData();
    }

}
