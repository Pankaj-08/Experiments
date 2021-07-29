import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:location/location.dart';
import 'package:untitled/LatLngSqlite.dart';
import 'package:untitled/SqliteHelper.dart';

import 'ImagePickerTesting.dart';

void main() => runApp(MyApp());

class MyApp2 extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Image Picker Demo',
      home: ImagePickerTesting(),
    );
  }
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  late BitmapDescriptor bd;

  late GoogleMapController mapController;
  late LocationData _currentPosition;
  Location location = Location();
  Map<MarkerId, Marker> markers = <MarkerId, Marker>{};

  bool isStopped = false; //global
  sec5Timer() {
    Timer.periodic(Duration(seconds: 2), (timer) {
      if (isStopped) {
        timer.cancel();
      }
      getfiltered();
    });
  }

  void _add() {
    var markerIdVal = "00";
    final MarkerId markerId = MarkerId(markerIdVal);

    // creating a new MARKER
    final Marker marker = Marker(
      markerId: markerId,
      icon: bd,
      position: LatLng(_currentPosition.latitude!, _currentPosition.longitude!),
      onTap: () {
        // _onMarkerTapped(markerId);
      },
    );

    setState(() {
      // adding a new marker to map
      markers[markerId] = marker;
    });
  }

  late GoogleMapController _controller;
  LatLng _initialcameraposition = LatLng(0.5937, 0.9629);

  _MyAppState() {
    nativeCall.setMethodCallHandler(getNativeCall);
  }

  late LatLngSqlite latLngSqlite;

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    latLngSqlite = new LatLngSqlite();
    getLoc();
    // getter();
    sec5Timer();
    BitmapDescriptor.fromAssetImage(
            ImageConfiguration(size: Size(18, 18)), 'asset/images/location.png')
        .then((onValue) {
      bd = onValue;
    });
  }

  Future<void> _onMapCreated(GoogleMapController _cntlr) async {
    _controller = _cntlr;
    // _add();
    // location.onLocationChanged.listen((l) {
    // });
  }

  final LatLng _center = const LatLng(22.521563, 72.677433);
  Set<Polyline> polys = Set();

  static const MethodChannel methodChannel =
      MethodChannel('sample.flutter.io/gps');
  String _gps = 'GPS LOC';

  Future<void> _getGps() async {
    String gps;
    try {
      final String? result = await methodChannel.invokeMethod('getGps');
      gps = 'Hello : $result';
    } catch (e) {
      gps = "error while loading $e";
    }
    setState(() {
      _gps = gps;
    });
  }

  static const MethodChannel nativeCall =
      MethodChannel('sample.flutter.io/nativeCall');
  String _nativeString = "Native Method";
  final List<String> entries = <String>[];
  int i = 0;
  List<LatLng> points = [];

  Future<dynamic> getNativeCall(MethodCall methodCall) async {
    print(methodCall.method);
    if (methodCall.method == "getNativeCall") {
      Map map = methodCall.arguments;
      print(map.toString());
      print("-->> " + map["time"] + " " + map["lat"] + " " + map["long"]);
      // insert(map["time"],map["lat"],map["long"], map["accu"]);
      points.add(LatLng(double.parse(map["lat"]), double.parse(map["long"])));
      setState(() {
        var poly = Polyline(
            polylineId: PolylineId('hll'),
            points: points,
            width: 3,
            color: Colors.teal);
        polys.add(poly);
      });
      setState(() {
        for (var s in map.keys) {
          print(s + " -> " + map[s]);
          entries.add(map[s]);
          // entries[i] = map[s];
          // i++;
        }
        _nativeString = "I am from Flutter";
      });
      print("After Method channel from native");
    }
  }

  Future<void> getter() async {
    points.clear();
    List<SqliteHelper> sqliteHelperList = await latLngSqlite.getlatlng();
    for (var s in sqliteHelperList) {
      points.add(LatLng(s.lat, s.lng));
      setState(() {
        var poly = Polyline(
            polylineId: PolylineId('hll'),
            points: points,
            width: 4,
            color: Colors.teal);
        polys.add(poly);
      });
      print(s.toString());
    }
  }

  void insert(int time, double lat, double long, double acc) {
    latLngSqlite.insertlatlng(
        new SqliteHelper(lat: lat, lng: long, time: time, accuracy: acc));
    print("success");
  }

  void delete() {
    latLngSqlite.deletelatlng(410);
  }

  void update() {
    SqliteHelper sqliteHelper =
        new SqliteHelper(lat: 24.55, lng: 72.55, time: 12, accuracy: 23.5);
    latLngSqlite.updateLatlng(sqliteHelper);
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: Scaffold(
        appBar: AppBar(
          title: Text('Maps Sample App'),
          backgroundColor: Colors.green[700],
        ),
        body: SafeArea(
          child: Stack(
            children: [
              GoogleMap(
                myLocationEnabled: true,
                onMapCreated: _onMapCreated,
                initialCameraPosition: CameraPosition(
                  target: _center,
                  zoom: 11.0,
                ),
                polylines: polys,
                markers: Set<Marker>.of(markers.values),
              ),
              Align(
                alignment: Alignment.topLeft,
                child: Text(
                  _nativeString,
                  style: TextStyle(
                    fontSize: 20,
                  ),
                ),
              ),
              Align(
                alignment: Alignment.topRight,
                child: Text(_gps),
              ),
              // Align(
              //   alignment: Alignment.topRight,
              //   child: ListView.builder(
              //       padding: const EdgeInsets.all(8),
              //       itemCount: entries.length,
              //       itemBuilder: (BuildContext context, int index) {
              //         return Container(
              //           height: 50,
              //           child: Center(child: Text('Entry ${entries[index]}')),
              //         );
              //       }),
              // ),
              Align(
                alignment: Alignment.bottomLeft,
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.end,
                    children: [
                  Padding(
                    padding: EdgeInsets.all(15),
                    child: Align(
                      alignment: Alignment.bottomLeft,
                      child: FloatingActionButton(
                        onPressed: getter,
                        child: Icon(Icons.add),
                      ),
                    ),
                  ),
                  Padding(
                    padding: EdgeInsets.all(15),
                    child: Align(
                      alignment: Alignment.bottomLeft,
                      child: FloatingActionButton(
                        onPressed: getfiltered,
                        child: Icon(Icons.subtitles_off_outlined),
                      ),
                    ),
                  ),
                ]),
              )
            ],
          ),
        ),
        floatingActionButton: FloatingActionButton(
          onPressed: _getGps,
          child: Icon(Icons.map),
        ),
      ),
    );
  }

  getLoc() async {
    bool _serviceEnabled;
    PermissionStatus _permissionGranted;

    _serviceEnabled = await location.serviceEnabled();
    if (!_serviceEnabled) {
      _serviceEnabled = await location.requestService();
      if (!_serviceEnabled) {
        return;
      }
    }

    _permissionGranted = await location.hasPermission();
    if (_permissionGranted == PermissionStatus.denied) {
      _permissionGranted = await location.requestPermission();
      if (_permissionGranted != PermissionStatus.granted) {
        return;
      }
    }

    _currentPosition = await location.getLocation();
    _initialcameraposition =
        LatLng(_currentPosition.latitude!, _currentPosition.longitude!);
    setState(() {
      _initialcameraposition =
          LatLng(_currentPosition.latitude!, _currentPosition.longitude!);
      _controller.animateCamera(
        CameraUpdate.newCameraPosition(
          CameraPosition(
              bearing: 5.5, target: _initialcameraposition, zoom: 16.8),
        ),
      );
    });
    // location.onLocationChanged.listen((LocationData currentLocation) {
    //   print("${currentLocation.latitude} : ${currentLocation.longitude}");
    // });
  }

  Future<void> getfiltered() async {
    points.clear();
    List<SqliteHelper> sqliteHelperList =
        await latLngSqlite.getFilteredLocation();
    for (var s in sqliteHelperList) {
      points.add(LatLng(s.lat, s.lng));
      setState(() {
        var poly = Polyline(
            polylineId: PolylineId('hll'),
            points: points,
            width: 4,
            color: Colors.teal);
        polys.add(poly);
      });
      print(s.toString());
    }
  }
}
