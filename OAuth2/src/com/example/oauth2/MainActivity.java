package com.example.oauth2;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.os.Bundle;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;


import com.google.android.gms.common.GooglePlayServicesUtil;

public class MainActivity extends Activity {
    
	public AccountManager accountManager;
	public String token;
	public Account account;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		accountManager = AccountManager.get(this);
		Account[] accounts = accountManager.getAccountsByType("com.google");
		account = accounts[1];
		Toast.makeText(this, accounts[1].name, Toast.LENGTH_LONG).show();
		
		initializeAuthTokenForAccount(account);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
//	@Override
//	protected Dialog onCreateDialog(int id) {
//	  switch (id) {
//	    case DIALOG_ACCOUNTS:
//	      AlertDialog.Builder builder = new AlertDialog.Builder(this);
//	      builder.setTitle("Select a Google account");
//	      final Account[] accounts = accountManager.getAccountsByType("com.google");
//	      final int size = accounts.length;
//	      String[] names = new String[size][];
//	      for (int i = 0; i < size; i++) {
//	        names[[]i] = accounts[[]i].name;
//	      }
//	      builder.setItems(names, new DialogInterface.OnClickListener() {
//	        public void onClick(DialogInterface dialog, int which) {
//	          // Stuff to do when the account is selected by the user
//	          gotAccount(accounts[[]which]);
//	        }
//	      });
//	      return builder.create();
//	  }
//	  return null;
//	}
	
	public void invalidateAuthToken( String token){
		accountManager.invalidateAuthToken("com.google", token);
	}
	
	public void initializeAuthTokenForAccount(Account account){
		accountManager.getAuthToken(account, "oauth2:https://www.googleapis.com/auth/tasks", null, this, new AccountManagerCallback<Bundle>() {
		    public void run(AccountManagerFuture<Bundle> future) {
		      try {
		        // If the user has authorized your application to use the tasks API
		        // a token is available.
		        token = future.getResult().getString(AccountManager.KEY_AUTHTOKEN);
		        
		        //Log.i("auth", token);
		        Toast.makeText(getBaseContext(), token, Toast.LENGTH_LONG).show();
		        
		        // Now you can use the Tasks API...
		        useTasksAPI(token);
		      } catch (OperationCanceledException e) {
		        // TODO: The user has denied you access to the API, you should handle that
		      } catch (Exception e) {
		        //handleException(e);
		      }
		    }
		  }, null);
		
	}
	
	public void useTasksAPI(String accessToken) {
		
		Toast.makeText(this, "use api!!", Toast.LENGTH_LONG).show();

		  // Setting up the Tasks API Service
//		  HttpTransport transport = AndroidHttp.newCompatibleTransport();
//		  AccessProtectedResource accessProtectedResource = new GoogleAccessProtectedResource(accessToken);
//		  Tasks service = new Tasks(transport, accessProtectedResource, new JacksonFactory());
//		  service.accessKey = "AIzaSyB7gm3XDcsAHt4ScQWPCgP3HmZXU0AqUqs";
//		  service.setApplicationName("Google-TasksSample/1.0");
       try{
		URL url = new URL("https://www.googleapis.com/tasks/v1/users/@me/lists/?key=" + "My_Api_Key");
		URLConnection conn = (HttpURLConnection) url.openConnection();
        //((HttpURLConnection)conn).setRequestMethod("GET");

//		conn.addRequestProperty("client_id", your client id);
//		conn.addRequestProperty("client_secret", your client secret);
		conn.setRequestProperty("Authorization", "OAuth " + accessToken);
		
		  // TODO: now use the service to query the Tasks API
		if(((HttpURLConnection) conn).getResponseCode()==200){
			Toast.makeText(this, "success!!", Toast.LENGTH_LONG).show();
			InputStream is = conn.getInputStream();

			int bytesRead = -1;
			byte[] buffer = new byte[1024];
			while ((bytesRead = is.read(buffer)) >= 0) {
			  // process the buffer, "bytesRead" have been read, no more, no less
				Toast.makeText(this, new String(buffer), Toast.LENGTH_LONG).show();
				Log.i("auth_token", token);
			}
		}
	    if(((HttpURLConnection)conn).getResponseCode()==401){
			invalidateAuthToken(token);
			initializeAuthTokenForAccount(account);
			Toast.makeText(this, "error "+((HttpURLConnection)conn).getResponseCode(), Toast.LENGTH_LONG).show();
		}
       }
       catch(Exception e){
			Toast.makeText(this,"exception :"+ e.getMessage(), Toast.LENGTH_LONG).show();
			invalidateAuthToken(token);
			initializeAuthTokenForAccount(account);

       }
		}

}
