# Spring Bootによるサンプル

MQTTとSTOMPの連携サンプルです。

## With Docker

```
$ gradle clean build buildDocker
$ docker run -e MQTTHOST=[MQTT Host] -p 8080:8080 spring-boot-sample
```

MQTTのホストとポートは、MQTTHOSTとMQTTPORTで指定できます。
デフォルトは、raspberrypi.local:1883です。
