package Anon.controller.service.message.remote;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representation of attack flows for Anon demo
 * <p>
 */

class FlowInfo {
    private String source;
    private String destination;
    private int protocol;
    private int bytes;
    private int packets;

    @JsonCreator
    public FlowInfo(
            @JsonProperty("source") String source,
            @JsonProperty("destination") String destination,
            @JsonProperty("protocol") int protocol,
            @JsonProperty("bytes") int bytes,
            @JsonProperty("packets") int packets
    ) {
        this.source = source;
        this.destination = destination;
        this.protocol = protocol;
        this.bytes = bytes;
        this.packets = packets;
    }

    public FlowInfo() {
        this.source = "";
        this.destination = "";
        this.protocol = 0;
        this.bytes = 0;
        this.packets = 0;
    }
}
