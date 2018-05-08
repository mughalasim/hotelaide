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
import com.hotelaide.main_pages.activities.DashboardActivity;
import com.hotelaide.utils.Helpers;


public class StartUpLoginFragment extends Fragment {

    private View rootview;

    private TextView
            btn_confirm,
            btn_cancel;

    private EditText
            et_user_pass,
            et_user_email;

    private ImageView img_user_pass_toggle;

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

                dropDownKeyboard(et_user_email);

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

        img_user_pass_toggle = rootview.findViewById(R.id.img_user_pass_toggle);
        img_user_pass_toggle.setTag(TAG_PASS_HIDDEN);
        img_user_pass_toggle.setImageResource(R.drawable.ic_pass_hide);


        et_user_email = rootview.findViewById(R.id.et_useremail);
        et_user_pass = rootview.findViewById(R.id.et_userpassword);

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
                if (helpers.validateEmptyEditText(et_user_pass)
                        && helpers.validateEmptyEditText(et_user_email)
                        && et_user_email.getText().toString().contains("asim")) {
                    startActivity(new Intent(getActivity(), DashboardActivity.class));
                }

            }
        });

        img_user_pass_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (img_user_pass_toggle.getTag().toString().equals(TAG_PASS_HIDDEN)) {
                    img_user_pass_toggle.setImageResource(R.drawable.ic_pass_show);
                    img_user_pass_toggle.setTag(TAG_PASS_SHOWN);
                    et_user_pass.setTransformationMethod(null);
                } else {
                    img_user_pass_toggle.setImageResource(R.drawable.ic_pass_hide);
                    img_user_pass_toggle.setTag(TAG_PASS_HIDDEN);
                    et_user_pass.setTransformationMethod(new PasswordTransformationMethod());
                }
                helpers.animate_wobble(img_user_pass_toggle);
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