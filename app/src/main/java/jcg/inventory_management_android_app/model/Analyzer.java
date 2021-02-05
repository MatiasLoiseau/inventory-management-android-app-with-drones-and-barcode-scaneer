package jcg.inventory_management_android_app.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.ArrayList;
import java.util.List;

import jcg.inventory_management_android_app.R;

public class Analyzer implements ImageAnalysis.Analyzer {

    Context context;
    int currentImage;
    BarcodeScannerOptions options;

    public Analyzer(Context context) {
        this.context = context;
        options = new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                                Barcode.FORMAT_QR_CODE,
                                Barcode.FORMAT_AZTEC)
                        .build();
    }

    public void start(Bitmap bitmap) {

        InputImage image = InputImage.fromBitmap(bitmap, 0);
        //InputImage image = InputImage.fromBitmap(bitmap, rotationDegree);
        // Pass image to an ML Kit Vision API
        // ...

        //BarcodeScanner scanner = BarcodeScanning.getClient(options);
        BarcodeScanner scanner = BarcodeScanning.getClient();

        processImage(scanner, image);

    }




    void processImage(BarcodeScanner scanner, InputImage image){

        Task<List<Barcode>> result = scanner.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {
                        // Task completed successfully
                        // ...
                        getInfoFromBarcodes(barcodes);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    void getInfoFromBarcodes(List<Barcode> barcodes){

        if (barcodes.size() == 0)
            Toast.makeText(context, "Error al escanear Imagen" , Toast.LENGTH_SHORT).show();

        else
            for (Barcode barcode: barcodes) {

                Rect bounds = barcode.getBoundingBox();
                Point[] corners = barcode.getCornerPoints();

                String rawValue = barcode.getRawValue();

                int valueType = barcode.getValueType();

                Toast.makeText(context,  "Raw Value: '" + rawValue + "'" , Toast.LENGTH_SHORT).show();


                /*
                // See API reference for complete list of supported types
                switch (valueType) {
                    case Barcode.TYPE_WIFI:
                        String ssid = barcode.getWifi().getSsid();
                        String password = barcode.getWifi().getPassword();
                        int type = barcode.getWifi().getEncryptionType();
                        break;
                    case Barcode.TYPE_URL:
                        String title = barcode.getUrl().getTitle();
                        String url = barcode.getUrl().getUrl();
                        break;
                }
                */
            }
    }


    @Override
    public void analyze(@NonNull ImageProxy image, int rotationDegrees) {

    }

}
