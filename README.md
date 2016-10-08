Spring BootによるMQTTとSTOMPの連携サンプル
===

# MQTT Brokerのインストール

[Apache ActiveMQ](http://activemq.apache.org/)をインストールします。

```
cd {ActiveMQをインストールしたディレクトリ}/bin
./actimvemq start
```

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
