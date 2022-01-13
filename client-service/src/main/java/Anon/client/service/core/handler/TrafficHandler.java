package Anon.client.service.core.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import Anon.client.service.core.intelligence.RuleGenerator;
import Anon.client.service.core.resource.TrafficMessage;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.javatuples.Pair;

import java.util.*;

/**
 * Receiving traffic information periodically.
 * <p>
 */
public class TrafficHandler {

    public CircularFifoQueue<Pair<String, String>> queue;
    private Timer timer;
    private ReceiverTask task;
    private ObjectMapper mapper;
    private RuleGenerator ruleGenerator;

    public TrafficHandler(int seconds, String trafficSource) {
        ruleGenerator = RuleGenerator.Companion.getInstance();
        mapper = new ObjectMapper();
        queue = new CircularFifoQueue<>(30);
        timer = new Timer();
        task = new ReceiverTask(trafficSource, queue);
        timer.schedule(task, 5, seconds * 1000);
    }

    /**
     * Main task class for executing all beacon's functions
     */
    class ReceiverTask extends TimerTask {
        CircularFifoQueue<Pair<String, String>> queue;
        KafkaConsumer<String, String> consumer;
        int iteration;


        ReceiverTask(String trafficSource, CircularFifoQueue<Pair<String, String>> queue) {
            this.queue = queue;
            initiateKafkaConsumer(trafficSource);
            iteration = 0;
        }

        private void initiateKafkaConsumer(String trafficSource) {
            String topicName = "Anon-client";
            Properties props = new Properties();
            props.put("bootstrap.servers", trafficSource);
            props.put("group.id", "test");
            props.put("enable.auto.commit", "false");
            props.put("auto.commit.interval.ms", "1000");
            props.put("session.timeout.ms", "30000");
            props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

            consumer = new KafkaConsumer<>(props);

            //Kafka Consumer subscribes list of topics here.
            TopicPartition tp = new TopicPartition(topicName, 0);
            List<TopicPartition> tps = Arrays.asList(tp);
            consumer.assign(tps);
            consumer.seekToEnd(tps);
        }

        /**
         * entry function for each periodical call.
         */
        public void run() {
            ConsumerRecords<String, String> records = consumer.poll(1000);
            for (ConsumerRecord<String, String> record : records) {
                String timestr = record.key(), jsonstr = record.value();
                Pair<String, String> temp = new Pair<>(timestr, jsonstr);
                queue.offer(temp);
                try {
//                    System.out.println(jsonstr);
                    TrafficMessage msg = mapper.readValue(jsonstr, TrafficMessage.class);
//                    System.out.println(msg.getTime());
                    ruleGenerator.addNewFlows(msg);
                } catch (Exception e) {
//                    System.out.println(e);
                }
            }
        }
    }
}
