package com.hotelaide.main.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hotelaide.R;
import com.hotelaide.main.activities.PdfViewActivity;
import com.hotelaide.main.adapters.DocumentAdapter;
import com.hotelaide.main.models.DocumentModel;
import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.util.ArrayList;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;
import static com.hotelaide.utils.StaticVariables.BROADCAST_UPLOAD_COMPLETE;
import static com.hotelaide.utils.StaticVariables.EXTRA_FAILED;
import static com.hotelaide.utils.StaticVariables.EXTRA_PASSED;
import static com.hotelaide.utils.StaticVariables.INT_PERMISSIONS_STORAGE;


public class DocumentsFragment extends Fragment {

    private View root_view;
    private Helpers helpers;
    private Database db;
    private final String TAG_LOG = "DOCUMENTS";

    private final int
            INT_PDF_REQUEST_CODE = 2323,
            INT_REFRESH_REQUEST_CODE = 4565;

    private FloatingActionButton
            btn_add;
    private MaterialButton btn_refresh;
    private TextView
            txt_no_results;

    private BroadcastReceiver receiver;

    private RelativeLayout rl_no_list_items;
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
                    Helpers.logThis(TAG_LOG, "HAS BUNDLES");
                    btn_add.setVisibility(View.GONE);
                    btn_refresh.setVisibility(View.GONE);
                } else {
                    listenUploadCompleteBroadcast();
                }

                setListeners();

                populateDocumentsFromDB();


            } catch (InflateException e) {
                Helpers.logThis(TAG_LOG, e.toString());
            }
        } else {
            container.removeView(root_view);
        }
        return root_view;
    }

    @Override
    public void onDestroy() {
        if (getActivity() != null && receiver != null) {
            getActivity().unregisterReceiver(receiver);
            receiver = null;
        }
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, getActivity());
        helpers.myPermissionsDialog(getActivity(), grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Helpers.logThis(TAG_LOG, "REQUEST CODE: " + requestCode);
        switch (requestCode) {
            case INT_PDF_REQUEST_CODE:
                if (resultCode == RESULT_OK && getActivity() != null) {
                    String path = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
                    if (path != null) {
                        Helpers.logThis(TAG_LOG, "FILE PATH: " + path);
                        startActivityForResult(
                                new Intent(getActivity(), PdfViewActivity.class).putExtra("FILE_PATH", path)
                                , INT_REFRESH_REQUEST_CODE);
                    }
                } else {
                    helpers.ToastMessage(getActivity(), "Invalid file selected");
                }
                break;

            case INT_REFRESH_REQUEST_CODE:
                populateDocumentsFromDB();
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

        btn_add = root_view.findViewById(R.id.btn_add);
        btn_refresh = root_view.findViewById(R.id.btn_refresh);

    }

    private void setListeners() {
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDocuments();
            }
        });

        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpers.ToastMessage(getActivity(), "Refreshing in a minute, please wait...");
                helpers.asyncGetAllDocuments();
            }
        });

    }

    private void populateDocumentsFromDB() {
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
        documentModel.name = "No Documents uploaded";
        documentModel.date_uploaded = "324123423";
        model_list.add(documentModel);
        adapter.notifyDataSetChanged();
    }

    @AfterPermissionGranted(INT_PERMISSIONS_STORAGE)
    private void openDocuments() {
        String[] perms = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        if (getActivity() != null && EasyPermissions.hasPermissions(getActivity(), perms)) {
            new MaterialFilePicker()
                    .withSupportFragment(DocumentsFragment.this)
                    .withRequestCode(INT_PDF_REQUEST_CODE).withHiddenFiles(true)
                    .withFilter(Pattern.compile(".*\\.pdf$"))
                    .withTitle("Select PDF File")
                    .start();

        } else if (getActivity() != null) {
            EasyPermissions.requestPermissions(getActivity(), getString(R.string.rationale_storage),
                    INT_PERMISSIONS_STORAGE, perms);
        }
    }

    private void listenUploadCompleteBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_UPLOAD_COMPLETE);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getExtras() != null && getActivity() != null) {
                    if (intent.getExtras().getString(EXTRA_PASSED) != null) {
                        Helpers.logThis(TAG_LOG, "PASSED");
                        helpers.ToastMessage(getActivity(), "Update successful");
                    } else if (intent.getExtras().getString(EXTRA_FAILED) != null) {
                        Helpers.logThis(TAG_LOG, "FAILED");
                        helpers.ToastMessage(getActivity(), "Update failed, please try again later");
                    }
                }
                populateDocumentsFromDB();
            }
        };
        if (getActivity() != null)
            getActivity().registerReceiver(receiver, filter);
    }

}