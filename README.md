
# Sensor measures collector

This app make sensor data collection using Wear OS.
The current available sensors are:
- Gyroscope;
- Accelerometer;
- Electrocardiogram;

There are two main services using foreground approach two collect sensors data:

- EcgManager;
- SensorsService;

The EcgManager was built with Samsung Health Sensor Lib and cannot be used without a registered app.

The SensorsService uses Sensor API for listen Accelerometer and Gyroscope measurements.

## Architecture
The data collected is registered on local database (Room Database).
There are two registered Workers:

### SyncRemote
Get on database the measurements that are not synced and sent to remote service. After this, these register are marked as synced.

### ClearSynced
Get on database the measurements that are synced and delete them.

Both Workers are initialized on application setup and executed with scheduled configuration.

## API Service
There is a file ```assets/config.json```.

Change ```base_url``` property.
