# App to show realtime tracking using knn.
# Use with Android API 17 and JDK 1.7.
# Requires external library KNearestNeighbourLib.

# Files need to be placed in Visualiser folder (can be specified in VisActivity class) on the devices internal storage (USB accessible).
# Output files and logs will be sent to same folder

# Field separator can be specified in VisActivity class.

# Expected filenames (can be specified in VisActivity class):
# RSSI Offline Map (compiled from raw RSSI data points): RSSIKNNData.knn
# Magnetic Offline Map (compiled from raw Magnetic data points): MagneticKNNData.knn
# Information about room layouts on the floor and how relate to floor plan image: RoomInfo.csv
# SSID filter - white list of SSIDs to keep, one per line - if left out or blank then all accepted: FilterSSID.txt
# Image to display floor plan: floorplan.png
