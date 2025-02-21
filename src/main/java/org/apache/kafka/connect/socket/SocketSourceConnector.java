package org.apache.kafka.connect.socket;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SocketSourceConnector implements the connector interface
 * to write on Kafka messages received on a Socket
 *
 * @author Andrea Patelli
 */
public class SocketSourceConnector extends SourceConnector {
    private final static Logger log = LoggerFactory.getLogger(SocketSourceConnector.class);

    public static final String PORT = "port";
    public static final String SCHEMA_NAME = "schema.name";
    public static final String BATCH_SIZE = "batch.size";
    public static final String TOPIC = "topic";

    private String port;
    private String schemaName;
    private String batchSize;
    private String topic;

    private static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(TOPIC, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Destination topic name")
            .define(SCHEMA_NAME, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Schema name")
            .define(BATCH_SIZE, ConfigDef.Type.INT, ConfigDef.Importance.HIGH, "Batch size")
            .define(PORT, ConfigDef.Type.INT, ConfigDef.Importance.HIGH, "Socket port");


    /**
     * Get the version of this connector.
     *
     * @return the version, formatted as a String
     */
    @Override
    public String version() {
        return AppInfoParser.getVersion();
    }

    /**
     * Start this Connector. This method will only be called on a clean Connector, i.e. it has
     * either just been instantiated and initialized or {@link #stop()} has been invoked.
     *
     * @param map configuration settings
     */
    @Override
    public void start(Map<String, String> map) {
        log.trace("Parsing configuration");

        port = map.get(PORT);
        if (port == null || port.isEmpty())
            throw new ConnectException("Missing " + PORT + " config");

        schemaName = map.get(SCHEMA_NAME);
        if (schemaName == null || schemaName.isEmpty())
            throw new ConnectException("Missing " + SCHEMA_NAME + " config");

        batchSize = map.get(BATCH_SIZE);
        if (batchSize == null || batchSize.isEmpty())
            throw new ConnectException("Missing " + BATCH_SIZE + " config");

        topic = map.get(TOPIC);
        if (topic == null || topic.isEmpty())
            throw new ConnectException("Missing " + TOPIC + " config");

        dumpConfiguration(map);
    }

    /**
     * Returns the Task implementation for this Connector.
     *
     * @return tha Task implementation Class
     */
    @Override
    public Class<? extends Task> taskClass() {
        return SocketSourceTask.class;
    }

    /**
     * Returns a set of configurations for the Task based on the current configuration.
     * It always creates a single set of configurations.
     *
     * @param i maximum number of configurations to generate
     * @return configurations for the Task
     */
    @Override
    public List<Map<String, String>> taskConfigs(int i) {
        ArrayList<Map<String, String>> configs = new ArrayList<>();
        Map<String, String> config = new HashMap<>();
        config.put(PORT, port);
        config.put(SCHEMA_NAME, schemaName);
        config.put(BATCH_SIZE, batchSize);
        config.put(TOPIC, topic);
        configs.add(config);
        return configs;
    }

    /**
     * Stop this connector.
     */
    @Override
    public void stop() {
    }

    @Override
    public ConfigDef config() {
        return CONFIG_DEF;
    }

    private void dumpConfiguration(Map<String, String> map) {
        log.trace("Starting connector with configuration:");
        for (Map.Entry entry : map.entrySet()) {
            log.trace("{}: {}", entry.getKey(), entry.getValue());
        }
    }
}
