package com.hotelaide.main.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.hotelaide.BuildConfig;
import com.hotelaide.R;
import com.hotelaide.main.models.NewsFeedModel;
import com.hotelaide.utils.Helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;


public class NewsFeedFragment extends Fragment {

    private View root_view;
    private Helpers helpers;
    private final String
            TAG_LOG = "NEWS FEED";

    // SEARCH ADAPTER ITEMS ------------------------------------------------------------------------
    private SwipeRefreshLayout swipe_refresh;
    private LinearLayoutManager layoutManager;
    private RecyclerView recycler_view;
    private ArrayList<NewsFeedModel> model_list = new ArrayList<>();
    private NewsFeedAdapter adapter;
    private String NEWS_FEED_URL = "";

    public NewsFeedFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {
                Bundle bundle = this.getArguments();
                if (bundle != null) {
                    NEWS_FEED_URL = bundle.getString("EXTRA_STRING");

                    root_view = inflater.inflate(R.layout.frag_recycler_view, container, false);

                    helpers = new Helpers(getActivity());

                    findAllViews();

                    setListeners();

                }

            } catch (InflateException e) {
                e.printStackTrace();
            }
        } else {
            container.removeView(root_view);
        }
        return root_view;
    }

    @Override
    public void onResume() {
        super.onResume();
        asyncGetRssFeeds();
    }

    // BASIC FUNCTIONS =============================================================================
    private void findAllViews() {
        // SEARCH FUNCTIONALITY --------------------------------------------------------------------
        swipe_refresh = root_view.findViewById(R.id.swipe_refresh);
        recycler_view = root_view.findViewById(R.id.recycler_view);
        adapter = new NewsFeedAdapter(model_list);
        recycler_view.setAdapter(adapter);
        recycler_view.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(layoutManager);

    }

    private void setListeners() {
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                asyncGetRssFeeds();
            }
        });
    }

    private void noListItems() {
        recycler_view.invalidate();
        model_list.clear();
        model_list.add(new NewsFeedModel());
        adapter.notifyDataSetChanged();
    }

    // ASYNC FETCH ALL NEWS FEEDS ================================================================
    private void asyncGetRssFeeds() {
        NewsFeedInterface newsFeedInterface = NewsFeedInterface.retrofit.create(NewsFeedInterface.class);
        Call<JsonObject> call = newsFeedInterface.getNewsFeed(NEWS_FEED_URL);
        swipe_refresh.setRefreshing(true);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (getActivity() != null) {
                    swipe_refresh.setRefreshing(false);
                    try {
                        JSONObject main = new JSONObject(String.valueOf(response.body()));
                        Helpers.logThis(TAG_LOG, main.toString());

                        JSONArray items = main.getJSONArray("items");

                        int length = items.length();

                        if (length > 0) {

                            Helpers.logThis(TAG_LOG, "LENGTH: " + length);

                            model_list.clear();

                            for (int i = 0; i < length; i++) {
                                JSONObject object = items.getJSONObject(i);
                                NewsFeedModel newsFeedModel = new NewsFeedModel();
                                newsFeedModel.id = i + 1;
                                newsFeedModel.title = object.getString("title");
                                newsFeedModel.desc = object.getString("description");
                                newsFeedModel.content = object.getString("content");
                                newsFeedModel.pub_date = object.getString("pubDate");
                                newsFeedModel.link = object.getString("link");

                                model_list.add(newsFeedModel);
                            }

                            adapter.notifyDataSetChanged();

                        } else {
                            noListItems();
                        }

                    } catch (JSONException e) {
                        helpers.ToastMessage(getActivity(), getString(R.string.error_server));
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                if (getActivity() != null) {
                    swipe_refresh.setRefreshing(false);
                    Helpers.logThis(TAG_LOG, t.toString());
                    if (helpers.validateInternetConnection()) {
                        helpers.ToastMessage(getActivity(), getString(R.string.error_server));
                    } else {
                        helpers.ToastMessage(getActivity(), getString(R.string.error_connection));
                    }
                }
            }
        });
    }

    // ADAPTER CLASS ===============================================================================
    private class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.ViewHolder> {
        private final ArrayList<NewsFeedModel> newsFeedModels;
        private Context context;

        class ViewHolder extends RecyclerView.ViewHolder {

            CardView
                    list_item;
            RelativeLayout
                    no_list_item;
            final TextView
                    txt_no_results,
                    txt_id,
                    txt_title,
                    txt_desc,
                    txt_content,
                    txt_pub_date,
                    txt_link;

            ViewHolder(View v) {
                super(v);
                // LIST ITEM
                list_item = v.findViewById(R.id.list_item);
                txt_id = v.findViewById(R.id.txt_id);
                txt_title = v.findViewById(R.id.txt_title);
                txt_desc = v.findViewById(R.id.txt_desc);
                txt_content = v.findViewById(R.id.txt_content);
                txt_pub_date = v.findViewById(R.id.txt_pub_date);
                txt_link = v.findViewById(R.id.txt_link);

                // NO LIST ITEM
                no_list_item = v.findViewById(R.id.rl_no_list_items);
                txt_no_results = v.findViewById(R.id.txt_no_results);

            }

        }

        NewsFeedAdapter(ArrayList<NewsFeedModel> newsFeedModels) {
            this.newsFeedModels = newsFeedModels;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_news_feed, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            context = holder.itemView.getContext();
            helpers = new Helpers(context);

            final NewsFeedModel newsFeedModel = newsFeedModels.get(position);

            if (newsFeedModel.id == 0) {
                holder.no_list_item.setVisibility(View.VISIBLE);
                holder.list_item.setVisibility(View.GONE);
                holder.txt_no_results.setText(R.string.error_no_news);

            } else {
                holder.no_list_item.setVisibility(View.GONE);
                holder.list_item.setVisibility(View.VISIBLE);

                holder.txt_id.setText(String.valueOf(newsFeedModel.id));
                holder.txt_title.setText(newsFeedModel.title);
                holder.txt_desc.setText(newsFeedModel.desc);
                holder.txt_content.setText(newsFeedModel.content);
                holder.txt_pub_date.setText(newsFeedModel.pub_date);
                holder.txt_link.setText(newsFeedModel.link);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!newsFeedModel.link.equals("")) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(newsFeedModel.link)));
                        }
                    }
                });

            }
        }

        @Override
        public int getItemCount() {
            return newsFeedModels.size();
        }

        void updateData(ArrayList<NewsFeedModel> view_model) {
            newsFeedModels.clear();
            newsFeedModels.addAll(view_model);
            notifyDataSetChanged();
        }

    }

    private interface NewsFeedInterface {
        String TAG_LOG = "CALL: RSS";

        OkHttpClient okClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .method(original.method(), original.body())
                                .build();

                        okhttp3.Response response = chain.proceed(request);

                        Helpers.logThis(TAG_LOG, "URL: " + request.url());
                        Helpers.logThis(TAG_LOG, "CODE:" + response.code());
                        if (response.code() == 401) {
                            Helpers.logThis(TAG_LOG, "MESSAGE: " + response.message());
                        } else if (response.code() > 300) {
                            Helpers.logThis(TAG_LOG, "MESSAGE: " + response.message());
                        }

                        return response;
                    }
                })
                .connectTimeout(BuildConfig.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(BuildConfig.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(BuildConfig.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(" https://api.rss2json.com/v1/")
                .client(okClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        // AUTHENTICATE FUNCTION =======================================================================
        @GET("api.json")
        Call<JsonObject> getNewsFeed(
                @Query("rss_url") String url
        );


    }

}