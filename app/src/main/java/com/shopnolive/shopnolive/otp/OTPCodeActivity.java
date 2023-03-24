package com.shopnolive.shopnolive.otp;

import static com.shopnolive.shopnolive.otp.OTPPhoneNumberActivity.api;
import static com.shopnolive.shopnolive.utils.Variable.accessToken;
import static com.shopnolive.shopnolive.utils.Variable.editor;
import static com.shopnolive.shopnolive.utils.Variable.myId;
import static com.shopnolive.shopnolive.utils.Variable.userInfo;
import static com.shopnolive.shopnolive.utils.Variable.userLoginToken;
import static com.shopnolive.shopnolive.utils.Variable.userProfileFollow;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shopnolive.shopnolive.R;
import com.shopnolive.shopnolive.model.login.LoginModel;
import com.shopnolive.shopnolive.model.login.LoginRespons;
import com.shopnolive.shopnolive.model.registration.RegistrationModel;
import com.shopnolive.shopnolive.ui.activities.BannedActivity;
import com.shopnolive.shopnolive.ui.main.HomeActivity;
import com.shopnolive.shopnolive.ppal.util.Common;
import com.shopnolive.shopnolive.ppal.util.PrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OTPCodeActivity extends AppCompatActivity {


    private Button submitOTPCodeBTN;
    private PinView codeOTPET;

    public static String code = null;
    public static String name = null;
    public static String number = null;
    public static String internationalFormateNumber = null;

    public static String userId = null;
    public static String mVerificationId = null;
    public FirebaseAuth firebaseAuth;
    // = FirebaseAuth.getInstance();
    //  PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String  device_id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p_code);

        firebaseAuth = FirebaseAuth.getInstance();

        try{
            device_id = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        }
        catch (Exception e){

        }

        //autoLogin();
        initComponent();
        initFunction();
        initListener();
        sendVerificationCode();
        //registration();

    }

    private void initComponent() {

        Intent intent = getIntent();
        name = intent.getExtras().getString("name");
        number = intent.getExtras().getString("number");
        internationalFormateNumber = intent.getExtras().getString("international");

    }

    private void initListener() {

        submitOTPCodeBTN.setOnClickListener(v -> {
            if (!codeOTPET.getText().toString().isEmpty())
                gotoOTPCodeActivity();

            else
                Toast.makeText(OTPCodeActivity.this, "Enter OTP !!", Toast.LENGTH_SHORT).show();
        });
    }

    private void gotoOTPCodeActivity() {
        verifyVerificationCode(codeOTPET.getText().toString());
    }

    private void initFunction() {

        submitOTPCodeBTN = findViewById(R.id.btnOTPCodeSubmit);
        codeOTPET = findViewById(R.id.pinView_otp_input);
    }

    private void verifyVerificationCode(String code) {
        //creating the credential

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        Log.i("ysawijaaw", "verifyVerificationCode: " + mVerificationId + " code: " + code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        Log.i("ysawijaaw", "signInWithPhoneAuthCredential: " + codeOTPET.getText().toString());

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(OTPCodeActivity.this, task -> {

                    if (task.isSuccessful()) {
                        registration();
                        Log.i("ysawijaaw", "onComplete: registration");
                    } else {
                        //verification unsuccessful.. display an error message
                        String message = "Somthing is wrong, we will fix it soon...";
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            message = task.getException().getMessage();
                            Log.i("ysawijaaw", "onComplete: " + task.getException().getMessage());//"Invalid code entered...";
                        }
                        Toast.makeText(OTPCodeActivity.this, "" + message, Toast.LENGTH_LONG).show();
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();

        Log.d("TAG", "onStart: " + number);

//          sendVerificationCode(number);
    }

    // }


    public PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            //Getting the code sent by SMS

            code = phoneAuthCredential.getSmsCode();

            if (code != null) {
                codeOTPET.setText(code);
                //verifying the code
                verifyVerificationCode(code);

            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            // Toast.makeText(OTPVerificationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("TAG", "onVerificationFailed: Error" + e.getMessage());
            Toast.makeText(OTPCodeActivity.this, "onVerificationFailed " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };

    String fullNumber;

    private void sendVerificationCode() {

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(internationalFormateNumber)       // Phone number to verify
                        .setTimeout(120L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);


//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                internationalFormateNumber
//                /*"+88"+number*/
//                /*"+8801990014631"*/,
//                60,
//                TimeUnit.SECONDS,
//                OTPCodeActivity.this,
//                mCallbacks);
    }

    JSONObject obj = new JSONObject();

    private void autoLogin() {

        Log.d("TAGregistration", "autoLogin : login " + number);

        LoginModel loginModel = new LoginModel(number);
        JsonObject gsonObject = new JsonObject();
        JSONObject jsonObj_ = new JSONObject();

        try {
            jsonObj_.put("phone", number);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonParser jsonParser = new JsonParser();
        gsonObject = (JsonObject) jsonParser.parse(jsonObj_.toString());




        Log.d("TAGregistration", "autoLogin : login " + number+" Device ID : "+device_id);
        Call<LoginRespons> call = api.loginUser(number,device_id);
        Log.d("TAGregistration", "autoLogin : login " + number);

        call.enqueue(new Callback<LoginRespons>() {
            @Override
            public void onResponse(Call<LoginRespons> call, retrofit2.Response<LoginRespons> response) {

                Log.d("TAGregistration", "autoLogin : login res " + response.body().getResponse().get(0).getPresentCoinBalance());

                LoginRespons respons = response.body();

                if (respons != null) {

                    //userProfileInformation = respons.getResponse().get(0);
                    userInfo = respons.getResponse().get(0);
                    userProfileFollow = respons.getFollowers();
                    userLoginToken = userInfo.getPhone();

                    editor.putString("token", userInfo.getPhone());// = pref.edit();.p("token","");
                    editor.commit();

                    goToHome();

                }
            }

            @Override
            public void onFailure(Call<LoginRespons> call, Throwable t) {

            }
        });

    }


    private void goToHome() {
        Toast.makeText(this, "goToHomeCalled", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(OTPCodeActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void registration() {

        // RegistrationModel registrationModel = new RegistrationModel(name,number);
        Call<RegistrationModel> call = api.registrationUser(name, number,device_id);

        Log.d("TAGregistration", "onResponse: " + name + "  " + number);

        call.enqueue(new Callback<RegistrationModel>() {
            @Override
            public void onResponse(Call<RegistrationModel> call, Response<RegistrationModel> response) {
                if (response.isSuccessful()) {
                    Log.d("LOGIN",response.body().toString());
                    RegistrationModel registrationModel = response.body();
                    if (registrationModel != null && registrationModel.isSuccess()) {
                        userInfo = registrationModel.data;
                        //userProfileFollow = respons.getFollowers();
                        userLoginToken = registrationModel.data.getPhone();
                        accessToken = registrationModel.data.getAccessToken();
                        myId = registrationModel.data.getId();


                        editor.putString("token", accessToken);// = pref.edit();.p("token","");
                        editor.putString("phone", userLoginToken);// = pref.edit();.p("token","");
                        editor.putString("myId", myId);// = pref.edit();.p("token","");
                        editor.commit();
                        //save my id
                        PrefManager prefManager =  new PrefManager(OTPCodeActivity.this);
                        prefManager.set_val(Common.MY_ID,String.valueOf(myId));

                            Log.d("LOGIN",userInfo.status);

                        //Device Blocked
                        if (userInfo.status.equals("active")) {
                            goToHome();

                        } else {
                            Intent intent1=new Intent(OTPCodeActivity.this,BannedActivity.class);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent1.putExtra("message","Your account is temporary banned. Please try again later or contact with admin");
                            startActivity(intent1);
                            finish();

                        }
                    } else {
                        Toast.makeText(OTPCodeActivity.this, "Couldn't connect to server", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RegistrationModel> call, Throwable t) {
                Intent intent1=new Intent(OTPCodeActivity.this,BannedActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.putExtra("message","Your account is permanently banned. Please try again later or login with another device");
                startActivity(intent1);
                finish();
                Toast.makeText(OTPCodeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}