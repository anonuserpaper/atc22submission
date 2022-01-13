package Anon.client.service.core.handler;

import com.google.gson.Gson;
import Anon.client.service.core.intelligence.RuleGenerator;
import Anon.client.service.message.AnonMessage;
import Anon.client.service.core.profiles.ClientConfiguration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;

/**
 *
 * This is the base class that gets instantiated when the webserver starts.
 *
 *
 */
public class MainHandler {


    private TrafficHandler trafficHandler;
    public RuleGenerator ruleGenerator;
    public ClientConfiguration clientConfiguration;

    public MainHandler(){

        clientConfiguration = readConfiguration();
        trafficHandler = new TrafficHandler(1, clientConfiguration.trafficSource);
        ruleGenerator = RuleGenerator.Companion.getInstance();
        ruleGenerator.init(clientConfiguration.ruleGenerationProfile, clientConfiguration.providerAddress);
        System.out.println("Anon client is running:");
        System.out.println("\tTraffic source: " + clientConfiguration.trafficSource);
        System.out.println("\tProvider address: " + clientConfiguration.providerAddress);
        System.out.println("\tStrategy: " + clientConfiguration.ruleGenerationProfile.getStrategy().toString());
        System.out.println("\tB: " + clientConfiguration.ruleGenerationProfile.getMinCoverage());
        System.out.println("\tG: " + clientConfiguration.ruleGenerationProfile.getMaxCollateral());
        System.out.println("\tM: " + clientConfiguration.ruleGenerationProfile.getMaxRules());
        System.out.println("\talpha: " + clientConfiguration.ruleGenerationProfile.getDecayRate());
    }

    private ClientConfiguration readConfiguration() {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            ClientConfiguration config = gson.fromJson(new FileReader("clientConfig.json"), ClientConfiguration.class);
            return config;
        } catch (Exception e) {
            System.out.println("Error reading config file 'clientConfig.json', terminating...");
            System.exit(0);
        }
        return new ClientConfiguration();
    }

    /**
     * Generic message handling function.
     * Based the entities that sent the messsage (i.e. client, admin, or controller), the function will forward the message
     * to the corresponding specific message handlers.
     *
     * @param msg
     * @return
     */
    public HashMap handleMessage(AnonMessage msg){
        return new HashMap<>();
    }
}
