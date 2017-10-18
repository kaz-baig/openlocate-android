
![OpenLocate](http://imageshack.com/a/img922/4800/Pihgqn.png)

We’re building an open SDK to collect location data (like GPS) from phones, for apps like yours

## Purpose

### Why is this project useful?

OpenLocate is supported by developers, non-profits, trade groups, and industry for the following reasons:

* Collecting location data in a battery efficient manner that does not adversely affect mobile application performance is non-trivial. OpenLocate enables everyone in the community to benefit from shared knowledge around how to do this well.
* Creates standards and best practices for location collection.
* Developers have full transparency on how OpenLocate location collection works.
* Location data collected via OpenLocate is solely controlled by the developer.

### What can I do with location data?

Mobile application developers can use location data collected via OpenLocate to:

* Enhance their mobile application using context about the user’s location.
* Receive data about the Points of Interest a device has visited by enabling integrations with 3rd party APIs such as Google Places or Foursquare Venues
* Send location data to partners of OpenLocate via integrations listed here.

### Who is supporting OpenLocate?

OpenLocate is supported by mobile app developers, non-profit trade groups, academia, and leading companies across GIS, logistics, marketing, and more.

## Requirements

- Android - Min SDK version 19

## Permissions

Openlocate uses the following permissions:

* ACCESS_COARSE_LOCATION - Required to access approximate location.
* ACCESS_FINE_LOCATION - Required to access precise location.
* INTERNET - Required to open network sockets.
* ACCESS_WIFI_STATE - Required to access information about Wi-Fi networks.

## Installation

### Adding to your project

Add the below line to your app's `build.gradle`:

```groovy
repositories {
    maven {
        url "https://s3-us-west-2.amazonaws.com/openlocate-android/"
    }
}
```

Add the below line to your app's `build.gradle` inside the `dependencies` section:
    
```groovy
compile 'com.openlocate:openlocate:0.1.10@aar'
```

## Usage

### Start tracking of location
Configure where the SDK should send data to by building the configuration with appropriate URL and headers. Supply the configuration to the `startTracking` method.

```java
Configuration config = new Configuration.Builder()
                    .setUrl(<Your URL>)
                    .setHeaders(<Your Headers>)
                    .build();
try {
  OpenLocate.getInstance(context).startTracking(config);
} catch (Exception e) {
  Log.e("OpenLocate", e.getMessage())
}
```

#### For example, to send data to SafeGraph:

```java
HashMap<String, String> headers = new HashMap<>();
headers.put("Authorization", "Bearer <TOKEN>");

Configuration config = new Configuration.Builder()
        .setUrl("https://api.safegraph.com/v1/provider/<UUID>/devicelocation")
        .setHeaders(headers)
        .build();
try {
  OpenLocate.getInstance(context).startTracking(config);
} catch (Exception e) {
  Log.e("OpenLocate", e.getMessage())
}
```


### Stop tracking of location

To stop location tracking, call the `stopTracking` method on `OpenLocate`. Get the instance by calling `getInstance`.

```java
OpenLocate.getInstance(context).stopTracking()
```

### Fields collected by the SDK

The following fields are collected by the SDK to be sent to a private or public API:

1. `latitude` - Latitude of the device
2. `longitude` - Longitude of the device
3. `utc_timestamp` - Timestamp of the recorded location in epoch
4. `horizontal_accuracy` - The accuracy of the location being recorded
5. `id_type` - 'aaid' for identifying android advertising type
6. `ad_id` - Advertising identifier
7. `ad_opt_out` - Limited ad tracking enabled flag

## Communication

- If you **need help**, post a question to the [discussion forum](https://groups.google.com/a/openlocate.org/d/forum/openlocate), or tag a question with 'OpenLocate' on [Stack Overflow](https://stackoverflow.com).
- If you **found a bug**, open an issue.
- If you **have a feature request**, open an issue.
- If you **want to contribute**, submit a pull request.

## License

This project is licensed under the MIT License - see the LICENSE.md file for details.
