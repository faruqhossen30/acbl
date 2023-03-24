// java class for activity_o_t_p_phone_number.xml

package com.shopnolive.shopnolive.otp;

import static com.shopnolive.shopnolive.utils.Variable.accessToken;
import static com.shopnolive.shopnolive.utils.Variable.editor;
import static com.shopnolive.shopnolive.utils.Variable.myId;
import static com.shopnolive.shopnolive.utils.Variable.pref;
import static com.shopnolive.shopnolive.utils.Variable.userLoginToken;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;
import com.shopnolive.shopnolive.R;
import com.shopnolive.shopnolive.api.client.ApiClient;
import com.shopnolive.shopnolive.api.service.ApiService;
import com.shopnolive.shopnolive.ui.main.HomeActivity;


public class OTPPhoneNumberActivity extends AppCompatActivity {

    private Button sendOTPCodeBTN;
    private EditText etNumber, userName;


    public static ApiService api = ApiClient.getClient().create(ApiService.class);

    CountryCodePicker ccp;
    String internationalFormat;

    String number;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p_phone_number);

//        FirebaseApp.initializeApp(this);

        /*FirebaseOptions options = new FirebaseOptions.Builder()
                .setApiKey("AIzaSyBLWF1qXi_DwIHeQ5Brb75punKN1FD-4Q0")
                .setDatabaseUrl("https://famouslive2-7f590-default-rtdb.firebaseio.com/")
                .setApplicationId("1:451595243618:android:0df8c14d220ee9f2be813b")
                .build();
//
        if (FirebaseApp.getApps(this).isEmpty()) {
            //   FirebaseApp.initializeApp(this);
            FirebaseApp app = FirebaseApp.initializeApp(this, options);
            FirebaseDatabase.getInstance(app);

        }*/
        /////I tried Try and Catch with no success//////


        /// for this : FirebaseApp app = FirebaseApp.initializeApp(c, options, "some_app");
        //// will fail with "FirebaseApp name some_app already exists!"


        initView();
        initFunction();
        initListener();

        //recieveLink();
    }

    private void recieveLink() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {

                    Uri deepLink = null;
                    if (pendingDynamicLinkData != null) {
                        deepLink = pendingDynamicLinkData.getLink();

                        Log.e("TAG", "  sharelink " + deepLink.toString());
                        String sharelink = deepLink.toString();
                        try {

                            sharelink = sharelink.substring(sharelink.lastIndexOf("=") + 1);
                            Log.e("TAG", " substring " + sharelink);

                            String userid = sharelink.substring(0, sharelink.indexOf("-"));


                        } catch (Exception e) {
                            Log.e("TAG", " error " + e.toString());
                        }


                    }

                }).addOnFailureListener(this, e -> Log.e("TAG", "getDynamicLink:onFailure", e));


    }


    private void initView() {
        pref = getApplicationContext().getSharedPreferences("login", 0); // 0 - for private mode
        editor = pref.edit();
    }

    private void initListener() {
        Log.i("onSuccess", "div");

        sendOTPCodeBTN.setOnClickListener(v -> {

            // String number = binding.etOtpNumberId.getText().toString();
            number = etNumber.getText().toString();
            name = userName.getText().toString();

            if (isValidPhoneNumber(number) && !name.isEmpty()) {
                boolean status = validateUsing_libphonenumber(ccp.getSelectedCountryCode(), number);
                if (status) {
                    gotoOTPCodeActivity();
                    //registration();
                    //autoLogin();
                } else {
                    Toast.makeText(OTPPhoneNumberActivity.this, "Invalid Phone Number ", Toast.LENGTH_SHORT).show();
                    //  tvIsValidPhone.setText("Invalid Phone Number (libphonenumber)");
                }
            }
        });

    }


    private void goToHome() {
        Intent intent = new Intent(OTPPhoneNumberActivity.this, HomeActivity.class);
        intent.putExtra("number", internationalFormat);
        startActivity(intent);
        finish();
    }

    private void loginActivity() {
        Intent intent = new Intent(OTPPhoneNumberActivity.this, OTPCodeActivity.class);
        intent.putExtra("number", internationalFormat);
        startActivity(intent);
        finish();
    }

    private void gotoOTPCodeActivity() {
        Intent in = new Intent(this, OTPCodeActivity.class);
        in.putExtra("name", name);
        in.putExtra("number", number);
        in.putExtra("international", internationalFormat);
        startActivity(in);
        finish();
    }

    private void initFunction() {
        sendOTPCodeBTN = findViewById(R.id.btnSendOTPCode);
        etNumber = findViewById(R.id.et_phone_number);
        userName = findViewById(R.id.userName);
        ccp = findViewById(R.id.country_code_picker);

    }

    private boolean isValidPhoneNumber(CharSequence phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            return Patterns.PHONE.matcher(phoneNumber).matches();
        }
        return false;
    }

    private boolean validateUsing_libphonenumber(String countryCode, String phNumber) {

        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        String isoCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(countryCode));
        Phonenumber.PhoneNumber phoneNumber = null;
        try {
            //phoneNumber = phoneNumberUtil.parse(phNumber, "IN");  //if you want to pass region code
            phoneNumber = phoneNumberUtil.parse(phNumber, isoCode);

        } catch (com.google.i18n.phonenumbers.NumberParseException e) {
            System.err.println(e);
        }

        boolean isValid = phoneNumberUtil.isValidNumber(phoneNumber);

        if (isValid) {
            internationalFormat = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            //   Toast.makeText(OTPPhoneNumberActivity.this, "Phone Number is Valid " + internationalFormat, Toast.LENGTH_LONG).show();
            return true;
        } else {
            Toast.makeText(this, "Phone Number is Invalid " + phoneNumber, Toast.LENGTH_LONG).show();
            return false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        userLoginToken = pref.getString("phone", "");
        accessToken = pref.getString("token", "");
        myId = pref.getString("myId", "");


        Log.d("TAGregistration", "onStart: " + accessToken);

        if (!accessToken.isEmpty()) {
            goToHome();
        }

    }


}
