package com.android.securityapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MergeCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.securityapplication.helpers.DatabaseHelper;
import com.android.securityapplication.helpers.Http;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Class that lists each of the user's stored contacts and current friend requests.
 *
 * <p>Clicking a contact opens up an activity displaying their information.</p>
 *
 * @author Jack Hindmarch
 */
public class ContactsFragment extends Fragment {
	public static final String CONTACT_ID = "ContactsFragment.CONTACT_ID";

	private ContactsAdapter adapter;
	private RecyclerView contactList;
	private BroadcastReceiver broadcastReceiver;

	private TextView noContacts;
	HashMap<String, String> params = new HashMap<>();
	HashMap<String, String> headers = new HashMap<>();
	SharedPreferences prefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		Log.e("===in contacts fragment", "");

		// If it is the first time the user is running the app, show a help dialog
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_contacts, container, false);
//		final DatabaseHelper db = DatabaseHelper.getInstance(getActivity());

		Log.e("=======", "hiiiii");
		noContacts = (TextView) rootView.findViewById(R.id.no_contacts);

		contactList = (RecyclerView) rootView.findViewById(R.id.contacts_list);
		final ArrayList<RowItem> chatList = new ArrayList<>();

		headers.put("Token", prefs.getString("key", null));
		Http stringRequest = new Http(Request.Method.GET, "http://192.168.1.102:3000/" + "api/user/all",
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						try {
							JSONObject payload = new JSONObject(response).getJSONObject("users");
							Log.e("====", payload.toString());
							Iterator<String> keys = payload.keys();

							while(keys.hasNext()) {
								final JSONObject user = payload.getJSONObject(keys.next());
								RowItem item = new RowItem();
								item.setName(user.getString("full_name"));
								item.setId(user.getString("id"));
								item.setItemListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
//
										try {
											params.put("user_id", user.getString("id"));

											Http stringRequest = new Http(Request.Method.POST, "http://192.168.1.102:3000/" + "api/user/user_profile",
													new Response.Listener<String>() {
														@Override
														public void onResponse(String response) {
															JSONObject payload = null;
															try {
																payload = new JSONObject(response);
																Log.e("====", payload.toString());
																DatabaseHelper db = DatabaseHelper.getInstance(getActivity());

																if(db.getPrivateKey(payload.getJSONObject("user").getString("id")) == null){
																	KeyPairGenerator kpg = null;
																	try {
																		kpg = KeyPairGenerator.getInstance("RSA");
																	} catch (NoSuchAlgorithmException e) {
																		e.printStackTrace();
																	}
																	kpg.initialize(1024);
																	KeyPair kp = kpg.genKeyPair();
																	PublicKey publicKey = kp.getPublic();
																	PrivateKey privateKey = kp.getPrivate();
																	db.createPublicKeys(payload.getJSONObject("user").getString("id"), null, Base64.encodeToString(privateKey.getEncoded(), Base64.NO_WRAP));
																	sendtoserver(publicKey, payload.getJSONObject("user").getString("id"));

																}
																String old_key = db.getPublicKey(payload.getJSONObject("user").getString("id"));
																if (!payload.getString("public_key").equals(old_key)) {
																	db.updatePublicKey(payload.getJSONObject("user").getString("id"), payload.getString("public_key"));

																}

																Intent intent = new Intent(getContext(), ChatActivity.class);
																try {
																	intent.putExtra("id", user.getString("id"));
																	//
																} catch (JSONException e) {
																	e.printStackTrace();
																}
																//
																startActivity(intent);





															} catch (JSONException e) {
																e.printStackTrace();
															}
														}
													}, new Response.ErrorListener() {
												@Override
												public void onErrorResponse(VolleyError error) {
												}
											}, headers, params);
											Volley.newRequestQueue(getContext()).add(stringRequest);
										} catch (JSONException e) {
											e.printStackTrace();
										}

									}
//																	}
								});
								chatList.add(item);
							}
							Log.e("=====", Integer.toString(chatList.size()));
							if(chatList.size() == 0){
								noContacts.setVisibility(View.VISIBLE);
								contactList.setVisibility(View.GONE);
							}else{
								noContacts.setVisibility(View.GONE);
								contactList.setVisibility(View.VISIBLE);
							}
							adapter = new ContactsAdapter(getActivity(), chatList);
							contactList.setAdapter(adapter);

							contactList.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
							LinearLayoutManager llm = new LinearLayoutManager(getContext());
							llm.setOrientation(LinearLayoutManager.VERTICAL);
							contactList.setLayoutManager(llm);

						} catch (JSONException e) {
							e.printStackTrace();
						}


					}
				}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
			}
		}, headers, null);
		Volley.newRequestQueue(getContext()).add(stringRequest);

		return rootView;
	}

	public void sendtoserver(PublicKey key, String user_id){
		params.clear();
		params.put("user_id", user_id);
		params.put("public_key", Base64.encodeToString(key.getEncoded(), Base64.NO_WRAP));
		headers.put("Token", prefs.getString("key", null));
		Http stringRequest = new Http(Request.Method.POST, "http://192.168.1.102:3000/" + "api/user/public_key",
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {

					}
				}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
			}
		}, headers, params);
		Volley.newRequestQueue(getContext()).add(stringRequest);

	}

	/**
	 * Check to see if contact list is empty. If so, display "no contacts".
	 *
	 * @param cursor - cursor to check.
	 */
	public void checkIfNoContacts(Cursor cursor) {
		if (!cursor.moveToFirst()) {
			noContacts.setVisibility(TextView.VISIBLE);
		} else {
			noContacts.setVisibility(TextView.GONE);
		}
	}


	/**
	 * Adapter that loads the card layout for each contact in the cursor.
	 */
	class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
		ArrayList<RowItem> items;
		Context CTX;


		public ContactsAdapter(Context context, ArrayList<RowItem> items) {
			this.items = items;
			this.CTX = context;
		}

		@Override
		public ContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_card, parent, false);
			RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			v.setLayoutParams(lp);
//        v.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(parent.getContext(), ChatActivity.class);
//
//            }
//        });
			ContactsAdapter.ViewHolder vh = new ContactsAdapter.ViewHolder(v);
			return vh;
		}

		@Override
		public void onBindViewHolder(ContactsAdapter.ViewHolder holder, int position) {
			holder.name.setText(items.get(position).getName());
			holder.item.setOnClickListener(items.get(position).getItemListener());
		}

		@Override
		public int getItemCount() {
			return items.size();
		}

		public class ViewHolder extends RecyclerView.ViewHolder {
			TextView name;
			RelativeLayout item;

			public ViewHolder(View v) {
				super(v);
				name = (TextView) v.findViewById(R.id.contact_name_card);
				item = (RelativeLayout)v.findViewById(R.id.row_item);
			}
		}
	}
}