package com.hotelaide.main.activities;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.android.material.button.MaterialButton;
import com.hotelaide.R;
import com.hotelaide.utils.Database;
import com.hotelaide.utils.Helpers;
import com.hotelaide.utils.HelpersAsync;

import java.io.File;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.hotelaide.utils.StaticVariables.STR_SHARE_LINK;

public class PdfViewActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener, OnPageErrorListener {
    private Helpers helpers;

    private Toolbar toolbar;

    private EditText et_file_name;

    private MaterialButton
            btn_confirm,
            btn_cancel;

    private PDFView pdf_view;

    private int
            INT_PAGE_NUMBER = 0;

    private String
            STR_FILE_NAME = "",
            STR_FILE_PATH = "";
    private File file;

    private final String
            TAG_LOG = "PDF VIEW";

    private Database db;


    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        helpers = new Helpers(PdfViewActivity.this);

        db = new Database();

        if (handleExtraBundles()) {

            setContentView(R.layout.activity_pdf_view);

            setUpToolBarAndTabs();

            findAllViews();

            setListeners();

            loadPdfFromFile();

        } else {
            finish();
            helpers.toastMessage(getString(R.string.error_unknown));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                helpers.dialogShare(PdfViewActivity.this, STR_SHARE_LINK);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void loadComplete(int nbPages) {

    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        INT_PAGE_NUMBER = page;
    }

    @Override
    public void onPageError(int page, Throwable t) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    // BASIC FUNCTIONS =============================================================================
    private Boolean handleExtraBundles() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getString("FILE_PATH") != null) {
            STR_FILE_PATH = extras.getString("FILE_PATH");

            file = new File(STR_FILE_PATH);
            Uri uri = Uri.fromFile(new File(file.getAbsolutePath()));
            STR_FILE_NAME = getFileNameFromUri(uri);

            return true;
        } else {
            return false;
        }
    }

    public String getFileNameFromUri(final Uri uri) {

        String fileName = null;
        if (uri != null) {
            // Get file name.
            // File Scheme.
            if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
                File file = new File(uri.getPath());
                fileName = file.getName();
            }
            // Content Scheme.
            else if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
                Cursor returnCursor =
                        getContentResolver().query(uri, null, null, null, null);
                if (returnCursor != null && returnCursor.moveToFirst()) {
                    int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    fileName = returnCursor.getString(nameIndex);
                    returnCursor.close();
                }
            }
        }
        return fileName;
    }

    private void findAllViews() {
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_confirm = findViewById(R.id.btn_confirm);

        et_file_name = findViewById(R.id.et_file_name);
        pdf_view = findViewById(R.id.pdf_view);

        btn_confirm.setText(getString(R.string.txt_upload));
        btn_cancel.setText(getString(R.string.txt_back));
    }

    private void setUpToolBarAndTabs() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setListeners() {
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (STR_FILE_NAME.equals("")) {
                    helpers.myDialog(getString(R.string.txt_alert), "File name cannot be empty");
                } else {
                    MultipartBody.Part partFile = MultipartBody.Part.createFormData("file",
                            STR_FILE_NAME, RequestBody.create(MediaType.parse("application/pdf"), file));
                    db.setDirtyDocument();
                    HelpersAsync.asyncUploadDocument(partFile);
                    onBackPressed();
                }
            }
        });
    }

    private void loadPdfFromFile() {
        btn_confirm.setVisibility(View.VISIBLE);

        pdf_view.fromFile(file)
                .defaultPage(INT_PAGE_NUMBER)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .onPageError(this)
                .load();

        Helpers.logThis(TAG_LOG, "FILE_PATH: " + STR_FILE_PATH);
        Helpers.logThis(TAG_LOG, "FILE_NAME: " + STR_FILE_NAME);

        et_file_name.setText(STR_FILE_NAME);
        et_file_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                STR_FILE_NAME = et_file_name.getText().toString();
            }
        });

    }


}
