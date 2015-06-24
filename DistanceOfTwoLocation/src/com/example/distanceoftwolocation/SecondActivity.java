package com.example.distanceoftwolocation;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

public class SecondActivity extends FragmentActivity implements SearchView.OnQueryTextListener{
	
	private static final String TAG="SecondActivity";
	GoogleMap googlemap;
	
	int oneTime=0;
	LatLng latLng;
	Double myLocLat, myLocLong, lat, longi, a, c, distance, dLat, dLng;
	double earthRadius = 6371000; //meters
	Marker pos, pos1;
	TextView tvShowData;
	private SearchView mSearchView;
	protected static String locationCity;
	MenuItem searchItem;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(isGooglePlay()){
			
			setContentView(R.layout.activity_second);
			
			}
			setUpMapIfNeeded();
			googlemap.setOnMapClickListener(new OnMapClickListener() {
			
			@Override
			public void onMapClick(LatLng arg0) {
			
				Log.i(TAG,"onMapClick method");
				latLng= arg0;
				getDistance(latLng);
			}
		});
		tvShowData=(TextView) findViewById(R.id.textView1);
	}

	private Boolean isGooglePlay(){
		int status=GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (status == ConnectionResult.SUCCESS){

			return true;	
		}else {
			Toast.makeText(this, "google play is not installed", Toast.LENGTH_SHORT).show();
		
		}
		return false;
	}
	private void setUpMapIfNeeded() {

		if(googlemap==null){
		
			googlemap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

		}
		if(googlemap != null){
			
			/*LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);*/
			

			
			LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
		}
		
	}
	private final LocationListener listener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            // A new location update is received.  Do something useful with it.  
            Log.i(TAG,"Location Listener start");
            if(oneTime==0){
            	googlemap.setMyLocationEnabled(true);
        		googlemap.setMapType(GoogleMap.MAP_TYPE_HYBRID);//set map type this is sattelite map
        		
            	
            
            	String coordinates[] = {""+location.getLatitude(), ""+location.getLongitude()};
            	myLocLat = Double.parseDouble(coordinates[0]);
            	myLocLong = Double.parseDouble(coordinates[1]);
            	LatLng ll=new LatLng(myLocLat, myLocLong);//create latlng object for current location
            	googlemap.moveCamera(CameraUpdateFactory.newLatLng(ll));//show current location in google map
    			googlemap.animateCamera(CameraUpdateFactory.zoomTo(15));//zoom in google map value 2-21
    			//pos=googlemap.addMarker(new MarkerOptions().position(new LatLng(myLocLat, myLocLong)).snippet("latitude: "+myLocLat+"longitude: "+myLocLong).title("youe are here"));
				
    			 Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
    			 List<Address> addresses = null;
				try {
					addresses = gcd.getFromLocation(location.getLatitude(),location.getLongitude(), 1);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			 //  String text=(addresses!=null)?"City : "+addresses.get(0).getSubLocality()+"\n Country : "+addresses.get(0).getCountryName():"Unknown Location";
				 
				String text=(addresses!=null)? addresses.get(0).getAddressLine(2):"Unknown Location";
   			      
				locationCity =  text.substring(0,text.indexOf(","));
    			
    			pos=googlemap.addMarker(new MarkerOptions().position(new LatLng(myLocLat, myLocLong)).snippet("latitude: "+myLocLat+"longitude: "+myLocLong).title("My location: "+locationCity));
       			pos.showInfoWindow();
			
    	
    			oneTime++;
            }
        	

        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i(TAG,"Location disable");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i(TAG,"Location enabldes");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };
  
    public void getDistance(LatLng tappedLatLngValue){
    	
		
		lat=tappedLatLngValue.latitude;
		longi=tappedLatLngValue.longitude;
		dLat=Math.toRadians(lat-myLocLat);
		dLng=Math.toRadians(longi-myLocLong);
		a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(myLocLat)) * Math.cos(Math.toRadians(lat)) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
		c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		distance = (earthRadius * c)/1000;
		pos1=googlemap.addMarker(new MarkerOptions().position(new LatLng(lat, longi)).title("Distance to travel: "+ String.format( "%.2f", distance )+"km").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
		pos1.showInfoWindow();
	
		//drawLine();
		
		
		
  }
   /* public void drawLine(double firstLat, double FirstLong, double SecondLat, double SecondLong){
    	 LatLng prev = new LatLng(firstLat, FirstLong);
	     LatLng my = new LatLng(SecondLat, SecondLong);
	        
		Polyline line = googlemap.addPolyline(new PolylineOptions().add(prev, my)
                .width(5).color(Color.BLUE));
		line.setVisible(true);
    }*/
    
