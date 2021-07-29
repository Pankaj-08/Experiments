package com.example.untitled;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class LocationDBHelper {
//    SQLiteDatabase db;
//    Context context;
//
//    public LocationDBHelper(Context context) {
//        this.context = context;
//        try {
//            db = new SQLiteHelper(context).getWritableDatabase();
//        } catch (Exception e) {
//            Utils.debug(context, e, e.getMessage());
//        }
//    }
//
//    public void addLocation(long time, double lattitude, double longitude, double accuracy) {
//        try {
//            db.execSQL("insert into locations values(" + time + "," + lattitude + "," + longitude + "," + accuracy + ")");
//        } catch (Exception e) {
//            Utils.debug(context, e, e.getMessage());
//        }
//    }
//
//    public void addFenceLocation(long time, double lat, double lng, float acc) {
//        try {
//            db.execSQL("insert into fence_locations values(" + time + "," + lat + "," + lng + "," + acc + ")");
//        } catch (Exception e) {
//            Utils.debug(context, e, e.getMessage());
//        }
//    }
//
//    public JSONArray getLocationArray(boolean allData) {
//        try {
//            SharedPrefUtils prefUtils = new SharedPrefUtils(context);
//            Cursor cursor = db.rawQuery("select * from locations", null);
//            JSONArray array = new JSONArray();
//            if (cursor.getCount() == 0) {
//                cursor.close();
//                return array;
//            } else {
//                if (cursor.moveToFirst()) {
//                    int lattitude = cursor.getColumnIndex("lattitude");
//                    int longitude = cursor.getColumnIndex("longitude");
//                    int time = cursor.getColumnIndex("time");
//                    int accuracy = cursor.getColumnIndex("accuracy");
//
//                    JSONArray line = null;
//                    while (!cursor.isAfterLast()) {
//                        float lat = cursor.getFloat(lattitude);
//                        float lng = cursor.getFloat(longitude);
//                        float acc = cursor.getFloat(accuracy);
//                        long t = cursor.getLong(time);
//                        if (lat == 0 && lng == 0) {
//                            if (line != null)
//                                array.put(line);
//                            line = new JSONArray();
//                        } else {
//                            if (line == null) line = new JSONArray();
//
//                            JSONArray point = new JSONArray();
//                            try {
//                                point.put(lat);
//                                point.put(lng);
//                                point.put(t);
//                                point.put(acc);
//                                if (allData || acc <= prefUtils.min_accuracy()) {
//                                    line.put(point);
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        cursor.moveToNext();
//                    }
//                    if (line != null)
//                        array.put(line);
//                    return array;
//                } else return null;
//            }
//        } catch (Exception e) {
//            Utils.debug(context, e, e.getMessage());
//            return null;
//        }
//    }
//
//    public JSONObject getTrackData(boolean allData) {
//        JSONArray array = getLocationArray(allData);
//        JSONObject object = new JSONObject();
//        try {
//            object.put("locations", array);
//            cleanLisoto(object);
//            object.put("distance", getDistance(array));
//            object.put("time", getTime(array));
//        } catch (Exception e) {
//            Utils.printExceptions(e);
//            Log.e("120", "LocationDBHelper -> getTrackData: " + e.getMessage());
//            Utils.debug(context, e, e.getMessage());
//        }
//        return object;
//    }
//
//    private Double getDistance(JSONArray array) throws Exception {
//        double d = 0;
//        float[] f = new float[1];
//        for (int i = 0; i < array.length(); i++) {
//            JSONArray line = array.getJSONArray(i);
//            if (line.length() == 0)
//                continue;
//            JSONArray lastPoint = line.getJSONArray(0);
//            for (int j = 1; j < line.length(); j++) {
//                JSONArray point = line.getJSONArray(j);
//                if (point.getDouble(3) <= 300) {
//                    Location.distanceBetween(
//                            point.getDouble(0),
//                            point.getDouble(1),
//                            lastPoint.getDouble(0),
//                            lastPoint.getDouble(1), f);
//                    d += f[0];
//                    lastPoint = point;
//                }
//            }
//        }
//        return d * (new SharedPrefUtils(context).getDistanceFactor());
//    }
//
//    public long getTime(JSONArray array) throws Exception {
//        SharedPrefUtils prefUtils = new SharedPrefUtils(context);
//        long totalTime = 0;
//        for (int i = 0; i < array.length(); i++) {
//
//            JSONArray line = array.getJSONArray(i);
//            if (line.length() > 1) {
//                totalTime += line.getJSONArray(line.length() - 1).getLong(2)
//                        - line.getJSONArray(0).getLong(2);
//            }
//        }
//        return totalTime / 1000;
//    }
//
//    public void cleanFenceLocations() {
//        try {
//            Cursor cursor = db.rawQuery("select * from fence_locations", null);
//            if (cursor.getCount() == 0) {
//                cursor.close();
//            } else {
//                if (cursor.moveToFirst()) {
//                    int lattitude = cursor.getColumnIndex("lattitude");
//                    int longitude = cursor.getColumnIndex("longitude");
//                    int time = cursor.getColumnIndex("time");
//
//                    MapPoint last_point = null;
//                    MapPoint last_last_point = null;
//
//                    while (!cursor.isAfterLast()) {
//                        double lat = cursor.getDouble(lattitude);
//                        double lng = cursor.getDouble(longitude);
//                        long t = cursor.getLong(time);
//                        MapPoint point = new MapPoint();
//                        point.point = new LatLng(lat, lng);
//                        point.time = t;
//
//                        if (last_point != null) {
//                            if (lat == 0 && lng == 0 && last_point.point.latitude == 0 && last_point.point.longitude == 0) {
//                                db.execSQL("delete from fence_locations where time=" + last_point.time);
//                            } else if (last_last_point != null &&
//                                    last_point.point.latitude == 0 && last_point.point.longitude == 0 &&
//                                    lat != 0 && lng != 0 && last_last_point.point.latitude != 0 &&
//                                    last_last_point.point.longitude != 0 &&
//                                    t - last_last_point.time < 30000) {
//
//                                db.execSQL("delete from fence_locations where time=" + last_point.time);
//                            } else if (last_last_point != null &&
//                                    last_last_point.point.latitude == 0 && last_last_point.point.longitude == 0 &&
//                                    lat == 0 && lng == 0 && last_point.point.latitude != 0 && last_point.point.longitude != 0) {
//
//                                db.execSQL("delete from fence_locations where time=" + last_point.time + " or time=" + last_last_point.time);
//                            }
//                        }
//                        last_last_point = last_point;
//                        last_point = point;
//                        cursor.moveToNext();
//                    }
//                }
//            }
//        } catch (Exception e) {
//            Utils.debug(context, e, e.getMessage());
//        }
//    }
//
//    public JSONArray getFenceLocations() {
//        try {
//            Cursor cursor = db.rawQuery("select * from fence_locations", null);
//            JSONArray data = new JSONArray();
//            if (cursor.getCount() == 0) {
//                cursor.close();
//                return data;
//            } else {
//                if (cursor.moveToFirst()) {
//                    int lattitude = cursor.getColumnIndex("lattitude");
//                    int longitude = cursor.getColumnIndex("longitude");
//                    int time = cursor.getColumnIndex("time");
//                    int accuracy = cursor.getColumnIndex("accuracy");
//
//                    JSONArray line = null;
//                    while (!cursor.isAfterLast()) {
//                        double lat = cursor.getDouble(lattitude);
//                        double lng = cursor.getDouble(longitude);
//                        long t = cursor.getLong(time);
//                        double acc = cursor.getDouble(accuracy);
//
//                        if (lat == 0 && lng == 0) {
//                            if (line != null && line.length() > 0)
//                                data.put(line);
//                            line = new JSONArray();
//                        } else {
//                            if (line == null)
//                                line = new JSONArray();
//                            JSONObject object = new JSONObject();
//                            try {
//                                object.put("lattitude", String.format(Locale.US, "%.8f", lat));
//                                object.put("longitude", String.format(Locale.US, "%.8f", lng));
//                                object.put("time", t);
//                                object.put("accuracy", String.format(Locale.US, "%.2f", acc));
//                                line.put(object);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        cursor.moveToNext();
//                    }
//                    if (line != null && line.length() > 0)
//                        data.put(line);
//                    return data;
//                } else return null;
//            }
//        } catch (Exception e) {
//            Utils.debug(context, e, e.getMessage());
//            return null;
//        }
//    }
//
//    public void storeFenceLocations(JSONObject object) {
//        try {
//            JSONArray array = object.getJSONArray("locations");
//            for (int i = 0; i < array.length(); i++) {
//                JSONArray line = array.getJSONArray(i);
//                addFenceLocation(0, 0, 0, 0);
//                for (int j = 0; j < line.length(); j++) {
//                    JSONObject obj = line.getJSONObject(j);
//                    addFenceLocation(
//                            obj.getLong("time"),
//                            obj.getDouble("lattitude"),
//                            obj.getDouble("longitude"),
//                            (float) obj.getDouble("accuracy")
//                    );
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Utils.debug(context, e, e.getMessage());
//        }
//    }
//
//    void cleanLisoto(JSONObject data) throws JSONException {
//        JSONArray paths = data.getJSONArray("locations");
//        JSONArray bounds = new JSONArray();
//        int factor = 100;
//        for (int j = 0; j < paths.length(); j++) {
//            JSONArray line = paths.getJSONArray(j);
//            if (line.length() < 2) {
//                bounds.put(new JSONArray());
//                continue;
//            }
//            LatLngBounds.Builder builder = new LatLngBounds.Builder();
//            for (int i = 1; i < line.length() - 1; i++) {
//                float[] first = new float[1];
//                float[] second = new float[1];
//                JSONArray point = line.getJSONArray(i);
//                if (i > 0) {
//                    JSONArray prevPoint = line.getJSONArray(i - 1);
//                    JSONArray nextPoint = line.getJSONArray(i + 1);
//                    Location.distanceBetween(point.getDouble(0), point.getDouble(1), prevPoint.getDouble(0), prevPoint.getDouble(1), first);
//                    Location.distanceBetween(point.getDouble(0), point.getDouble(1), nextPoint.getDouble(0), nextPoint.getDouble(1), second);
//                    if (first[0] > factor && second[0] > factor) {
//                        line.remove(i);
//                        i -= 2;
//                    }
//                }
//            }
//            for (int i = 0; i < line.length(); i++) {
//                JSONArray point = line.getJSONArray(i);
//                builder.include(new LatLng(point.getDouble(0), point.getDouble(1)));
//            }
//            LatLngBounds latLngBounds = builder.build();
//            JSONArray temp = new JSONArray();
//            temp.put(latLngBounds.northeast.latitude);
//            temp.put(latLngBounds.northeast.longitude);
//            temp.put(latLngBounds.southwest.latitude);
//            temp.put(latLngBounds.southwest.longitude);
//            bounds.put(temp);
//            paths.put(j, line);
//        }
//        data.put("locations", paths);
//        data.put("bounds", bounds);
//    }
//
//    public JSONObject calculateData(Context context, JSONArray array) {
//        JSONObject object = new JSONObject();
//        FenceCalculator fenceCalculator = new FenceCalculator(context, array);
//        try {
//            fenceCalculator.cleanFenceLisoto();
//            fenceCalculator.removeInOutSequences();
//            fenceCalculator.createInOutTimeOnLineChange();
//            fenceCalculator.createInOutTimesFromAllLines();
//            object = fenceCalculator.getData();
//        } catch (JSONException e) {
//            Log.e("338", "LocationDBHelper -> calculateData: " + e.getMessage());
//        }
//        return object;
//    }
//
//
////	public JSONObject calculateData(Context context,JSONArray array){
////		JSONObject object = new JSONObject();
////		JSONObject lastPoint = null;
////		boolean lastPointIn = false;
////		try {
////			object.put("locations",array);
////			cleanFenceLisoto(object);
////			array = object.getJSONArray("locations");
////			List<List<LatLng>>  polygons = new SharedPrefUtils(context).fenceArea();
////
////			JSONArray inTimes = new JSONArray();
////			JSONArray outTimes = new JSONArray();
////
////			long totalIn = 0;
////			long totalOut = 0;
////
////			for (int i=0;i<array.length();i++){
////				JSONArray line = array.getJSONArray(i);
////				if (line.length()<=1)
////					continue;
////				line = inOutIn(line, polygons);
////				line = outInOut(line,polygons);
////				array.put(i,line);
////
////
////				JSONObject lastObject = line.getJSONObject(0);
////
////				LatLng lastlatLng = new LatLng(lastObject.getDouble("lattitude"),lastObject.getDouble("longitude"));
////				boolean lastIn = isInside(polygons,lastlatLng,lastObject.getDouble("accuracy"));
////
////				if(lastPoint !=null)
////				{
////					if(lastPointIn!=lastIn)
////					{
////						if (lastIn) {
////							inTimes.put(lastObject.getLong("time"));
////						} else {
////							outTimes.put(lastObject.getLong("time"));
////						}
////					}
////				}
////				for (int j=1;j<line.length();j++){
////					JSONObject obj = line.getJSONObject(j);
////					boolean nextIn = false, isNext = false;
////					long nextTimeDiff = 1200;
////
////					LatLng latLng = new LatLng(obj.getDouble("lattitude"),obj.getDouble("longitude"));
////					double acc = obj.getDouble("accuracy");
////					boolean in = isInside(polygons,latLng,acc);
////					long timeDifference = (obj.getLong("time") - lastObject.getLong("time"))/1000;
////					try {
////						JSONObject nextObj = line.getJSONObject(j + 1);
////						LatLng latLng2 = new LatLng(nextObj.getDouble("lattitude"),
////								nextObj.getDouble("longitude"));
////						nextIn = isInside(polygons,latLng2,nextObj.getDouble("accuracy"));
////						isNext = true;
////						nextTimeDiff = (nextObj.getLong("time") - obj.getLong("time"))/1000;
////						lastPoint= lastObject;
////						lastPointIn  = lastIn;
////					} catch (Exception e) {
////						Log.e("402", "LocationDBHelper -> calculateData: "+e.getMessage());
////					}
////
////					if (in != lastIn){
////						long midTime = (lastObject.getLong("time")+obj.getLong("time"))/2;
////						totalIn += timeDifference/2;
////						totalOut += timeDifference/2;
////
////						if(isNext && in!=nextIn && timeDifference<120 && nextTimeDiff<120)
////						{
////							totalIn += nextTimeDiff/2;
////							totalOut += nextTimeDiff/2;
////							j++;
////						}
////						else {
////							if (in) {
////								inTimes.put(midTime);
////							} else {
////								outTimes.put(midTime);
////							}
////						}
////
////					}else{
////						if (in){
////							totalIn += timeDifference;
////						}else{
////							totalOut += timeDifference;
////						}
////					}
////					lastObject = obj;
////					lastIn = in;
////				}
////			}
////
////			ArrayList<Pair<Boolean,Long>> times = new ArrayList<>();
////			for (int i=0;i<inTimes.length();i++){
////				long t = inTimes.getLong(i);
////				times.add(new Pair<>(true,t));
////			}
////			for (int i=0;i<outTimes.length();i++){
////				long t = outTimes.getLong(i);
////				times.add(new Pair<>(false,t));
////			}
////			Collections.sort(times, (o1, o2) -> Long.compare(o1.second,o2.second));
////
////			object.put("in_times",inTimes);
////			object.put("out_times",outTimes);
////			object.put("total_in",totalIn);
////			object.put("total_out",totalOut);
////
////		} catch (Exception e) {
////			e.printStackTrace();
////			Utils.debug(context,e,e.getMessage());
////			Log.e("439", "LocationDBHelper > calculateData: "+e.getMessage());
////		}
////		return object;
////	}
//
////	void cleanFenceLisoto(JSONObject data) throws JSONException {
////		JSONArray paths = data.getJSONArray("locations");
////		JSONArray bounds = new JSONArray();
////		int factor=100;
////		for (int j=0;j<paths.length();j++){
////			JSONArray currentline = paths.getJSONArray(j);
////			JSONArray line = new JSONArray();
////			for(int i=0;i<currentline.length();i++)
////			{
////				JSONObject point = currentline.getJSONObject(i);
////				if(point.getDouble("accuracy")<300)
////				{
////					line.put(point);
////				}
////			}
////			if (line.length()<2) {
////				bounds.put(new JSONArray());
////				continue;
////			}
////			LatLngBounds.Builder builder = new LatLngBounds.Builder();
////			for (int i=1;i<line.length()-1;i++){
////				float []first=new float[1];
////				float []second=new float[1];
////				JSONObject point = line.getJSONObject(i);
////				if(i>0 ) {
////					JSONObject prevPoint = line.getJSONObject(i - 1);
////					JSONObject nextPoint = line.getJSONObject(i + 1);
////					Location.distanceBetween(point.getDouble("lattitude"), point.getDouble("longitude"), prevPoint.getDouble("lattitude"), prevPoint.getDouble("longitude"), first);
////					Location.distanceBetween(point.getDouble("lattitude"), point.getDouble("longitude"), nextPoint.getDouble("lattitude"), nextPoint.getDouble("longitude"), second);
////					if ((first[0] > factor && second[0] > factor)) {
////						line.remove(i);
////						i -= 2;
////					}
////				}
////			}
////			for (int i=0;i<line.length();i++)
////			{
////				JSONObject point = line.getJSONObject(i);
////				builder.include(new LatLng(point.getDouble("lattitude"),point.getDouble("longitude")));
////			}
////			LatLngBounds latLngBounds = builder.build();
////			JSONArray temp = new JSONArray();
////			temp.put(latLngBounds.northeast.latitude);
////			temp.put(latLngBounds.northeast.longitude);
////			temp.put(latLngBounds.southwest.latitude);
////			temp.put(latLngBounds.southwest.longitude);
////			bounds.put(temp);
////			paths.put(j,line);
////		}
////		data.put("locations",paths);
////		data.put("bounds",bounds);
////	}
////	private JSONArray inOutIn(JSONArray line, List<List<LatLng>> polygons) throws JSONException {
////		JSONArray newLine = new JSONArray();
////		newLine.put(line.getJSONObject(0));
////		for (int j=1;j<line.length()-1;j++){
////			JSONObject current = line.getJSONObject(j);
////			JSONObject last = line.getJSONObject(j-1);
////			JSONObject next = line.getJSONObject(j+1);
////
////			LatLng currentLatLng = new LatLng(current.getDouble("lattitude"),current.getDouble("longitude"));
////			LatLng lastLatLng = new LatLng(last.getDouble("lattitude"),last.getDouble("longitude"));
////			LatLng nextLatLng = new LatLng(next.getDouble("lattitude"),next.getDouble("longitude"));
////
////			boolean currentIn = isInside(polygons,currentLatLng,current.getDouble("accuracy"));
////			boolean lastIn = isInside(polygons,lastLatLng,last.getDouble("accuracy"));
////			boolean nextIn = isInside(polygons,nextLatLng,next.getDouble("accuracy"));
////
////			if(!(lastIn && !currentIn && nextIn)){
////				newLine.put(current);
////			}
////
////		}
////		newLine.put(line.getJSONObject(line.length()-1));
////		return newLine;
////	}
////	private JSONArray outInOut(JSONArray line,List<List<LatLng>> polygons) throws JSONException {
////		JSONArray newLine = new JSONArray();
////		newLine.put(line.getJSONObject(0));
////		for (int j=1;j<line.length()-1;j++){
////			JSONObject current = line.getJSONObject(j);
////			JSONObject last = line.getJSONObject(j-1);
////			JSONObject next = line.getJSONObject(j+1);
////
////			LatLng currentLatLng = new LatLng(current.getDouble("lattitude"),current.getDouble("longitude"));
////			LatLng lastLatLng = new LatLng(last.getDouble("lattitude"),last.getDouble("longitude"));
////			LatLng nextLatLng = new LatLng(next.getDouble("lattitude"),next.getDouble("longitude"));
////
////			boolean currentIn = isInside(polygons,currentLatLng,current.getDouble("accuracy"));
////			boolean lastIn = isInside(polygons,lastLatLng,last.getDouble("accuracy"));
////			boolean nextIn = isInside(polygons,nextLatLng,next.getDouble("accuracy"));
////
////			if(!(!lastIn && currentIn && !nextIn)){
////				newLine.put(current);
////			}
////		}
////		newLine.put(line.getJSONObject(line.length()-1));
////		return newLine;
////	}
//
////	private boolean isInside(List<List<LatLng>> polygons,LatLng latLng, double radius) {
////		boolean inside = false;
////		for (int i=0;i<polygons.size();i++){
//////			inside |= IntersectionUtility.doIntersect(new Circle(point,radius),polygons.get(i));
////			inside = inside || PolyUtil.containsLocation(latLng, polygons.get(i),false) ||
////					PolyUtil.isLocationOnEdge(latLng, polygons.get(i),false,radius);
////		}
////		return inside;
////	}
//

}
