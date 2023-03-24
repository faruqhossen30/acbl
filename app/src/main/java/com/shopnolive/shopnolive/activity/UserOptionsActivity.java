package com.shopnolive.shopnolive.activity;

import static com.shopnolive.shopnolive.api.client.ApiClient.BASE_URL;
import static com.shopnolive.shopnolive.utils.Variable.userInfo;
import static com.shopnolive.shopnolive.utils.Variable.userProfileFollow;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.shopnolive.shopnolive.R;
import com.shopnolive.shopnolive.activity.profile.ProfileViewModel;
import com.shopnolive.shopnolive.databinding.ActivityUserOptionsBinding;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserOptionsActivity extends AppCompatActivity {

    ActivityUserOptionsBinding binding;
    final int REQUEST_GALLERY = 9544;
    CircleImageView imgHolder;
    String part_image;
    ProgressDialog pd;
    ProfileViewModel profileViewModel;

    private static final int INTENT_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserOptionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.swiperefresh.setEnabled(false);

        pd = new ProgressDialog(this);
        pd.setMessage("loading ... ");

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        //  Log.d("image_test", "onCreate:  1 "+ BASE_URL + userInfo.getImage());

        if (userInfo.getImage() != null)
            Picasso.get().load(BASE_URL + userInfo.getImage()).placeholder(R.drawable.user1).into(binding.userImageProfile);
        // Log.d("image_test", "onCreate: 2 "+ BASE_URL + userInfo.getImage());


        try {
            binding.userNameProfile.setText(userInfo.getName());
            binding.phoneNumberProfile.setText(userInfo.getPhone());
            binding.dimondUserStory.setText(String.valueOf(userInfo.getPresentCoinBalance()));
            binding.followerUserStory.setText(String.valueOf(userProfileFollow.size()));
            //Picasso.get().load(BASE_URL +userInfo.getImage() ).placeholder(R.drawable.user1).into(binding.userImageProfile);

        } catch (Exception error) {
            error.printStackTrace();
        }
        binding.backButton.setOnClickListener(v -> finish());

        binding.settings.setOnClickListener(v -> {
            Intent intent = new Intent(UserOptionsActivity.this, AccountSettingsActivity.class);
            startActivity(intent);
        });

        binding.shareApp.setOnClickListener(v -> {

            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "BD Live");
                String shareMessage = "\nYou Can Join With Us for Fun....... \n\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + getPackageName() + "\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "Share BD Live"));
            } catch (Exception e) {
                //e.toString();
            }
        });

        binding.helpFeedback.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + "famoussapurt2010@gmail.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            intent.putExtra(Intent.EXTRA_TEXT, "Your Opinion : ");
            startActivity(intent);
        });


        binding.userImageProfile.setOnClickListener(v -> {
        });

        binding.btnPurchase.setOnClickListener(v ->
                Toast.makeText(UserOptionsActivity.this, "Under Development", Toast.LENGTH_SHORT).show()
        );
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == INTENT_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {

                try {

                    InputStream is = getContentResolver().openInputStream(data.getData());

                    //  uploadImage(getBytes(is));
//
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream byteBuff = new ByteArrayOutputStream();

        int buffSize = 1024;
        byte[] buff = new byte[buffSize];

        int len = 0;
        while ((len = is.read(buff)) != -1) {
            byteBuff.write(buff, 0, len);
        }

        return byteBuff.toByteArray();
    }

    @Override
    protected void onStart() {
        super.onStart();
        observeProfile();
    }

    private void observeProfile() {
        profileViewModel.getProfile().observe(this, myProfile -> {
            userInfo = myProfile.getProfileData();
            userProfileFollow = myProfile.getFollowers();

            try {
                if (userInfo.getImage() != null)
                    Picasso.get().load(BASE_URL + userInfo.getImage()).placeholder(R.drawable.user1).into(binding.userImageProfile);

                binding.userNameProfile.setText(userInfo.getName());
                binding.phoneNumberProfile.setText(userInfo.getPhone());
                binding.dimondUserStory.setText(String.valueOf(userInfo.getPresentCoinBalance()));
                binding.followerUserStory.setText(String.valueOf(userProfileFollow.size()));
                //Picasso.get().load(BASE_URL +userInfo.getImage() ).placeholder(R.drawable.user1).into(binding.userImageProfile);

            } catch (Exception error) {
                error.printStackTrace();
            }
        });
    }
}