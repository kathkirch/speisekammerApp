package com.example.myapplication;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;


public class BarcodeScanner extends AppCompatActivity {

    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;

    private TextView barcodeText;
    private String barcodeData;
    private boolean isBarcode;
    private Button checkBarcode;
    Intent intent;
    private String location;
    private Button noBarcode;


    public static final String BARCODE = "barcodeText";
    public static final String LOCATION = "location";
    public static final String ISBARCODE = "isBarcode";

    private RadioButton pantry;
    private RadioButton pantryBoard;
    private RadioButton fridge;
    private RadioButton freezer;

    /*
    private String barcode;
    private String productName;
    private String productDescription;
    private String packSize;
    private String unit;
    private double packageAmount;
    private String location;

    //ActivityResultLauncher<Intent> someActivityResultLauncher;

    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final DatabaseReference productNode = database.getReference("products");
    DatabaseReference locationNode;
    DatabaseReference barcodeNode;

     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);

        surfaceView = findViewById(R.id.surface_view);
        barcodeText = findViewById(R.id.barcode_text);
        checkBarcode = findViewById(R.id.bt_check_barcode);
        noBarcode = findViewById(R.id.btNObarcode);

        pantry = findViewById(R.id.radioPantry);
        pantryBoard = findViewById(R.id.radioPantryBoard);
        fridge = findViewById(R.id.radioFridge);
        freezer = findViewById(R.id.radioFreezer);

        //Intent to start new Activity (Add Product into Database)
        intent = new Intent (getApplicationContext(), IntoDatabase.class);


        //Listener for Button to add product without barcode
        noBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isBarcode = false;

                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(BarcodeScanner.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.no_barcode_popup, null);
                mBuilder.setView(dialogView);
                final EditText editText = dialogView.findViewById(R.id.popUpNoBarcodeInput_editText);
                final Button button = dialogView.findViewById(R.id.button_noBarcode);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //hier neues Produkt hinzufuegen
                        //
                        final String inputText = editText.getText().toString();
                        String noBarcode_Code = "NB_" + inputText;

                        String loco = locationPicker();

                        intent.putExtra(LOCATION, loco);
                        intent.putExtra(BARCODE, noBarcode_Code);
                        intent.putExtra(ISBARCODE, isBarcode);

                        startActivity(intent);
                    }
                });

                final AlertDialog dialog = mBuilder.create();
                dialog.show();

            }
        });

        //Listener for Button to add product with barcode
        checkBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                putInIntentandStart();
//                String loco = locationPicker();
//                String barcode = barcodeText.getText().toString();
//
//
//                if (location.isEmpty()){
//                    Toast.makeText(getApplicationContext(), "Bitte Lagerort wählen", Toast.LENGTH_SHORT).show();
//                } else if (!isBarcode){
//                    Toast.makeText(getApplicationContext(), "Bitte Barcode scannen", Toast.LENGTH_SHORT).show();
//                } else if (!location.isEmpty() && isBarcode){
//                    intent.putExtra(LOCATION, loco);
//                    intent.putExtra(BARCODE, barcode);
//                    intent.putExtra(ISBARCODE, isBarcode);
//                    startActivity(intent);
//                }
            }
        });
    }

    public void putInIntentandStart () {
        String loco = locationPicker();
        String barcode = barcodeText.getText().toString();

        if (location.isEmpty()){
            Toast.makeText(getApplicationContext(), "Bitte Lagerort wählen", Toast.LENGTH_SHORT).show();
        } else if (!isBarcode){
            Toast.makeText(getApplicationContext(), "Bitte Barcode scannen", Toast.LENGTH_SHORT).show();
        } else if (!location.isEmpty() && isBarcode){
            intent.putExtra(LOCATION, loco);
            intent.putExtra(BARCODE, barcode);
            intent.putExtra(ISBARCODE, isBarcode);
            startActivity(intent);
        }

    }

    public String locationPicker (){

        location = "";

        if (pantry.isChecked()){
            location = "Speis";
        }
        else if (pantryBoard.isChecked()){
            location = "Vorratsschrank";
        }
        else if (fridge.isChecked()){
            location = "Kühlschrank";
        }
        else if (freezer.isChecked()){
            location = "Gefrierschrank";
        }
        return  location;
    }

    private void initialiseDetectorsAndSources() {

        Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(BarcodeScanner.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(BarcodeScanner.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Toast.makeText(getApplicationContext(),
                        "To prevent memory leaks barcode scanner has been stopped",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(@NotNull Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {

                    barcodeText.post(new Runnable() {

                        @Override
                        public void run() {
                            if (barcodes.valueAt(0).email != null) {
                                barcodeText.removeCallbacks(null);
                                barcodeData = barcodes.valueAt(0).email.address;
                                barcodeText.setText(barcodeData);
                                isBarcode = true;
                            } else {
                                barcodeData = barcodes.valueAt(0).displayValue;
                                barcodeText.setText(barcodeData);
                                isBarcode = true;
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }
}