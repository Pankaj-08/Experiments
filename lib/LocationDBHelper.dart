import 'package:geolocator/geolocator.dart';

class LocationDBHelper {
  void addLocation(
      double time, double lattitude, double longitude, double accuracy) {
    //     try {
    //         db.execSQL("insert into locations values(" + time + "," + lattitude + "," + longitude + "," + accuracy + ")");
    //     } catch (Exception e) {
    //         Utils.debug(context, e, e.getMessage());
    //     }
    // }
  }

  Future<Map> cleanLisoto(Map data) async {
    print("--->>>>>>Before<<<<<----   ");
    print(data.length);
    print(data);
    List<List<Map>> paths = data["locations"];
    print(paths.length);
    // Map bounds = [{}];
    int factor = 100;
    for (int j = 0; j < paths.length; j++) {
      List<Map> line = paths[j];
      print(line.length);
      if (line.length < 2) {
        // bounds.add([{}]);
        continue;
      }
      // LatLngBounds.Builder builder = new LatLngBounds.Builder();
      for (int i = 1; i < line.length - 1; i++) {
        // print(i);
        double first;
        double second;
        Map point = line[i];
        if (i > 0) {
          Map prevPoint = line[i - 1];
          Map nextPoint = line[i + 1];
          if (prevPoint.isNotEmpty && nextPoint.isNotEmpty) {
            print("$i -> $prevPoint");
            print("$i -> $nextPoint");
            first = Geolocator.distanceBetween(
                point['lat'], point['lng'], prevPoint['lat'], prevPoint['lng']);
            second = Geolocator.distanceBetween(
                point['lat'], point['lng'], nextPoint['lat'], nextPoint['lng']);
            // print("$first -- $second");
            // Location.distanceBetween(point.getDouble(0), point.getDouble(1), prevPoint.getDouble(0), prevPoint.getDouble(1), first);
            // Location.distanceBetween(point.getDouble(0), point.getDouble(1), nextPoint.getDouble(0), nextPoint.getDouble(1), second);
            if (first > factor && second > factor) {
              // print("line Removed $i");
              line.removeAt(i);
              i -= 2;
            }
          }
        }
      }
      //     for (int i = 0; i < line.length(); i++) {
      //         JSONArray point = line.getJSONArray(i);
      //         builder.include(new LatLng(point.getDouble(0), point.getDouble(1)));
      //     }
      //     LatLngBounds latLngBounds = builder.build();
      //     JSONArray temp = new JSONArray();
      //     temp.put(latLngBounds.northeast.latitude);
      //     temp.put(latLngBounds.northeast.longitude);
      //     temp.put(latLngBounds.southwest.latitude);
      //     temp.put(latLngBounds.southwest.longitude);
      //     bounds.put(temp);
      paths[j] = line;
      print(line.length);
    }
    data["locations"] = paths;
    // data.put("bounds", bounds);
    print("--->>>>>>After<<<<<----   ");
    print(data.length);
    print(paths.length);
    print(data);
    return data;
  }
}
