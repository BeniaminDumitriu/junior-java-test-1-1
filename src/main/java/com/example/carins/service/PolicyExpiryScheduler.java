package com.example.carins.service;

import com.example.carins.repo.InsurancePolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class PolicyExpiryScheduler {

    private static final Logger log = LoggerFactory.getLogger(PolicyExpiryScheduler.class);

    private final InsurancePolicyRepository policyRepository;

    public PolicyExpiryScheduler(InsurancePolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    // Rulează la fiecare 5 minute. Acceptance: log în max 1 oră după expirare
    @Scheduled(cron = "0 0/5 * * * *")
    public void notifyExpiringPolicies() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalDateTime expiryStart = today.atStartOfDay();
        LocalDateTime expiryCutoff = expiryStart.plusHours(1);

        // Logăm doar în fereastra de 1 oră după miezul nopții
        if (!now.isBefore(expiryStart) && now.isBefore(expiryCutoff)) {
            policyRepository.findExpiringOn(today).forEach(p -> {
                log.info("Policy {} for car {} expired on {}", p.getId(), p.getCar().getId(), p.getEndDate());
                p.setExpiryNotified(true);
                policyRepository.save(p);
            });
        }
    }
}



