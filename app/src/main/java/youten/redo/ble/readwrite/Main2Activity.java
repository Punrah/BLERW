package youten.redo.ble.readwrite;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import youten.redo.ble.readwrite.AsyncTask.MyAsyncTask;
import youten.redo.ble.readwrite.app.AppConfig;

public class Main2Activity extends AppCompatActivity {
   TextView textView,textViewLokasi,textViewKeterangan,textViewKategori,textViewKodepos;
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    private String mDeviceAddress = "unknow";
    private String mDeviceName = "unknow";
    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList();
    public static BluetoothGattCharacteristic mBluetoothGattCharacteristic = null;
    private boolean isNotify = false;
    boolean first=false;
    String nilai="";

    String lokasi,kodepos,kategori,keterangan,suara;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main2);
        textView =(TextView) findViewById(R.id.textview);
        textViewLokasi =(TextView) findViewById(R.id.lokasi);
        textViewKodepos =(TextView) findViewById(R.id.kode_pos);
        textViewKategori =(TextView) findViewById(R.id.kategori);
        textViewKeterangan =(TextView) findViewById(R.id.keterangan);



        Intent intent = getIntent();
//        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
//        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        mDeviceName = "LinKim BLE";
        mDeviceAddress = "E9:8E:69:5A:1D:B5";

        bindService(new Intent(this, BluetoothLeService.class), this.mServiceConnection, Context.BIND_AUTO_CREATE);
        registerReceiver(this.mGattUpdateReceiver, makeGattUpdateIntentFilter());



    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e("TchipBLE DEVICE DETAIL", "Unable to initialize Bluetooth");
                finish();
            }
            mBluetoothLeService.connect(mDeviceAddress);
        }

        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                clearUI();
                Toast.makeText(getApplicationContext(), R.string.device_disconnect, Toast.LENGTH_LONG).show();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else {
                BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action);
            }
        }
    };

    private void clearUI() {
        textView.setText("");
    }
    private static IntentFilter makeGattUpdateIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices != null) {
            String unknownServiceString = getResources().getString(R.string.unknown_service);
            String unknownCharaString = getResources().getString(R.string.unknown_charactistic);
            ArrayList<HashMap<String, String>> gattServiceData = new ArrayList();
            ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList();
            this.mGattCharacteristics = new ArrayList();
            for (BluetoothGattService gattService : gattServices) {
                List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    if((gattCharacteristic.getProperties()&16)==16) {
//                        String debugUuid = gattCharacteristic.getUuid().toString();
//                        Bundle bundle = new Bundle();
//                        Log.v("TchipBLE DEVICE DETAIL", debugUuid);
//                        Intent intent = new Intent(this, ReadWriteActivity.class);
                          //startActivity(intent);
                        readWrite(gattCharacteristic);

                    }

                }
            }
        }
    }

    private void readWrite(BluetoothGattCharacteristic gattCharacteristic)
    {
        this.mBluetoothGattCharacteristic = (BluetoothGattCharacteristic) gattCharacteristic;
//                        startActivity(intent);
        if (this.mBluetoothGattCharacteristic == null || mBluetoothLeService == null) {
            finish();
        }
        init(this.mBluetoothGattCharacteristic.getProperties());
    }

    private void init(int properties) {

        if ((properties & 16) == 16) {
            Log.v("TchipBLE", "Start Notify");
            Toast.makeText(getApplicationContext(), R.string.start_lisenting, Toast.LENGTH_LONG).show();
            mBluetoothLeService.setCharacteristicNotification(mBluetoothGattCharacteristic, true);
            this.isNotify = true;
        }
    }

    protected void onPause() {
        super.onPause();
        unregisterReceiver(this.mGattUpdateReceiver2);
    }

    protected void onResume() {
        super.onResume();
        if (this.mBluetoothLeService != null) {
            Log.d("TchipBLE DEVICE DETAIL", "Connect request result=" + this.mBluetoothLeService.connect(this.mDeviceAddress));
        }
        registerReceiver(this.mGattUpdateReceiver2, makeGattUpdateIntentFilter());
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.mGattUpdateReceiver);
        unbindService(this.mServiceConnection);
        if (this.isNotify) {
            mBluetoothLeService.setCharacteristicNotification(mBluetoothGattCharacteristic, false);
        }
    }

    private final BroadcastReceiver mGattUpdateReceiver2 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                Toast.makeText(getApplicationContext(), R.string.device_disconnect, Toast.LENGTH_LONG).show();
                finish();
            } else if (!BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action) && BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                receive(intent.getExtras().getString(BluetoothLeService.EXTRA_DATA));
               // Toast.makeText(context, intent.getExtras().getString(BluetoothLeService.EXTRA_DATA), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void receive(String msg) {
        if(nilai.contentEquals(""))
        {
            nilai=msg;
        }
        else
        {
            nilai=nilai+msg;
            String[] nilai2=nilai.split(":");

            lokasi="";
            kodepos="";
            kategori="";
            keterangan="";
            suara="";
            textView.setText("");
            textViewLokasi.setText("");
            textViewKodepos.setText("");
            textViewKategori.setText("");
            textViewKeterangan.setText("");


            String uuid =nilai2[1].replace("\n", "");
            new getMotor(uuid).execute();
            textView.setText(uuid);


        }

    }

    private class getMotor extends MyAsyncTask {
        String uuid;

        public getMotor(String uuid)
        {
            this.uuid=uuid;

        }




        @Override
        public Context getContext () {
            return Main2Activity.this;
        }



        @Override
        public void setSuccessPostExecute() {

            //textView.setText(uuid);



            String url = "http://www.blind.web.id/admin/assets/uploads/files/"+(suara); // your URL here

            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.setDataSource(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                mediaPlayer.prepare(); // might take long! (for buffering, etc)
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.start();
            nilai="";


            textViewLokasi.setText(lokasi);
            textViewKodepos.setText(kodepos);
            textViewKategori.setText(kategori);
            textViewKeterangan.setText(keterangan);



        }

        @Override
        public void setFailPostExecute() {

        }

        public void postData() {


            String url = AppConfig.URL+Uri.encode(uuid);
//            try {
//                url = URLEncoder.encode(url, "UTF-8");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            try {
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                String jsonStr = EntityUtils.toString(entity, "UTF-8");

                if (jsonStr != null) {
                    try {
                        isSucces=true;
                        JSONObject obj = new JSONObject(jsonStr);

                        JSONArray jsonArray = obj.getJSONArray("results");
                        lokasi = jsonArray.getJSONObject(0).getString("lokasi");
                        kodepos = jsonArray.getJSONObject(0).getString("kodepos");
                        kategori = jsonArray.getJSONObject(0).getString("kategori");
                        keterangan = jsonArray.getJSONObject(0).getString("keterangan");
                        suara = jsonArray.getJSONObject(0).getString("suara");


                    } catch (final JSONException e) {
                        badServerAlert();
                    }
                } else {
                    badServerAlert();
                }
            } catch (IOException e) {
                badInternetAlert();
            }
        }






    }



}
