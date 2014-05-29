WifiPositioning

===============


Maven POM Project containing analysis application and supporting libraries.


Android modules require the following resources required to build:

* Android SDK API16: http://developer.android.com/sdk/index.html?hl=sk

* Maven Android Plugin: https://code.google.com/p/maven-android-plugin/wiki/GettingStarted

* Maven 3.1.1 or later: http://maven.apache.org/download.cgi



## Android Detector

Gathers RSSI data from access points and stores alongside specified grid references as offline radio map. Output used by Android KNN, Android Particle Filter, KNN Framework and Particle Filter Framework.
Data stored to local storage with optional Google Drive integration, requires API-key and setup.

* https://developers.google.com/drive/android/intro



## Android KNN

Android implementation Particle Filter for indoor positioning. Generates on-screen results and stores to file for further testing in Particle Filter Framework.



## Android Particle Filter

Android implementation Particle Filter for indoor positioning. Generates on-screen results and stores to file for further testing in Particle Filter Framework.



## Base Library

Generic functions for loading, storing, displaying and processing wifi information.



## KNN Framework

Testing framework for data generated by Android KNN.



## KNN Library

Functions focused around K-Nearest Neighbour algorithm.



## Particle Filter Framework

Testing framework for data generated by Android Particle Filter.

## Particle Filter Library

Functions focussed around Particle Filter algorithm.



## Probabilistic Library

Functions focussed around Probabilistic algorithm.
