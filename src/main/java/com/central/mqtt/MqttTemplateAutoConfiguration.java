package com.central.mqtt;

import com.central.mqtt.properties.MqttProperties;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

/**
 * @author Microgalaxy
 * @version v1.0.0
 * @date 2020/11/06 20:20
 */
@Configuration
@EnableConfigurationProperties(MqttProperties.class)
class MqttTemplateAutoConfiguration {
    private final Logger log = LoggerFactory.getLogger(MqttTemplateAutoConfiguration.class);

    @Autowired
    private MqttProperties config;

    private MqttMassageDispatcher dispatcher;

    @ConditionalOnMissingBean
    @Bean
    protected MqttMassageDispatcher initDispatcher() {
        dispatcher = new MqttMassageDispatcher(config);
        dispatcher.initMqttHandleMap();
        return dispatcher;
    }

    @ConditionalOnMissingBean
    @Bean
    protected MqttConnectOptions mqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(config.getUsername());
        options.setPassword(config.getPassword().toCharArray());
        //default：30
        options.setConnectionTimeout(config.getConnectionTimeout());
        //default：false
        options.setAutomaticReconnect(config.getAutomaticReconnect());
        //default：true
        options.setCleanSession(config.getCleanSession());
        //default：60
        options.setKeepAliveInterval(config.getKeepAliveInterval());
        return options;
    }

    @DependsOn({"initDispatcher","mqttConnectOptions"})
    @ConditionalOnMissingBean
    @Bean
    protected MqttTemplate mqttTemplate() {
        MqttTemplate mqttTemplate = new MqttTemplate(config, dispatcher);
        try {
            mqttTemplate.initMqttClient();
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Create mqtt client failed: {}", e.getMessage(), e);
            }
            System.exit(1);
        }
        return mqttTemplate;
    }

    @EventListener(ApplicationStartedEvent.class)
    public void connectionMqttBroker() {
        if (log.isInfoEnabled()) {
            log.info("Connecting to mqtt broker ...【host: {}】【clientId: {}】【username: {}】",
                    "tcp://" + config.getDomain() + ":" + config.getPort(), config.getClientId(), config.getUsername());
        }

        MqttConnectOptions options = config.getApplicationContext().getBean(MqttConnectOptions.class);
        MqttTemplate mqttTemplate = config.getApplicationContext().getBean(MqttTemplate.class);
        try {
            mqttTemplate.connectionMqttBroker(options);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Connection to mqtt broker failed: {}", e.getMessage(), e);
            }
//            System.exit(1);
        }
    }
}
