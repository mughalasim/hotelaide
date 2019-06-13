package com.hotelaide.main.fragments;

import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.hotelaide.R;
import com.hotelaide.main.adapters.ConversationAdapter;
import com.hotelaide.main.models.ConversationModel;
import com.hotelaide.utils.FBDatabase;
import com.hotelaide.utils.Helpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import static com.hotelaide.BuildConfig.URL_LAST;
import static com.hotelaide.BuildConfig.URL_UNREAD;

public class ConversationFragment extends Fragment {

    private View root_view;
    private final String TAG_LOG = "CONVERSATIONS";
    private Helpers helpers;

    private SwipeRefreshLayout swipe_refresh;
    private DatabaseReference child_ref;
    private RecyclerView recycler_view;
    private ArrayList<ConversationModel> model_list = new ArrayList<>();
    private ConversationAdapter adapter;

    public ConversationFragment() {
    }


    // OVERRIDE FUNCTIONS ==========================================================================
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {
                root_view = inflater.inflate(R.layout.frag_recycler_view, container, false);

                helpers = new Helpers(getActivity());

                findAllViews();

                setupFireBase();

                setListeners();


            } catch (InflateException e) {
                e.printStackTrace();
            }
        } else {
            container.removeView(root_view);
        }
        return root_view;
    }


    // BASIC FUNCTIONS =============================================================================
    private void findAllViews() {
        swipe_refresh = root_view.findViewById(R.id.swipe_refresh);
        recycler_view = root_view.findViewById(R.id.recycler_view);
        adapter = new ConversationAdapter(model_list);
        recycler_view.setAdapter(adapter);
        recycler_view.setHasFixedSize(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(layoutManager);
    }

    private void setListeners() {
        helpers.animateSwipeRefresh(swipe_refresh);
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchMessageList();
            }
        });

        child_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                swipe_refresh.setRefreshing(false);
                setFromDataSnapShotArray(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    // FIRE BASE METHODS ===========================================================================
    private void setupFireBase() {
        FBDatabase.updateUserDetails();
        child_ref = FBDatabase.getURLConversation();
    }

    private void fetchMessageList() {
        swipe_refresh.setRefreshing(true);
        child_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                swipe_refresh.setRefreshing(false);
                setFromDataSnapShotArray(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // PARSING METHODS =============================================================================
    private void setFromDataSnapShotArray(DataSnapshot dataSnapshot) {
        try {
            Gson gson = new Gson();
            JSONObject messages_object = new JSONObject(gson.toJson(dataSnapshot.getValue()));
            Iterator<String> keys = messages_object.keys();

            model_list.clear();

            while (keys.hasNext()) {
                String key = keys.next();
                Helpers.logThis(TAG_LOG, key);

                JSONObject object = messages_object.getJSONObject(key);

                ConversationModel conversationModel = new ConversationModel();
                conversationModel.last_message = object.getString(URL_LAST);
                if (object.isNull(URL_UNREAD)) {
                    conversationModel.unread_messages = 0;
                } else {
                    conversationModel.unread_messages = object.getInt(URL_UNREAD);
                }

                conversationModel.from_id = Integer.parseInt(key);

                model_list.add(conversationModel);

            }

            if (model_list.size() < 1) {
                noListItems();
            } else {
                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            noListItems();
        } catch (Exception e) {
            e.printStackTrace();
            noListItems();
        }
    }

    private void noListItems() {
        recycler_view.invalidate();
        model_list.clear();
        model_list.add(new ConversationModel());
        adapter.notifyDataSetChanged();
    }


}