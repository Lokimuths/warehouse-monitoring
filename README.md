# warehouse-monitoring

Reactive warehouse monitoring service. Sensors push UDP measurements. Warehouse forwards them to RabbitMQ. Central service reads from RabbitMQ and logs an alarm when a reading exceeds its threshold.

sensors --udp--> warehouse --rabbitmq--> central --alarm log

## Stack

Kotlin · Akka Typed · Akka I/O UDP · RabbitMQ · Gradle

## Run

  ```bash
  docker compose up --build

  Starts RabbitMQ and the app. App listens on UDP 3344 (temperature) and 3355 (humidity).

  Send a measurement

  echo "sensor_id=t1; value=40" | nc -u -w1 localhost 3344
  echo "sensor_id=h1; value=60" | nc -u -w1 localhost 3355

  Both exceed thresholds. Expect in docker compose logs app:

  ALARM | warehouse-1:t1 temperature=40.0 exceeds 35.0
  ALARM | warehouse-1:h1 humidity=60.0 exceeds 50.0

  Thresholds

  ┌─────────────┬──────┬───────────┐
  │   Sensor    │ Port │ Threshold │
  ├─────────────┼──────┼───────────┤
  │ Temperature │ 3344 │ 35.0      │
  ├─────────────┼──────┼───────────┤
  │ Humidity    │ 3355 │ 50.0      │
  └─────────────┴──────┴───────────┘

  Multiple warehouses

  Central uses a RabbitMQ fanout exchange. Run several app containers with different warehouse.id values to feed one central. All publish to the same exchange; central tags each alarm with the originating warehouse.

  Tests

  ./gradlew test