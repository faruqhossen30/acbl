//this is the java class for activity_account_settings.xml

package com.shopnolive.shopnolive.activity;

import static com.shopnolive.shopnolive.api.client.ApiClient.BASE_URL;
import static com.shopnolive.shopnolive.otp.OTPPhoneNumberActivity.api;
import static com.shopnolive.shopnolive.utils.Variable.accessToken;
import static com.shopnolive.shopnolive.utils.Variable.editor;
import static com.shopnolive.shopnolive.utils.Variable.userInfo;
import static com.shopnolive.shopnolive.utils.Variable.userProfileFollow;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;

import com.shopnolive.shopnolive.R;
import com.shopnolive.shopnolive.activity.profile.ProfileViewModel;
import com.shopnolive.shopnolive.databinding.ActivityAccountSettingsBinding;
import com.shopnolive.shopnolive.model.DeleteResponse;
import com.shopnolive.shopnolive.model.ResultMsg;
import com.shopnolive.shopnolive.otp.OTPPhoneNumberActivity;
import com.shopnolive.shopnolive.utils.ImageUtils;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountSettingsActivity extends AppCompatActivity {

    // Binding
    ActivityAccountSettingsBinding binding;
    private ProfileViewModel profileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);

        initFunction();
        initListener();

        //  back = findViewById(R.id.settings_back);
        binding.toolbar.settingsBack.setOnClickListener(v -> finish());


    }

    private void observeProfile() {
        profileViewModel.getProfile().observe(this, myProfile -> {
            try {
                userInfo = myProfile.getProfileData();
                userProfileFollow = myProfile.getFollowers();
                //getLiveUser();
            } catch (Exception e) {

            }
        });

    }


    private void initListener() {

        binding.changeName.setOnClickListener(view -> alarDialog("name"));

        binding.changeNumber.setOnClickListener(view -> alarDialog("number"));

        binding.changeProfile.setOnClickListener(view -> alarDialog("profile"));

        binding.deleteAccount.setOnClickListener(view -> deleteDialog());

    }

    private void deleteDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_alert_dialog);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView no = dialog.findViewById(R.id.no_dialog);
        TextView yes = dialog.findViewById(R.id.yes_dialog);

        no.setOnClickListener(view -> dialog.cancel());

        yes.setOnClickListener(view -> {
            Call<DeleteResponse> call = api.deleteUser();
            call.enqueue(new Callback<DeleteResponse>() {
                @Override
                public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                    if (response.body() != null && response.body().isSuccess()) {
                        Toast.makeText(AccountSettingsActivity.this, "Account successfully deleted", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        accessToken = "";
                        editor.putString("token", accessToken);// = pref.edit();.p("token","");
                        editor.putString("phone", "");// = pref.edit();.p("token","");
                        editor.commit();
                        Intent intent = new Intent(AccountSettingsActivity.this, OTPPhoneNumberActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        AccountSettingsActivity.this.finish();
                    } else {
                        Toast.makeText(AccountSettingsActivity.this, "Failed to delete account", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<DeleteResponse> call, Throwable t) {
                    Toast.makeText(AccountSettingsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();

        /*TextView yesBTN, noBTN;

        yesBTN = v.findViewById(R.id.deleteAccountYes);
        noBTN = v.findViewById(R.id.yes_dialog);

        yesBTN.setOnClickListener(view -> {
            Call<ResultMsg> call = api.deleteUser(userInfo.getId());
            call.enqueue(new Callback<ResultMsg>() {
                @Override
                public void onResponse(Call<ResultMsg> call, Response<ResultMsg> response) {
                    if (response.body() != null) {
                        Toast.makeText(AccountSettingsActivity.this, "Delete Account !!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        Intent intent = new Intent(AccountSettingsActivity.this, OTPPhoneNumberActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    }
                }

                @Override
                public void onFailure(Call<ResultMsg> call, Throwable t) {
                    dialog.dismiss();
                    Toast.makeText(AccountSettingsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        });

        noBTN.setOnClickListener(view -> dialog.dismiss());*/

    }

    private void alarDialog(String check) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AccountSettingsActivity.this);
        View v = LayoutInflater.from(AccountSettingsActivity.this).inflate(R.layout.change_dalog, null);
        alertDialog.setView(v);

        AlertDialog dialog = alertDialog.create();
        dialog.show();
        dialog.setCancelable(false);

        EditText changeET;
        Button changeBTN, cancelBTN, uploadImageBTN;

        Log.d("image_check", "onClick: " + check);

        profileIV = v.findViewById(R.id.imageViewProfile);
        changeET = v.findViewById(R.id.changeET);
        changeBTN = v.findViewById(R.id.changeBTN);
        cancelBTN = v.findViewById(R.id.cancelBTN);
        uploadImageBTN = v.findViewById(R.id.uploadImage);

        if (check.equals("profile")) {

            changeET.setVisibility(View.GONE);
            changeBTN.setText("Select");
            profileIV.setVisibility(View.VISIBLE);
            uploadImageBTN.setVisibility(View.GONE);
            //   Log.d("image_test", "onCreate:  3 "+ BASE_URL + userInfo.getImage());
            if (userInfo.getImage() != null)
                Picasso.get().load(BASE_URL + userInfo.getImage()).placeholder(R.drawable.user1).into(profileIV);
            // Log.d("image_test", "onCreate:  4 "+ BASE_URL + userInfo.getImage());

            if (bitmap != null) {
                uploadImageBTN.setVisibility(View.VISIBLE);
                profileIV.setImageBitmap(bitmap);
            }

        } else if
        (check.equals("delete")) {

            changeBTN.setText("Delete");
            changeET.setVisibility(View.GONE);
            profileIV.setVisibility(View.GONE);
            uploadImageBTN.setVisibility(View.GONE);

        } else if (check.equals("dismiss")) {
            dialog.dismiss();
        } else {
            changeBTN.setText("Change");
            changeET.setVisibility(View.VISIBLE);
            profileIV.setVisibility(View.GONE);
            uploadImageBTN.setVisibility(View.GONE);
        }

        cancelBTN.setOnClickListener(view -> {
            dialog.dismiss();
            bitmap = null;
        });

        uploadImageBTN.setOnClickListener(view -> {
            // @Part MultipartBody.Part file)

            dialog.dismiss();
            uploadFile(userInfo.getId());

        });


        changeBTN.setOnClickListener(view -> {
            String value = changeET.getText().toString();

            // Log.d("image_check", "onClick: "+check+" btn");


            if (!value.isEmpty()) {
                if (check.equals("name")) {
                    // UserNameChange nameChange = new UserNameChange(name);
                    Call<ResultMsg> call = api.nameChange(userInfo.getId(), value);
                    call.enqueue(new Callback<ResultMsg>() {
                        @Override
                        public void onResponse(Call<ResultMsg> call, Response<ResultMsg> response) {
                            if (response.body() != null) {
                                Toast.makeText(AccountSettingsActivity.this, "Name Change", Toast.LENGTH_SHORT).show();
                                userInfo.setName(value);
                                dialog.dismiss();
                                observeProfile();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultMsg> call, Throwable t) {
                        }
                    });
                } else if (check.equals("number")) {
                    Call<ResultMsg> call = api.numberChange(userInfo.getId(), value);
                    call.enqueue(new Callback<ResultMsg>() {
                        @Override
                        public void onResponse(Call<ResultMsg> call, Response<ResultMsg> response) {
                            if (response.body() != null) {
                                //  Toast.makeText(AccountSettingsActivity.this, "Number Change", Toast.LENGTH_SHORT).show();
                                userInfo.setPhone(value);
                                dialog.dismiss();
                                observeProfile();

                            }
                        }

                        @Override
                        public void onFailure(Call<ResultMsg> call, Throwable t) {

                        }
                    });
                }
            } else {
                if (check.equals("profile")) {

                    //    Log.d("image_check", "onClick: profile");
                    isStoragePermissionGranted();
                    // dialog.show();
                }
            }


        });
    }


    private void initFunction() {
    }


    public boolean isStoragePermissionGranted() {
        //   Log.d("image_check", "onClick: profile permission");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                chooseFromGallery();
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            chooseFromGallery();
            return true;
        }
    }

    private void chooseFromGallery() {

        Log.d("image_check", "onClick: profile choose Gallery");
        Intent intent = new Intent();

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                imageUri = data.getData();
                bitmap = ImageUtils.getInstant().getCompressedBitmap(getRealPathFromURI(imageUri), 60);

                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                Uri tempUri = getImageUri(getApplicationContext(), bitmap);

                // CALL THIS METHOD TO GET THE ACTUAL PATH
                File finalFile = new File(getRealPathFromURI(tempUri));

                //fileReqBody = RequestBody.create(MediaType.parse("image/*"), finalFile);

                alarDialog("profile");

                RequestBody requestFile =
                        RequestBody.create(
                                MediaType.parse(getContentResolver().getType(tempUri)),
                                finalFile
                        );
                part = MultipartBody.Part.createFormData("image", String.valueOf(System.currentTimeMillis()), requestFile);

                ///Log.d(TAG, "onActivityResult: ");
                //  uploadImageClick();

            } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {


                //Uri currImageURI = data.getData();
                bitmap = ImageUtils.getInstant().getCompressedBitmap(getRealPathFromURI(imageUri), 60);
                Uri tempUri = getImageUri(getApplicationContext(), bitmap);

                String imageUrl = getRealPathFromURI(tempUri);

                // CALL THIS METHOD TO GET THE ACTUAL PATH
                String path = getRealPathFromURI(tempUri);
                if (path == null) return;

                File imgFile = new File(imageUrl);

                RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), imgFile);
                RequestBody requestFile =
                        RequestBody.create(
                                MediaType.parse(getContentResolver().getType(tempUri)),
                                imgFile
                        );
                part = MultipartBody.Part.createFormData("image", String.valueOf(System.currentTimeMillis()), requestFile);
                // uploadImageClick();

            }


        }


    }

    public String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    private void uploadFile(String userId) {

        ProgressDialog progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);//you can cancel it by pressing back button
        progressBar.setMessage("Image File Uploading.....");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgress(0);//initially progress is 0
        progressBar.setMax(100);//sets the maximum value 100
        progressBar.show();//displays the progress bar


        //creating a file
        //  File file = new File(getRealPathFromURI(fileUri));

        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), userId);


        Call<String> call = api.imageUploadFile(id, part);

        //finally performing the call
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.body() != null) {
                    Toast.makeText(getApplicationContext(), "Image File Uploaded Successfully...", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Some error occurred...", Toast.LENGTH_LONG).show();
                }
                observeProfile();
                if (bitmap != null) profileIV.setImageBitmap(bitmap);
                progressBar.dismiss();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (bitmap != null) profileIV.setImageBitmap(bitmap);
                progressBar.dismiss();
                finish();
                //Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private static final int SELECT_PICTURE = 2;
    private MultipartBody.Part part;
    private Uri imageUri;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;

    private ImageView profileIV;
    private Bitmap bitmap = null;
    RequestBody fileReqBody = null;
}