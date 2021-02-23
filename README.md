# mqtt-mapping

基于spring boot的mqtt例子，使用注解将不同的主题分派给不同的任务。支持+、#通配符。

#### 1,yml配置
````
spring:
  mqtt:
    domain: 127.0.0.1
    port: 1883
    clientId: SERVER00000001
    username: admin
    password: admin
    connectionTimeout: 10
    automaticReconnect: true
    cleanSession: true
    keepAliveInterval: 60
````

#### 2,使用方法，程序里已经包含了mptt
````
    /**
     * @author wuxh
     * @version v1.0.0
     */
    @MqttMassageController
    //根主题
    @MqttMassageMapping("mqtt")
    public class MqttController {
    
        //注入mqtt处理类。
        @Autowired
        private MqttTemplate mqttTemplate;
    
        @OnConnectComplete
        public void onConnect() {
            mqttTemplate.subscribe("mqtt/msg", MqttConstant.MqttQos.QOS1);
    
    
            mqttTemplate.publish("mqtt/msg", MqttConstant.MqttQos.QOS1,"hello");
        }
        
        //订阅的主题，框架会自动将 mqtt/msg 派发到此函数
        @MqttMassageMapping("/msg")
        public void iotMsg(@RequestTopic String topic, @RequestMassage MqttMessage msg) {
           System.out.println(new String( msg.getPayload()));
        }

        @OnDisconnect
        public void onDisconnect() {
           //do something...
        }
    }
````
