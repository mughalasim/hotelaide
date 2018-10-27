package com.hotelaide.main.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.hotelaide.R;
import com.hotelaide.interfaces.UserInterface;
import com.hotelaide.main.adapters.DocumentAdapter;
import com.hotelaide.main.models.DocumentModel;
import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.SharedPrefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.hotelaide.utils.StaticVariables.INT_PERMISSIONS_STORAGE;
import static com.hotelaide.utils.StaticVariables.USER_ID;


public class DocumentsFragment extends Fragment {

    private View root_view;
    private FloatingActionButton btn_upload;
    private Helpers helpers;
    private final String
            TAG_LOG = "DOCUMENTS";
    private final int
            INT_REQUEST_CODE = 2323;

    private TextView
            txt_no_results;
    private RelativeLayout rl_no_list_items;

    private Database db;


    private RecyclerView recycler_view;
    private ArrayList<DocumentModel> model_list = new ArrayList<>();
    private DocumentAdapter adapter;

    public DocumentsFragment() {
    }

    // OVERRIDE METHODS ============================================================================
    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {
                root_view = inflater.inflate(R.layout.frag_profile_documents, container, false);

                db = new Database();

                helpers = new Helpers(getActivity());

                findAllViews();

                Bundle bundle = this.getArguments();
                if (bundle != null) {
                    Helpers.LogThis(TAG_LOG, "HAS BUNDLES");
                    btn_upload.setVisibility(View.GONE);
                }

                setListeners();

                populateDocumentsFromDB();


            } catch (InflateException e) {
                Helpers.LogThis(TAG_LOG, e.toString());
            }
        } else {
            ((ViewGroup) container.getParent()).removeView(root_view);
        }
        return root_view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, getActivity());
        helpers.myPermissionsDialog(getActivity(), grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case INT_REQUEST_CODE:
                if (resultCode == RESULT_OK && data.getData() != null && getActivity() != null) {

                    Uri resultUri = data.getData();
                    File file = new File(resultUri.getPath());

                    MultipartBody.Part partFile = MultipartBody.Part.createFormData("document",
                            file.getName(), RequestBody.create(MediaType.parse("application/pdf"), file));

                    asyncUploadDocument(partFile);

                } else {
                    helpers.ToastMessage(getActivity(), "Invalid file selected");
                }
                break;
        }
    }


    // BASIC FUNCTIONS =============================================================================
    private void findAllViews() {
        txt_no_results = root_view.findViewById(R.id.txt_no_results);
        rl_no_list_items = root_view.findViewById(R.id.rl_no_list_items);
        txt_no_results.setText("No Documents uploaded");

        recycler_view = root_view.findViewById(R.id.recycler_view);
        adapter = new DocumentAdapter(model_list);
        recycler_view.setAdapter(adapter);
        recycler_view.setHasFixedSize(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(layoutManager);

        btn_upload = root_view.findViewById(R.id.btn_upload);

    }

    private void setListeners() {
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDocuments();
            }
        });

    }

    private void populateDocumentsFromDB() {
        if (adapter.getItemCount() > 0)
            adapter.removeLoadingItem();
        rl_no_list_items.setVisibility(View.GONE);
        model_list.clear();
        model_list = db.getAllDocuments();
        recycler_view.invalidate();
        adapter.updateData(model_list);
        adapter.notifyDataSetChanged();
        if (model_list.size() <= 0) {
            noListItems();
        }
    }

    private void noListItems() {
        rl_no_list_items.setVisibility(View.VISIBLE);
        recycler_view.invalidate();
        model_list.clear();
        DocumentModel documentModel = new DocumentModel();
        model_list.add(documentModel);
        adapter.notifyDataSetChanged();
    }

    @AfterPermissionGranted(INT_PERMISSIONS_STORAGE)
    private void openDocuments() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};
        if (getActivity() != null && EasyPermissions.hasPermissions(getActivity(), perms)) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            startActivityForResult(intent, INT_REQUEST_CODE);
        } else if (getActivity() != null) {
            EasyPermissions.requestPermissions(getActivity(), getString(R.string.rationale_storage),
                    INT_PERMISSIONS_STORAGE, perms);
        }
    }

    private void setLoadingItem() {
        model_list.clear();
        model_list = db.getAllDocuments();
        rl_no_list_items.setVisibility(View.GONE);
        DocumentModel documentModel = new DocumentModel();
        documentModel.id = 0;
        documentModel.name = "Loading...";
        documentModel.file_url = "kwdvjedvedv";
        documentModel.date_uploaded = "This may take a moment";
//        documentModel.image = "https://cdn.pixabay.com/photo/2016/01/03/00/43/upload-1118929_960_720.png";
        documentModel.image = "https://gifimage.net/wp-content/uploads/2018/06/upload-gif-13.gif";
        model_list.add(documentModel);
        adapter.updateData(model_list);
    }


    // UPLOAD DOCUMENT ASYNC FUNCTION ==============================================================
    private void asyncUploadDocument(final MultipartBody.Part partFile) {

        UserInterface userInterface = UserInterface.retrofit.create(UserInterface.class);
        Call<JsonObject> call = userInterface.setUserDocument(
                SharedPrefs.getInt(USER_ID),
                partFile
        );

        setLoadingItem();

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (getActivity() != null) {
                    helpers.progressDialog(false);
                    try {
                        JSONObject main = new JSONObject(String.valueOf(response.body()));

                        Helpers.LogThis(TAG_LOG, main.toString());

                        if (main.getBoolean("success")) {

                            asyncFetchAllDocuments();

                        } else {
                            helpers.handleErrorMessage(getActivity(), main.getJSONObject("data"));
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
                    helpers.progressDialog(false);
                    Helpers.LogThis(TAG_LOG, t.toString());
                    if (helpers.validateInternetConnection()) {
                        helpers.ToastMessage(getActivity(), getString(R.string.error_server));
                    } else {
                        helpers.ToastMessage(getActivity(), getString(R.string.error_connection));
                    }
                }
            }
        });

    }

    // FETCH ALL DOCUMENTS ASYNC FUNCTION ==========================================================
    private void asyncFetchAllDocuments() {

        UserInterface userInterface = UserInterface.retrofit.create(UserInterface.class);
        Call<JsonObject> call = userInterface.getAllDocuments(
                SharedPrefs.getInt(USER_ID)
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (getActivity() != null) {
                    helpers.progressDialog(false);
                    try {
                        JSONObject main = new JSONObject(String.valueOf(response.body()));

                        Helpers.LogThis(TAG_LOG, main.toString());

                        if (main.getBoolean("success")) {
                            JSONObject data_object = main.getJSONObject("data");
                            JSONArray document_array = data_object.getJSONArray("documents");

                            if (document_array != null && document_array.length() > 0) {
                                int length = document_array.length();
                                for (int i = 0; i < length; i++) {
                                    JSONObject document_object = document_array.getJSONObject(i);
                                    db.setDocumentFromJson(document_object);
                                }
                                populateDocumentsFromDB();
                            }
                        } else {
                            helpers.handleErrorMessage(getActivity(), main.getJSONObject("data"));
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
                    helpers.progressDialog(false);
                    Helpers.LogThis(TAG_LOG, t.toString());
                    if (helpers.validateInternetConnection()) {
                        helpers.ToastMessage(getActivity(), getString(R.string.error_server));
                    } else {
                        helpers.ToastMessage(getActivity(), getString(R.string.error_connection));
                    }
                }
            }
        });

    }

}