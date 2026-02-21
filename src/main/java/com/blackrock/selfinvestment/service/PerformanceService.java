package com.blackrock.selfinvestment.service;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.text.DecimalFormat;
import java.time.Duration;

import org.springframework.stereotype.Service;

import com.blackrock.selfinvestment.models.PerformanceResponseDTO;

@Service
public class PerformanceService {
    public PerformanceResponseDTO generateReport(long startTime) {

        // -------- Response Time --------
        long durationMs = System.currentTimeMillis() - startTime;

        Duration duration = Duration.ofMillis(durationMs);

        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        long millis = duration.toMillisPart();

        String formattedTime = String.format(
                "%02d:%02d:%02d.%03d",
                hours, minutes, seconds, millis
        );

        // -------- Memory --------
        Runtime runtime = Runtime.getRuntime();

        long usedMemoryBytes =
                runtime.totalMemory() - runtime.freeMemory();

        double usedMemoryMB =
                usedMemoryBytes / (1024.0 * 1024.0);

        DecimalFormat df = new DecimalFormat("0.00");

        String formattedMemory =
                df.format(usedMemoryMB);

        // -------- Threads --------
        ThreadMXBean threadMXBean =
                ManagementFactory.getThreadMXBean();

        int threadCount = threadMXBean.getThreadCount();

        return new PerformanceResponseDTO(
                formattedTime,
                formattedMemory,
                threadCount
        );
    }
}
