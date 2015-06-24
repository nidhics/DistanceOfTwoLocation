package com.example.distanceoftwolocation;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;



public class DirectionsJSONParser {
	
	public List<List<HashMap<String, String>>> dataDirection(String data){
	
	//	double endLat, endLongi, startLat, startLongi;
		String stgResultString = null;
		String poly;
		
		 List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>();
	      
		//String[] arrAllValueOfLatLong = null;
		ArrayList<String> allPolyPoints=new ArrayList<>();
		 ArrayList<String> arrAllValueOfLatLong=new ArrayList<>();
		try {
				JSONObject jsonObj=new JSONObject(data);
				JSONArray routesJsonArray = jsonObj.getJSONArray("routes");
				
				for(int i=0; i<routesJsonArray.length(); i++){
					List path = new ArrayList<HashMap<String, String>>();
					JSONArray legsJsonArray = ((JSONObject)routesJsonArray.get(i)).getJSONArray("legs");
			
					for(int j=0; j<legsJsonArray.length(); j++){
					
						JSONArray stepsJsonArray = ((JSONObject)legsJsonArray.get(j)).getJSONArray("steps");
						
						for(int k=0; k<stepsJsonArray.length(); k++){
							
							
						/*	endLat = (double)((JSONObject)((JSONObject)stepsJsonArray.get(k)).get("end_location")).get("lat");
							endLongi = (double)((JSONObject)((JSONObject)stepsJsonArray.get(k)).get("end_location")).get("lng");
							startLat = (double)((JSONObject)((JSONObject)stepsJsonArray.get(k)).get("start_location")).get("lat");
							startLongi = (double)((JSONObject)((JSONObject)stepsJsonArray.get(k)).get("start_location")).get("lng");*/
							
			                poly=(String)((JSONObject)((JSONObject)stepsJsonArray.get(k)).get("polyline")).get("points");
			                //allPolyPoints.add(poly);
			                List<LatLng> pointToDraw = decodePoly(poly);
			                
			                for(int l=0;l<pointToDraw.size();l++){
	                            HashMap<String, String> hm = new HashMap<String, String>();
	                            hm.put("lat", Double.toString(((LatLng)pointToDraw.get(l)).latitude) );
	                            hm.put("lng", Double.toString(((LatLng)pointToDraw.get(l)).longitude) );
	                            path.add(hm);
	                        }
			                
			                
			               allPolyPoints.add(pointToDraw.toString());
			                
			               
			               
			               
			               /* stgResultString = String.valueOf(endLat)+","+String.valueOf(endLongi)+","+String.valueOf(startLat)+","+String.valueOf(startLongi);
							arrAllValueOfLatLong.add(stgResultString);*/
						}
						routes.add(path);
					}
				
				}
		
		} catch (JSONException e) {
		
			e.printStackTrace();
		}
		
		return routes;
		//return arrAllValueOfLatLong;
		//return null;
	}
	private List<LatLng> decodePoly(String encoded) {

		List<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			/*LatLng p = new LatLng((int) (((double) lat / 1E5) * 1E6),
				 (int) (((double) lng / 1E5) * 1E6));*/
			
			
			 LatLng p = new LatLng((((double) lat / 1E5)),
                     (((double) lng / 1E5)));
			 
			poly.add(p);
		}

		return poly;
	}
}

