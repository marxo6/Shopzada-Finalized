package com.utcc.shopzada.StoreComponent;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.utcc.shopzada.MainActivity;
import com.utcc.shopzada.Prevalent.Prevalent;
import com.utcc.shopzada.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddProductFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final int IMAGE_PICK = 1;
    private Uri imageUri;
    private EditText pdName, pdDescription, pdAmount, pdPrice;
    private ImageView noImageIcon, pdImage;
    private MainActivity mainActivity;
    private String saveCurrentDate, saveCurrentTime, name, description;
    private int amount, price;
    private FirebaseStorage storage;
    private FirebaseDatabase database;
    private StorageReference imageRef;
    private DatabaseReference dataRef;
    private String productKey, productId, downloadImageKey;

    public AddProductFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddProductFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddProductFragment newInstance(String param1, String param2) {
        AddProductFragment fragment = new AddProductFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);

        mainActivity = (MainActivity) getActivity();
        ImageView backPress = (ImageView) view.findViewById(R.id.backButton);
        noImageIcon = (ImageView) view.findViewById(R.id.noimageIcon);
        pdImage = (ImageView) view.findViewById(R.id.productImage);
        pdName = (EditText) view.findViewById(R.id.productNameInput);
        pdDescription = (EditText) view.findViewById(R.id.productDescriptionInput);
        pdAmount = (EditText) view.findViewById(R.id.productAmountInput);
        pdPrice = (EditText) view.findViewById(R.id.productPriceInput);
        ConstraintLayout add = (ConstraintLayout) view.findViewById(R.id.addButton);
        ConstraintLayout imageHolder = (ConstraintLayout) view.findViewById(R.id.productImageContainer);

        storage = FirebaseStorage.getInstance("gs://shopzadaproject.appspot.com");
        database = FirebaseDatabase.getInstance("https://shopzadaproject-default-rtdb.asia-southeast1.firebasedatabase.app/");
        imageRef = storage.getReference("Products Images");
        dataRef = database.getReference("Products Unverified");

        backPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.onBackPressed();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProduct();
            }
        });

        imageHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        return view;
    }

    private void openGallery() {
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, IMAGE_PICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            noImageIcon.setVisibility(View.GONE);
            pdImage.setVisibility(View.VISIBLE);
            pdImage.setImageURI(imageUri);
        }
    }

    private void addProduct() {
        name = pdName.getText().toString();
        description = pdDescription.getText().toString();
        amount = Integer.parseInt(pdAmount.getText().toString());
        price = Integer.parseInt(pdPrice.getText().toString());

        if (name.isEmpty()) {
            Toast.makeText(mainActivity, "Name cannot be empty!", Toast.LENGTH_SHORT).show();
            pdName.requestFocus();
            return;
        } else if (description.isEmpty()) {
            Toast.makeText(mainActivity, "Description cannot be empty!", Toast.LENGTH_SHORT).show();
            pdDescription.requestFocus();
            return;
        } else if (amount < 1) {
            Toast.makeText(mainActivity, "Amount cannot be less than 1!", Toast.LENGTH_SHORT).show();
            pdAmount.requestFocus();
            return;
        } else if (price < 1) {
            Toast.makeText(mainActivity, "Price cannot be less than 1!", Toast.LENGTH_SHORT).show();
            pdPrice.requestFocus();
            return;
        } else if (imageUri == null) {
            Toast.makeText(mainActivity, "No image is selected!", Toast.LENGTH_SHORT).show();
        } else {
            validateData();
        }
    }

    private void validateData() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productKey = saveCurrentDate + saveCurrentTime;
        productId = imageUri.getLastPathSegment() + "_" + Prevalent.currentOnlineUser.getUsername();

        StorageReference reference = imageRef.child(imageUri.getLastPathSegment() + "_" + Prevalent.currentOnlineUser.getUsername());

        final UploadTask uploadTask = reference.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mainActivity, "Error: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(mainActivity, "Successfully uploaded!", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        downloadImageKey = reference.getDownloadUrl().toString();
                        return reference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(mainActivity, "Image uploaded to database!", Toast.LENGTH_SHORT).show();
                            downloadImageKey = task.getResult().toString();
                            uploadProduct();
                        }
                    }
                });
            }
        });
    }

    private void uploadProduct() {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("id", productId);
        productMap.put("name", name);
        productMap.put("description", description);
        productMap.put("amount", amount);
        productMap.put("price", price);
        productMap.put("owner", Prevalent.currentOnlineUser.getUsername());
        productMap.put("image", downloadImageKey);
        productMap.put("rating", 0);
        productMap.put("views", 0);

        dataRef.child(productId).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(mainActivity, "Successful!", Toast.LENGTH_SHORT).show();
                            mainActivity.onBackPressed();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mainActivity, "Error: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}