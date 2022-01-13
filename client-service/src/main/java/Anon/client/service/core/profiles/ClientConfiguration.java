package Anon.client.service.core.profiles;

import Anon.client.rulegeneration.core.profiles.RuleGenerationProfile;

public class ClientConfiguration {
    public RuleGenerationProfile ruleGenerationProfile;
    public String trafficSource;
    public String providerAddress;

    public ClientConfiguration(RuleGenerationProfile ruleGenerationProfile, String trafficSource, String providerAddress) {
        this.ruleGenerationProfile = ruleGenerationProfile;
        this.trafficSource = trafficSource;
        this.providerAddress = providerAddress;
    }

    public ClientConfiguration() {
        this.ruleGenerationProfile = new RuleGenerationProfile();
        this.trafficSource = "0.0.0.0:9092";
        this.providerAddress = "0.0.0.0:8081";
    }
}
