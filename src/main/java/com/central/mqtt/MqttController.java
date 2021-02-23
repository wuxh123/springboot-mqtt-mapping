package com.central.mqtt;

import com.central.mqtt.annotation.*;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;

@MqttMassageController
@MqttMassageMapping("mqtt")
public class MqttController {
    @Autowired
    private MqttTemplate mqttTemplate;

    @OnConnectComplete
    public void onConnect() {
        mqttTemplate.subscribe("mqtt/msg", MqttConstant.MqttQos.QOS1);


        mqttTemplate.publish("mqtt/msg", MqttConstant.MqttQos.QOS1,"hello");
    }

    @MqttMassageMapping("/msg")
    public void iotMsg(@RequestTopic String topic, @RequestMassage MqttMessage msg) {
        System.out.println(new String( msg.getPayload()));
    }

    @OnDisconnect
    public void onDisconnect() {
        System.out.println("onDisconnect");
    }

}
