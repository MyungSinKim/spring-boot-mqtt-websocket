Spring BootによるMQTTとSTOMPの連携サンプル
===

# MQTT Brokerのインストール

[Apache ActiveMQ](http://activemq.apache.org/)をインストールします。

```
cd {ActiveMQをインストールしたディレクトリ}/bin
./activemq start
```

* [ActiveMQの管理コンソール](http://localhost:8161)

# npmモジュールのインストール

プロジェクトのsrc/main/resources/staticでnpm installを実行します。

```
cd src/main/resources/static
npm install
```

# Run

```
./gradlew clean bootRun
```

# ブラウザ

ブラウザから[http://localhost:8080](http://localhost:8080)にアクセスします。
