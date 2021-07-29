import 'dart:async';

import 'package:flutter/cupertino.dart';
import 'package:flutter/widgets.dart';
import 'package:path/path.dart';
import 'package:sqflite/sqflite.dart';
import 'package:untitled/LocationDBHelper.dart';
import 'package:untitled/SqliteHelper.dart';

class LatLngSqlite {
  late Database database;

  LatLngSqlite() {
    WidgetsFlutterBinding.ensureInitialized();
    initDB();
  }

  Future<void> initDB() async {
    database = await openDatabase(
      join(await getDatabasesPath(), 'latlng_database.db'),
      onCreate: (db, version) {
        return db.execute(
          'CREATE TABLE latlng(time integer PRIMARY KEY, lat real, lng real, accuracy real)',
        );
      },
      version: 1,
    );
  }

  Future<void> insertlatlng(SqliteHelper sqliteHelper) async {
    final db = database;
    await db.insert(
      'latlng',
      sqliteHelper.toMap(),
      conflictAlgorithm: ConflictAlgorithm.replace,
    );
  }

  Future<List<SqliteHelper>> getlatlng() async {
    final db = database;

    final List<Map<String, dynamic>> maps = await db.query('latlng');
    return List.generate(maps.length, (i) {
      return SqliteHelper(
        time: maps[i]['time'],
        lat: maps[i]['lat'],
        lng: maps[i]['lng'],
        accuracy: maps[i]['accuracy'],
      );
    });
  }

  Future<List<SqliteHelper>> getFilteredLocation() async {
    final db = database;
    final List<Map<String, dynamic>> maps = await db.query('latlng');

    Map data = {};
    List<List<Map>> paths = [[{}]];

    List<Map> line = List.generate(maps.length, (i) {
        Map point = maps[i];
      return point;
    });
    line = await cleanAccuracy(line);
    paths[0] = line;
    data['locations'] = paths;
    print(data);
    data  = await LocationDBHelper().cleanLisoto(data);
    paths = data['locations'];
    line  = paths[0];
    List<SqliteHelper> mainList = [];
    for(Map m in line)
      {
        if(m.isNotEmpty) {
          mainList.add(SqliteHelper(
            time: m['time'],
            lat: m['lat'],
            lng: m['lng'],
            accuracy: m['accuracy'],
          ));
        }
      }
    return mainList;
    //   ( List.generate(line.length, (i) {
    //   return SqliteHelper(
    //     time: line[i]['time'],
    //     lat: line[i]['lat'],
    //     lng: line[i]['lng'],
    //     accuracy: line[i]['accuracy'],
    //   );
    // }));
  }

  Future<void> updateLatlng(SqliteHelper sqliteHelper) async {
    // Get a reference to the database.
    final db = database;

    // Update the given SqliteHelper.
    await db.update(
      'latlng',
      sqliteHelper.toMap(),
      // Ensure that the Dog has a matching id.
      where: 'time = ?',
      // Pass the Dog's id as a whereArg to prevent SQL injection.
      whereArgs: [sqliteHelper.time],
    );
  }

  Future<void> deletelatlng(double time) async {
    // Get a reference to the database.
    final db = database;

    // Remove the Dog from the database.
    await db.delete(
      'latlng',
      // Use a `where` clause to delete a specific dog.
      where: 'time = ?',
      // Pass the Dog's id as a whereArg to prevent SQL injection.
      whereArgs: [time],
    );
  }

  Future<List<Map>> cleanAccuracy(List<Map> line) async{
    List<Map> newLine = [{}];
    for(Map point in line)
      {
        if(point['accuracy']<20)
          {
            newLine.add(point);
          }
      }
    return newLine;
  }
}
