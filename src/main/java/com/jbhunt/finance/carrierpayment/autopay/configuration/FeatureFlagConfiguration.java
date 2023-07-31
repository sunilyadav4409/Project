package com.jbhunt.finance.carrierpayment.autopay.configuration;

import com.jbhunt.finance.carrierpayment.autopay.feature.FeatureFlags;
import io.rollout.rox.server.Rox;
import io.rollout.rox.server.RoxOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class FeatureFlagConfiguration {

    @Bean
    public FeatureFlags featureFlags(@Value("${rollout.enabled}") boolean rolloutEnabled,
                                     @Value("${rollout.key}") String rolloutKey) {
        FeatureFlags featureFlags = new FeatureFlags();

        if (rolloutEnabled) {
            RoxOptions options = new RoxOptions.Builder()
                    .withFetchIntervalInSeconds(Duration.ofMinutes(10).toSeconds())
                    .build();

            Rox.register(featureFlags);
            Rox.setup(rolloutKey, options);
        }

        return featureFlags;
    }

}
