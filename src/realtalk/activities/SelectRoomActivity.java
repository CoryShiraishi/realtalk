package realtalk.activities;


import java.util.ArrayList;
import java.util.List;

import realtalk.controller.ChatController;
import realtalk.util.ChatManager;
import realtalk.util.ChatRoomInfo;
import realtalk.util.ChatRoomResultSet;
import realtalk.util.UserInfo;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
//import android.location.Criteria;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.realtalk.R;

/**
 * Activity for selecting a chat room to join
 * 
 * @author Jordan Hazari and Taylor Williams
 *
 */
@SuppressLint("NewApi")
public class SelectRoomActivity extends Activity {
	private static final double HACKED_GPS_DISTANCE_CONSTANT_TO_BE_REMOVED = 500.0;  
	private List<ChatRoomInfo> rgchatroominfo = new ArrayList<ChatRoomInfo>();
	private List<ChatRoomInfo> rgchatroominfoJoined = new ArrayList<ChatRoomInfo>();
	private List<ChatRoomInfo> rgchatroominfoUnjoined = new ArrayList<ChatRoomInfo>();
	private ChatRoomAdapter unJoinedAdapter;
	private ChatRoomAdapter joinedAdapter;
	private Bundle bundleExtras;
	private SharedPreferences sharedpreferencesLoginPrefs;
	private Editor editorLoginPrefs;

	/**
	 * Sets up the activity, and diplays a list of available rooms
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_select_room);
		bundleExtras = getIntent().getExtras();
		sharedpreferencesLoginPrefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
		editorLoginPrefs = sharedpreferencesLoginPrefs.edit();
		
		UserInfo userinfo = bundleExtras.getParcelable("USER");
		String stUser = userinfo.stUserName();
		TextView textviewRoomTitle = (TextView) findViewById(R.id.userTitle);
		textviewRoomTitle.setText(stUser);

		ListView listviewJoined = (ListView) findViewById(R.id.joined_list);
		listviewJoined.setClickable(false);
		
		ListView listviewUnjoined = (ListView) findViewById(R.id.unjoined_list);
		listviewUnjoined.setClickable(false);

		// when a room is clicked, starts a new ChatRoomActivity
        listviewJoined.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	ChatRoomInfo criSelected = rgchatroominfoJoined.get(position);
        		Intent itStartChat = new Intent(SelectRoomActivity.this, ChatRoomActivity.class);
        		itStartChat.putExtras(bundleExtras);
        		itStartChat.putExtra("ROOM", criSelected);
        		SelectRoomActivity.this.startActivity(itStartChat);
        		SelectRoomActivity.this.finish();
            }
        });
        
		// when a room is clicked, starts a new ChatRoomActivity
        listviewUnjoined.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	ChatRoomInfo criSelected = rgchatroominfoUnjoined.get(position);
        		Intent itStartChat = new Intent(SelectRoomActivity.this, ChatRoomActivity.class);
        		itStartChat.putExtras(bundleExtras);
        		itStartChat.putExtra("ROOM", criSelected);
        		SelectRoomActivity.this.startActivity(itStartChat);
        		SelectRoomActivity.this.finish();
            }
        });

		// Binding resources Array to ListAdapter
		unJoinedAdapter = new ChatRoomAdapter(this, R.layout.message_item, rgchatroominfoUnjoined, false);
		listviewUnjoined.setAdapter(unJoinedAdapter);
		
		joinedAdapter = new ChatRoomAdapter(this, R.layout.message_item, rgchatroominfoJoined, true);
		listviewJoined.setAdapter(joinedAdapter);
		
		// REASON for commenting out location code: chatController will get updated indefinitely if location continously changes.
		// Should meet up to agree on a convention or protocol regarding this.
		// TODO
		

		//RIGHT NOW GPS IS HARD TO ENABLE ON THE EMULATOR
		//IF YOU DON'T WANT TO DEAL WITH IT, UNCOMMENT THIS LINE:
		new RoomLoader(this, 0, 0, HACKED_GPS_DISTANCE_CONSTANT_TO_BE_REMOVED).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		
		//todo put a message on screen that is NON BLOCKING!!! that says "loading rooms..."
		//allowing the user to back out if gps is never found.
		
		//location code:
//		LocationManager locationmanager = (LocationManager) getSystemService(LOCATION_SERVICE);
//		final double radiusMeters = 500.0;
//		Criteria criteria = new Criteria();
//		String stBestProvider = locationmanager.getBestProvider(criteria, true);
//		if (stBestProvider == null) {
//			//no location providers, must ask user to enable a provider
//			//TODO set loading text equal to "fail...enable a location provider (wifi/network/gps)"
//			throw new RuntimeException("NO LOCATION PROVIDER");
//		}
//		else {
//			// Define a listener that responds to location updates
//			LocationListener locationListener = new LocationListener() {
//				Location locationUser;
//				public void onLocationChanged(Location location) {
//					// Called when a new location is found by the network location provider.
//					//if new location data is not usable...
//					if (locationUser == null)
//					{
//						locationUser = location;
//						loadRooms(locationUser, radiusMeters);
//					}
//					else if (location.getAccuracy() <= locationUser.getAccuracy()) {
//						locationUser = location;
//						loadRooms(locationUser, radiusMeters);
//					}
//					//TODO stop always listening and updating?
//				}
//
//				public void onStatusChanged(String provider, int status, Bundle extras) {}
//				public void onProviderEnabled(String provider) {}
//				public void onProviderDisabled(String provider) {}
//			};
//
//			// Register the listener with the Location Manager to receive location updates
//			locationmanager.requestLocationUpdates(stBestProvider, 0, 0, locationListener);
//		}
	}
	
	private void getDetails(int position, boolean fJoined) {
		final ChatRoomInfo chatroominfo;
		if(fJoined) {
			chatroominfo = rgchatroominfoJoined.get(position);
		} else {
			chatroominfo = rgchatroominfoUnjoined.get(position);
		}
		
    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		//set title
		alertDialogBuilder.setTitle(chatroominfo.stName());
		
		//set dialog message
		alertDialogBuilder
			.setMessage(Html.fromHtml("<b>Description: </b> " +  chatroominfo.stDescription() + 
									"<br/><br/><b>Active Users: </b> " + chatroominfo.numUsersGet() +
									"<br/><br/><b>Creator: </b> " + chatroominfo.stCreator()))
			.setCancelable(false);
		
		alertDialogBuilder.setNegativeButton("Join", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Intent itStartChat = new Intent(SelectRoomActivity.this, ChatRoomActivity.class);
        		itStartChat.putExtras(bundleExtras);
        		itStartChat.putExtra("ROOM", chatroominfo);
        		SelectRoomActivity.this.startActivity(itStartChat);
        		SelectRoomActivity.this.finish();
			}	
		});
		
		alertDialogBuilder.setPositiveButton("Back", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// User cancelled the dialog
				dialog.cancel();
			}
		});
		
		//create alert dialog
		AlertDialog alertdialogDeleteAcc = alertDialogBuilder.create();
		
		//show alert dialog
		alertdialogDeleteAcc.show();
	}
	
//	private void LoadRooms(Location locationUser, double radiusMeters) {
//		new RoomLoader(this, locationUser.getLatitude(), locationUser.getLongitude(), radiusMeters).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select_room, menu);
		return true;
	}

	/**
	 * Menu options
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//respond to menu item selection
		switch (item.getItemId()){
		case R.id.logout:
			editorLoginPrefs.putBoolean("loggedIn", false);
			editorLoginPrefs.putString("loggedin_username", null);
			editorLoginPrefs.putString("loggedin_password", null);
			editorLoginPrefs.commit();
			Intent itLogin = new Intent(this, LoginActivity.class);
			startActivity(itLogin);
			finish();
			return true;
		case R.id.settings:
			startActivity(new Intent(this, AccountSettingsActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void createRoom(View view) {
		Intent itCreateRoom = new Intent(this, CreateRoomActivity.class);
		itCreateRoom.putExtra("USER", bundleExtras.getParcelable("USER"));
		this.startActivity(itCreateRoom);
	}

	/**
	 * Retrieves the user's available chatrooms and initializes the ChatController
	 * 
	 * @author Jordan Hazari
	 *
	 */
	class RoomLoader extends AsyncTask<String, String, ChatRoomResultSet> {
		private SelectRoomActivity selectroomactivity;
		private double radiusMeters;
		private double latitude;
		private double longitude;

