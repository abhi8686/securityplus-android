package com.android.securityapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.android.securityapplication.helpers.ConversationRowItem;
import com.android.securityapplication.helpers.Http;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Class that handles functionality of the conversations list screen.
 *
 * <p>Simply fetches all the current conversations with each contact, sorted by
 * last activity time.</p>
 *
 * <p>Clicking a conversation opens up its conversation activity.</p>
 *
 * @author Jack Hindmarch
 */
public class ConversationsFragment extends Fragment
{
	private ConversationsAdapter adapter;
	private RecyclerView conversationList;
	private BroadcastReceiver broadcastReceiver;
	private TextView noConversations;
	HashMap<String, String> headers = new HashMap<>();
	SharedPreferences preferences;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

	}


	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState)
	{
		final View rootView = inflater.inflate(R.layout.fragment_conversations, container, false);

		// Text view saying "no conversations" to be displayed if there are none stored
		noConversations = (TextView) rootView.findViewById(R.id.no_conversations);

		// Initialise and fill up contact list
		conversationList =  (RecyclerView)rootView.findViewById(R.id.conversations_list);
//		DatabaseHelper db = DatabaseHelper.getInstance(getActivity());
//		Cursor conversations = db.getMostRecentMessages();
		//conversations.moveToFirst();
//		checkIfNoConversations(conversations);



		ArrayList<ConversationRowItem> chatList = new ArrayList<>();

		ConversationRowItem item = new ConversationRowItem(getContext());
//		item.setName();
		headers.put("Token", preferences.getString("key", null));
//		Http stringRequest = new Http(Request.Method.GET, "https://securityplus12.herokuapp.com/" + "api/user/messages/check",
//				new Response.Listener<String>() {
//					@Override
//					public void onResponse(String response) {
//						try {
//							JSONObject payload = new JSONObject(response).getJSONObject("messages");
//							Log.e("====", payload.toString());
//							Iterator<String> keys = payload.keys();
//							while(keys.hasNext()) {
//								JSONObject user = payload.getJSONObject(keys.next());
//								RowItem item = new RowItem();
//								item.setName(user.getString("full_name"));
//								chatList.add(item);
//							}
//							if(chatList.size() == 0){
//								noContacts.setVisibility(View.VISIBLE);
//								contactList.setVisibility(View.GONE);
//							}else{
//								noContacts.setVisibility(View.GONE);
//								contactList.setVisibility(View.VISIBLE);
//							}
//							adapter = new ConversationsAdapter(getContext(), chatList);
//							conversationList.setAdapter(adapter);
//
//							conversationList.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
//							LinearLayoutManager llm = new LinearLayoutManager(getContext());
//							llm.setOrientation(LinearLayoutManager.VERTICAL);
//							conversationList.setLayoutManager(llm);
//
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//
//
//					}
//				}, new Response.ErrorListener() {
//			@Override
//			public void onErrorResponse(VolleyError error) {
//			}
//		}, headers, null);
//		Volley.newRequestQueue(getContext()).add(stringRequest);
//
//


		// Listen for click events on contact cards
//		conversationList.setOnItemClickListener(new AdapterView.OnItemClickListener()
//		{
//			@Override
//			public void onItemClick(AdapterView<?> adapterView, View view, int i, long id)
//			{
//				DatabaseHelper db = DatabaseHelper.getInstance(getActivity());
//				long uid = db.getUIDFromMID(id);
//				Intent intent = new Intent(getActivity(), ConversationActivity.class);
//				intent.putExtra("uid", uid);
//				startActivity(intent);
//			}
//		});

		return rootView;
	}

	/**
	 * Check to see if conversation list is empty. If so, display "no conversations".
	 *
	 * @param cursor - cursor to check.
	 */
	public void checkIfNoConversations(Cursor cursor)
	{
		if(!cursor.moveToFirst())
		{
			noConversations.setVisibility(TextView.VISIBLE);
		}
		else
		{
			noConversations.setVisibility(TextView.GONE);
		}
	}



	public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ViewHolder>  {
		ArrayList<ConversationRowItem> items;
		Context CTX;

		public ConversationsAdapter(Context context, ArrayList<ConversationRowItem> data){
			items = data;
			this.CTX = context;
		}

		@Override
		public ConversationsAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_layout, parent, false);
			RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
			v.setLayoutParams(lp);
//        v.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(parent.getContext(), ChatActivity.class);
//
//            }
//        });
			ConversationsAdapter.ViewHolder vh = new ConversationsAdapter.ViewHolder(v);
			return vh;
		}

		@Override
		public void onBindViewHolder(ConversationsAdapter.ViewHolder holder, int position) {
			holder.name.setText(items.get(position).getName());
			holder.body.setText(items.get(position).getHeading());
			holder.time.setText(items.get(position).getTime());

			if(items.get(position).getImage() == null){
//            holder.photo.downloadImage(new String(R.drawable.default_profile));
				Picasso.with(CTX).load(R.drawable.default_profile).into(holder.photo);
				Log.e("IMAGE", "no default image");
			}else {

				holder.photo.downloadImage(items.get(position).getImage());
			}
			if(items.get(position).getOnline() == false) {
				if(items.get(position).getFrom().equals("Agent"))
					holder.online.setVisibility(View.GONE);
				else
					holder.online.setImageDrawable(CTX.getResources().getDrawable(R.drawable.engaged));
			}
			else
				holder.online.setImageDrawable(CTX.getResources().getDrawable(R.drawable.online));
//        holder.photo.setImageBitmap(BitmapFactory.decodeResource(null, items.get(position).getImage()));
			// holder.photo.setImageResource(R.drawable.chat);
			holder.item.setOnClickListener(items.get(position).getItemListener());
		}

		@Override
		public int getItemCount() {
			return items.size();
		}

		public class ViewHolder extends RecyclerView.ViewHolder {
			TextView name, body, time;
			RoundedImageView photo;
			ImageView online;
			RelativeLayout item;

			public ViewHolder(View v){
				super(v);
				name = (TextView)v.findViewById(R.id.name);
				body = (TextView)v.findViewById(R.id.heading);
				photo = (RoundedImageView)v.findViewById(R.id.image);
				online = (ImageView)v.findViewById(R.id.online);
				time = (TextView)v.findViewById(R.id.time);
				item = (RelativeLayout)v.findViewById(R.id.chat_item);
			}
		}
	}
}