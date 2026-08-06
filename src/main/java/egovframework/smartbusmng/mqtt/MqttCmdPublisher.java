package egovframework.smartbusmng.mqtt;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MqttCmdPublisher implements DisposableBean {

	private final String broker;
	private final String username;
	private final String password;
	private final String clientId;
	private final int connectTimeoutSec;
	private final int keepAliveSec;
	
	private volatile MqttClient client;
	
	public MqttCmdPublisher(
        @Value("${mqtt.broker}") String broker,
        @Value("${mqtt.username:}") String username,
        @Value("${mqtt.password:}") String password,
        @Value("${mqtt.clientIdPrefix:smartbusmng-dispatch}") String clientIdPrefix,
        @Value("${mqtt.connectTimeoutSec:10}") int connectTimeoutSec,
        @Value("${mqtt.keepAliveSec:30}") int keepAliveSec
	) {
		this.broker = broker;
		this.username = username;
		this.password = password;
        this.clientId = clientIdPrefix + "-" + UUID.randomUUID().toString().substring(0, 8);
		this.connectTimeoutSec = connectTimeoutSec;
		this.keepAliveSec = keepAliveSec;
	}
	
	private synchronized void ensureConnected() throws MqttException {
		if (client != null && client.isConnected()) return;
		
		client = new MqttClient(broker, clientId, new MemoryPersistence());
		
		MqttConnectOptions opt = new MqttConnectOptions();
		opt.setAutomaticReconnect(true);
		opt.setCleanSession(false);
		opt.setConnectionTimeout(connectTimeoutSec);
		opt.setKeepAliveInterval(keepAliveSec);
		
		if (username != null && !username.isBlank()) opt.setUserName(username);
		if (password != null && !password.isBlank()) opt.setPassword(password.toCharArray());
		
		client.connect(opt);
	}
	
	public void publish(String topic, String jsonPayload, int qos, boolean retained) throws MqttException {
		ensureConnected();
		MqttMessage msg = new MqttMessage(jsonPayload.getBytes(StandardCharsets.UTF_8));
		msg.setQos(qos);
		msg.setRetained(retained);
		client.publish(topic, msg);
	}
	
	@Override
	public void destroy() throws Exception {
		try {
			if (client != null && client.isConnected()) client.disconnect();
		} finally {
			if (client != null) {
				client.close();
			}
		}
	}
}
