package Anon.controller.service.sdnwrapper;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import Anon.controller.service.core.rule.OpenflowRule;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.ClientBuilder;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * Parent abstract class for all SDN controller wrapper classes.
 * This class provides guideline on what to implement.
 * <p>
 * Use case of the wrapper:
 * the wrapper will only be used to control switches that an AS controls
 * therefore, it will always install/remove/get rules by using AdminMessage class
 */
public abstract class SdnWrapper {

    javax.ws.rs.client.Client httpClient;
    String baseURI;

    /**
     * Base URI to the SDN controller restful service
     *
     * @param baseURI the base URI of the SDN controller
     */
    SdnWrapper(String baseURI) {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(JacksonFeature.class);
        // init http client
        this.httpClient = ClientBuilder.newClient(clientConfig);
        this.baseURI = baseURI;
    }

    /**
     * Translate a JSON object from the String format into JsonNode format
     *
     * @param json the string format of the JSON object
     * @return the JsonNode format of JSON object
     */
    static JsonNode jsonStringToJsonNode(String json) {
        JsonFactory factory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper(factory);
        JsonNode node = null;
        try {
            node = mapper.readTree(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return node;
    }

    /**
     * Abstract rule creation function that all subclasses should implement
     *
     * @param rules      the OpenFlow rules to be created on SDN switch
     * @param sdnSwitch the SDN switch to install the rule on
     * @return the return message of the rule creation attempt
     */
    public abstract String deployRules(List<OpenflowRule> rules, SdnSwitch sdnSwitch);

    /**
     * Abstract rule deletion function that all subclasses should implement
     *
     * @param rule_ids   the ID of the OpenFlow rule to be deleted on SDN switch
     * @param sdnSwitch the SDN switch to delete the rule from
     * @return the return message of the rule deletion attempt
     */
    public abstract String deleteRules(List<String> rule_ids, SdnSwitch sdnSwitch);

}