public void descriptionMethod(View v){
		
		
		// Origin of route
        String str_origin = "origin="+myLocLat+","+myLocLong;
 
        // Destination of route
        String str_dest = "destination="+lat+","+longi;
 
        // Sensor enabled
        String sensor = "sensor=false";
 
        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;
 
        // Output format
        String output = "json";
 
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
        GetjasonDataForDirection gjd = new GetjasonDataForDirection();
		gjd.execute(url);
		
	} 

	class GetjasonDataForDirection extends AsyncTask<String, Void, List<List<HashMap<String,String>>>>{

		@Override
		protected List<List<HashMap<String, String>>> doInBackground(String... params) {
			// TODO Auto-generated method stub
			return downloadUrl(params[0]);
		}
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			// TODO Auto-generated method stub
			//super.onPostExecute(result);
			Log.i(TAG, result.toString());
			 ArrayList<LatLng> points = null;
	            PolylineOptions lineOptions = null;
	 
	            // Traversing through all the routes
	            for(int i=0;i<result.size();i++){
	                points = new ArrayList<LatLng>();
	                lineOptions = new PolylineOptions();
	 
	                // Fetching i-th route
	                List<HashMap<String, String>> path = result.get(i);
	 
	                // Fetching all the points in i-th route
	                for(int j=0;j<path.size();j++){
	                    HashMap<String,String> point = path.get(j);
	 
	                    double lat = Double.parseDouble(point.get("lat"));
	                    double lng = Double.parseDouble(point.get("lng"));
	                    LatLng position = new LatLng(lat, lng);
	 
	                    points.add(position);
	                }
	 
	                // Adding all the points in the route to LineOptions
	                lineOptions.addAll(points);
	                lineOptions.width(2);
	                lineOptions.color(Color.RED);
	            }
	 
	             // Drawing polyline in the Google Map for the i-th route
	            googlemap.addPolyline(lineOptions);
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		
	}
	public List<List<HashMap<String, String>>> downloadUrl(String downloadData){
		InputStream is=null;
		BufferedReader br;
		String data=null;
		
		try {
			URL url=new URL(downloadData);
			HttpURLConnection conn=(HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.connect();
			conn.getResponseCode();
			is=conn.getInputStream();
			br= new BufferedReader(new InputStreamReader(is));
			StringBuffer sb=new StringBuffer();
			String line="";
			String newLine=System.getProperty("line.seperator");
			while((line=br.readLine())!=null){
				  sb.append(line+newLine);
			  }
			  br.close();
			  data=sb.toString().replaceAll("null", "");
			 Log.i(TAG,"data from url"+data);
			  DirectionsJSONParser djp=new DirectionsJSONParser();
			  
			return djp.dataDirection(data);
			  
		} catch (Exception e) {
			return null;
		} 
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		
		getMenuInflater().inflate(R.menu.second, menu);
		
     	searchItem = menu.findItem(R.id.action_search);
     	mSearchView = (SearchView) searchItem.getActionView();
         setupSearchView(searchItem);

   return true;
	}
	private void setupSearchView(MenuItem searchItem) {
		// TODO Auto-generated method stub
		 if (isAlwaysExpanded()) {
	          //  mSearchView.setIconifiedByDefault(false);
	            if (mSearchView.getVisibility() == View.VISIBLE) {
				 mSearchView.setIconified(true);// to Expand the SearchView when clicked
				 mSearchView.setVisibility(View.INVISIBLE);
	            } else {
	            	mSearchView.setVisibility(View.VISIBLE);
	            	mSearchView.setIconified(false);
	            }
	        } else {
	            searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
	        }

	        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	        if (searchManager != null) {
	            List<SearchableInfo> searchables = searchManager.getSearchablesInGlobalSearch();

	            SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
	            for (SearchableInfo inf : searchables) {
	                if (inf.getSuggestAuthority() != null
	                        && inf.getSuggestAuthority().startsWith("applications")) {
	                    info = inf;
	                }
	            }
	            mSearchView.setSearchableInfo(info);
	        }

	        mSearchView.setOnQueryTextListener(this);
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onClose() {
        
        return false;
    }

    protected boolean isAlwaysExpanded() {
        return false;
    }
}
