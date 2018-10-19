package com.hotelaide.main.fragments;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hotelaide.R;
import com.hotelaide.main.adapters.DocumentAdapter;
import com.hotelaide.main.models.DocumentModel;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.MyApplication;
import com.hotelaide.utils.SharedPrefs;

import java.io.File;
import java.util.ArrayList;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

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
            INT_RESULT_CODE = 2323;

    private TextView
            txt_no_results;
    private RelativeLayout rl_no_list_items;


    private RecyclerView recycler_view;
    private ArrayList<DocumentModel> model_list = new ArrayList<>();
    private DocumentAdapter adapter;
    private SwipeRefreshLayout swipe_refresh;

    public DocumentsFragment() {
    }

    // OVERRIDE METHODS ============================================================================
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {
                root_view = inflater.inflate(R.layout.frag_profile_documents, container, false);

                helpers = new Helpers(getActivity());

                findAllViews();

                setListeners();


            } catch (InflateException e) {
                e.printStackTrace();
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
            case INT_RESULT_CODE:
                if (resultCode == RESULT_OK && data.getData() != null && getActivity() != null) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();

                    File file = new File(uri.getPath());

                    String uriString = uri.toString();

                    Helpers.LogThis(TAG_LOG, uri.toString());

                    String file_name = "";

                    if (uriString.startsWith("content://")) {
                        Cursor cursor = null;
                        try {
                            cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                file_name = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                                Helpers.LogThis(TAG_LOG, file_name);

                                uploadToFireBase(file_name, uri);
                            }
                        } catch (Exception e) {
                            Helpers.LogThis(TAG_LOG, e.toString());
                        } finally {
                            if (cursor != null)
                                cursor.close();
                        }
                    } else if (uriString.startsWith("file://")) {
                        file_name = file.getName();
                        Helpers.LogThis(TAG_LOG + " FAILED:", file_name);
                        helpers.ToastMessage(getActivity(), "Invalid file selected");

                    } else {
                        helpers.ToastMessage(getActivity(), "Invalid file selected");
                    }
                }
                break;
        }
    }


    // BASIC FUNCTIONS =============================================================================
    private void findAllViews() {
        txt_no_results = root_view.findViewById(R.id.txt_no_results);
        rl_no_list_items = root_view.findViewById(R.id.rl_no_list_items);
        txt_no_results.setText("No Documents uploaded");

        swipe_refresh = root_view.findViewById(R.id.swipe_refresh);
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

    @AfterPermissionGranted(INT_PERMISSIONS_STORAGE)
    private void openDocuments() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};
        if (getActivity() != null && EasyPermissions.hasPermissions(getActivity(), perms)) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            startActivityForResult(intent, INT_RESULT_CODE);
        } else if (getActivity() != null) {
            EasyPermissions.requestPermissions(getActivity(), getString(R.string.rationale_storage),
                    INT_PERMISSIONS_STORAGE, perms);
        }
    }

    private void uploadToFireBase(String file_name, Uri uri) {
        MyApplication.initFireBase();

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();

        StorageReference user_ref = storageRef.child(String.valueOf(SharedPrefs.getInt(USER_ID)));

        StorageReference document_ref = user_ref.child(file_name);


        try {
            UploadTask uploadTask = document_ref.putFile(uri);

            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                }
            });

        } catch (Exception e) {
            Helpers.LogThis(TAG_LOG, e.toString());
            helpers.ToastMessage(getActivity(), e.toString());
        }
    }

}