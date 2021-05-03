package jcg.inventory_management_android_app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static jcg.inventory_management_android_app.model.BluetoothConstants.*;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MyActivity";

    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket socket = null;
    private TextView textView;
    private ArrayList<String> barcodeArray;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);

        textView =  findViewById(R.id.textView);
        
        buttonsImplementations();
        
        //Bluetooth initialize
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this,
                    "Este dispositivo no se puede conectar a Bluetooth.",
                    Toast.LENGTH_LONG).show();
            finish();
        }
        else if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

        } else
            initializeThread();

    }

    private void buttonsImplementations() {

        Button changeViewButton;
        ArrayList<String> barcodeArray = new ArrayList<>();

        //Add Button
        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(view -> {

            barcodeArray.add(textView.toString());
            Toast.makeText(this, "Se agregó un codigo a la lista", Toast.LENGTH_SHORT).show();

        });

        ListView listView = findViewById(R.id.list_view);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                barcodeArray );

        listView.setAdapter(arrayAdapter);

        //Change View Button
        changeViewButton = findViewById(R.id.changeViewButton);
        changeViewButton.setOnClickListener(view -> {

            arrayAdapter.notifyDataSetChanged();
        });

    }

    private void initializeThread() {

        AcceptData acceptData = new AcceptData();
        acceptData.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initializeThread();
    }

    class AcceptData extends Thread {

        private final BluetoothServerSocket mmServerSocket;
        private String device;

        public AcceptData() {

            BluetoothServerSocket tmp = null;
            try {
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("Bluetooth", MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmServerSocket = tmp;
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            device = socket.getRemoteDevice().getName();
            Toast.makeText(getBaseContext(), "Connected to " + device, Toast.LENGTH_SHORT).show();

        }


        public void run() {

            InputStream socketInputStream = null;
            try {
                socketInputStream = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Keep looping to listen for received messages
            byte[] buffer = new byte[256];
            int bytes;

            while (true) {
                try {

                    bytes = socketInputStream.read(buffer);

                    String code = new String(buffer,0,bytes);

                    runOnUiThread(() -> textView.setText(code));

                    buffer = new byte[256];

                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }

            if (socket != null) {
                try {
                    mmServerSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}