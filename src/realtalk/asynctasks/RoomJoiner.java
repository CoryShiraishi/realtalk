package realtalk.asynctasks;

import realtalk.activities.ChatRoomActivity;
import realtalk.util.ChatRoomInfo;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

import com.realtalk.R;

/**
 * Joins a chat room
 * 
 * @author Jordan Hazari
 *
 */
public class RoomJoiner extends AsyncTask<String, String, Boolean> {
	/**
	 * 
	 */
	private ChatRoomActivity chatRoomActivity;
	private ChatRoomInfo chatroominfo;
	private Activity activity;
	
	/**
	 * Constructs a RoomCreator object
	 * 
	 * @param chatroominfo the room to create/join
	 * @param chatRoomActivity
	 */
	public RoomJoiner(ChatRoomActivity chatRoomActivity, Activity activity, ChatRoomInfo chatroominfo) {
		this.chatRoomActivity = chatRoomActivity;
		this.chatroominfo = chatroominfo;
		this.activity = activity;
	}
	
	/**
	 * Displays a popup dialogue while joining the room
	 */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.chatRoomActivity.setProgressdialog(new ProgressDialog(this.chatRoomActivity));
        this.chatRoomActivity.getProgressdialog().setMessage(chatRoomActivity.getResources().getString(R.string.entering_room));
        this.chatRoomActivity.getProgressdialog().setIndeterminate(false);
        this.chatRoomActivity.getProgressdialog().setCancelable(true);
        this.chatRoomActivity.getProgressdialog().setCanceledOnTouchOutside(false);
        this.chatRoomActivity.getProgressdialog().show();
    }

    /**
     * Adds the room, or joins it if it already exists
     * @return 
     */
	@Override
	protected Boolean doInBackground(String... params) {
		if (this.chatRoomActivity.getChatController().fIsAlreadyJoined(chatroominfo)) {
			return true;
		} else {
			ConnectivityManager connectivitymanager = (ConnectivityManager) this.chatRoomActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkinfo = connectivitymanager.getActiveNetworkInfo();
            if (networkinfo != null && networkinfo.isConnected()) {
				return this.chatRoomActivity.getChatController().joinRoom(chatroominfo, this.chatRoomActivity.getfAnon());
			}
            return null;
		}
	}
	
	/**
	 * Closes the popup dialogue
	 */
	@Override
    protected void onPostExecute(Boolean success) {
        this.chatRoomActivity.getProgressdialog().dismiss();
        if (success == null) {
        	Toast toast = Toast.makeText(this.chatRoomActivity.getApplicationContext(), R.string.network_failed, Toast.LENGTH_LONG);
			toast.show();
        } else if (!success) {
            Toast serverToast = Toast.makeText(activity, R.string.join_room_failed, Toast.LENGTH_LONG);
            serverToast.show();
        } else {
            this.chatRoomActivity.createGCMMessageLoader();
        }
	}
}