		/**
		 * Constructs a RoomLoader object
		 * 
		 * @param selectroomroomactivity the activity context
		 */
		public RoomLoader(SelectRoomActivity selectroomactivity, 
		        double latitude, double longitude, double radiusMeters) {
			this.selectroomactivity = selectroomactivity;
			this.longitude = longitude;
			this.latitude = latitude;
			this.radiusMeters = radiusMeters;
		}

		/**
		 * Retrieves and displays the available rooms
		 */
		@Override
		protected ChatRoomResultSet doInBackground(String... params) {
		    ChatController.getInstance().fRefresh();
			ChatRoomResultSet crrsNear = ChatManager.crrsNearbyChatrooms
					(latitude, longitude, radiusMeters);

			rgchatroominfo = crrsNear.rgcriGet();

			selectroomactivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					unJoinedAdapter.clear();

					for (int i = 0; i < rgchatroominfo.size(); i++) {
						if (!ChatController.fIsAlreadyJoined(rgchatroominfo.get(i))) {
							unJoinedAdapter.add(rgchatroominfo.get(i));
						} else {
							joinedAdapter.add(rgchatroominfo.get(i));
						}
					}
				}
			});

			return crrsNear;
		}
	}

	private class ChatRoomAdapter extends ArrayAdapter<ChatRoomInfo> {

		private List<ChatRoomInfo> rgchatroominfo;
		private boolean fJoined;

		public ChatRoomAdapter(Context context, int textViewResourceId, List<ChatRoomInfo> rgchatroominfo, boolean fJoined) {
			super(context, textViewResourceId, rgchatroominfo);
			this.rgchatroominfo = rgchatroominfo;
			this.fJoined = fJoined;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.room_item, null);
			}
			ChatRoomInfo chatroominfo = rgchatroominfo.get(position);
			if (chatroominfo != null) {
				TextView textviewTop = (TextView) view.findViewById(R.id.toptext);
				TextView textviewBottom = (TextView) view.findViewById(R.id.bottomtext);
				Button button = (Button) view.findViewById(R.id.detailsButton);
				
				OnClickListener listener = new OnClickListener() {
				    @Override
				    public void onClick(View view) {
				    	getDetails((Integer) view.getTag(), fJoined);
				    }
				};
				
				if (textviewTop != null) {
					textviewTop.setText(chatroominfo.stName());
				}
				if(textviewBottom != null) {
					textviewBottom.setText("\t" + chatroominfo.numUsersGet() + " users");
				}
				if(button != null) {
					button.setTag(position);
					button.setOnClickListener(listener);
					button.setFocusable(false);
				}
			}
			return view;
		}
	}
}
