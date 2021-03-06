package jcg.inventory_management_android_app;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import jcg.inventory_management_android_app.model.Analyzer;


public class CaptureActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button analyzeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);

        imageView =  findViewById(R.id.imageView);
        byte[] b = (getIntent().getExtras()).getByteArray("image");

        Bitmap image = BitmapFactory.decodeByteArray(b, 0, b.length);
        imageView.setImageBitmap(image);

        //Analyze Button
        analyzeButton = findViewById(R.id.analyzeButton);
        analyzeButton.setOnClickListener(view -> {

            Analyzer analyzer = new Analyzer(this);
            analyzer.start(image);
        });

    }

}