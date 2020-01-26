package com.example.rentacar;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.rentacar.Controllers.DatabaseFields;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.michaelbel.bottomsheet.BottomSheet;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class RegisterCustomerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int IMAGE_CAPTURE = 12;
    private static final int PICK_IMAGE = 11;

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private StorageReference mStorageReference;

    private Button save_data;
    private EditText name, mobile;
    private CircleImageView image;
    private View bottomSheet;
    private LinearLayout use_camera, use_gallery;

    private BottomSheet.Builder bottom_sheet_builder;

    private Uri imageUri;
    private SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_customer);

        bottomSheet = getLayoutInflater().inflate(R.layout.image_choose_option, null);

        initComponents();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        mStorageReference = FirebaseStorage.getInstance().getReference("Images");
        mFirebaseAuth = FirebaseAuth.getInstance();

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setCancelable(false);

        save_data.setOnClickListener(this);
        image.setOnClickListener(this);
        use_camera.setOnClickListener(this);
        use_gallery.setOnClickListener(this);
    }

    private void saveData() {
        final String cus_name = name.getText().toString().trim();
        final String cus_mobile = mobile.getText().toString().trim();

        if (TextUtils.isEmpty(cus_name)) {
            Toasty.info(this, "Please Enter Your Name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(cus_mobile)) {
            Toasty.info(this, "Please Enter Your Mobile", Toast.LENGTH_SHORT).show();
            return;
        }

        if (image.getTag().equals("temp")) {
            Toasty.error(this, "Please Select a Profile Image", Toast.LENGTH_SHORT).show();
        } else {

            pDialog.setTitleText("Uploading Photo...");
            pDialog.show();

            final StorageReference storageReference = mStorageReference.child(mFirebaseAuth.getCurrentUser().getUid()).child(DatabaseFields.userFields.PROFILE_PHOTO);
            UploadTask uploadTask = storageReference.putFile(imageUri);

            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    HashMap<String, String> user_data = new HashMap<>();
                    user_data.put(DatabaseFields.userFields.USER_TYPE, DatabaseFields.constants.USER_TYPE_CUSTOMER);
                    user_data.put(DatabaseFields.userFields.PROFILE_LEVEL, "2");
                    user_data.put(DatabaseFields.customerFields.NAME, cus_name);
                    user_data.put(DatabaseFields.userFields.MOBILE, cus_mobile);
                    user_data.put(DatabaseFields.userFields.PROFILE_PHOTO, task.getResult().toString());

                    pDialog.setTitleText("Waiting...");
                    mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");

                    mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).setValue(user_data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
//                                Toasty.success(RegisterCustomerActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterCustomerActivity.this, Home.class);
                                startActivity(intent);
                                pDialog.dismissWithAnimation();
                                finish();
                            } else {
//                                Toasty.error(RegisterCustomerActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                pDialog.setCancelText(task.getException().getMessage())
                                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            }
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        if (v == save_data) {
            saveData();
        } else if (v == use_camera) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, IMAGE_CAPTURE);
            }
        } else if (v == use_gallery) {
            selectFromGallery();
        } else if (v == image) {
            bottom_sheet_builder = new BottomSheet.Builder(this);
            bottom_sheet_builder.setTitle("Select Option")
                    .setView(bottomSheet)
                    .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri image_uri = data.getData();
            CropImage.activity(image_uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(3, 2)
                    .start(this);
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                bottom_sheet_builder.dismiss();
                imageUri = result.getUri();
                Picasso.get().load(imageUri).into(image);
                image.setTag("selected");
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        } else if (requestCode == IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            bottom_sheet_builder.dismiss();
            selectFromGallery();
        }
    }

    private void selectFromGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    private void initComponents() {
        save_data = findViewById(R.id.cus_reg_btn);
        name = findViewById(R.id.cus_reg_name);
        mobile = findViewById(R.id.cus_reg_mobile);
        image = findViewById(R.id.cus_reg_image);

        use_camera = bottomSheet.findViewById(R.id.select_from_camera);
        use_gallery = bottomSheet.findViewById(R.id.select_from_gallery);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pDialog.dismissWithAnimation();
    }
}
