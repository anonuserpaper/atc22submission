package Anon.client.rulegeneration.core.resource;

/**
 * Representation of attack flows for Anon demo
 *
 */

public class Flow {
    public String source;
    public String destination;
    public int protocol;
    public int bytes;
    public int packets;


    public Flow() {
        this.source = "";
        this.destination = "";
        this.protocol = 0;
        this.bytes = 0;
        this.packets = 0;
    }

    public Flow(IP source) {
        this.source = source.toString();
        this.destination = "";
        this.protocol = 0;
        this.bytes = 0;
        this.packets = 0;
    }
}
