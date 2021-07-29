
class SqliteHelper
{
   late double lat;
   late double lng;
   late double accuracy;
   late int time;
   SqliteHelper({
   required this.lat,
      required this.lng,
      required this.time,
      required this.accuracy
   });

   Map<String, dynamic> toMap() {
      return {
         'time': time,
         'lat': lat,
         'lng': lng,
         'acc': accuracy,
      };
   }

   @override
   String toString() {
      return 'LatLng{time: $time, lat: $lat, lng: $lng, acc: $accuracy}';
   }

}