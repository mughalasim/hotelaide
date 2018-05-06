package com.hotelaide.main_pages.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.text.method.PasswordTransformationMethod;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotelaide.R;
import com.hotelaide.main_pages.activities.HomeActivity;
import com.hotelaide.utils.Helpers;


public class StartUpLoginFragment extends Fragment {

    private View rootview;

    private TextView
            btn_confirm,
            btn_cancel;

    private EditText
            et_userpassword,
            et_useremail;

    private ImageView et_userpassword_reveal;

    private Helpers helpers;

    private final String
            TAG_LOG = "FRAGMENT LOGIN",
            TAG_PASS_HIDDEN = "0",
            TAG_PASS_SHOWN = "1";

    public StartUpLoginFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootview == null && getActivity() != null) {
            try {
                rootview = inflater.inflate(R.layout.fragment_startup_login, container, false);
                helpers = new Helpers(getActivity());

                findAllViews();

                setListerners();

                AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

                dropDownKeyboard(et_useremail);

            } catch (InflateException e) {
                e.printStackTrace();
            }
        } else {
            ((ViewGroup) container.getParent()).removeView(rootview);
        }
        return rootview;
    }

    private void findAllViews() {
        btn_cancel = rootview.findViewById(R.id.btn_cancel);
        btn_confirm = rootview.findViewById(R.id.btn_confirm);

        et_userpassword_reveal = rootview.findViewById(R.id.et_userpassword_toggle);
        et_userpassword_reveal.setTag(TAG_PASS_HIDDEN);
        et_userpassword_reveal.setImageResource(R.drawable.ic_pass_hide);


        et_useremail = rootview.findViewById(R.id.et_useremail);
        et_userpassword = rootview.findViewById(R.id.et_userpassword);

    }

    private void setListerners() {
        btn_cancel.setVisibility(View.GONE);
        btn_confirm.setVisibility(View.VISIBLE);
        btn_confirm.setText(getString(R.string.nav_login));

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_userpassword.getText().toString().length() < 1) {
                    et_userpassword.setError(getString(R.string.error_field_required));
                } else if (et_useremail.getText().toString().length() < 1) {
                    et_useremail.setError(getString(R.string.error_field_required));
                } else if (et_useremail.getText().toString().contains("asim")) {
                    startActivity(new Intent(getActivity(), HomeActivity.class));
                }

            }
        });

        et_userpassword_reveal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_userpassword_reveal.getTag().toString().equals(TAG_PASS_HIDDEN)) {
                    et_userpassword_reveal.setImageResource(R.drawable.ic_pass_show);
                    et_userpassword_reveal.setTag(TAG_PASS_SHOWN);
                    et_userpassword.setTransformationMethod(null);
                }else{
                    et_userpassword_reveal.setImageResource(R.drawable.ic_pass_hide);
                    et_userpassword_reveal.setTag(TAG_PASS_HIDDEN);
                    et_userpassword.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });
    }


    private void dropDownKeyboard(EditText editText) {
        if (getActivity() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
    }

}