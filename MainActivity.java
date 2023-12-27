package com.example.network;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.BitSet;


public class MainActivity extends AppCompatActivity {
    private Button btn1,btn2,btn3,btn4,chkNet;
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(MainActivity.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if(ni == null)
            Toast.makeText(this,"沒有網路",Toast.LENGTH_SHORT).show();
        else if(ni.isConnectedOrConnecting())
            Toast.makeText(this,"WIFI是活動的",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this,"沒有網路",Toast.LENGTH_SHORT).show();

        btn1 = findViewById(R.id.button);
        btn2 = findViewById(R.id.button2);
        btn3 = findViewById(R.id.button3);
        btn4 = findViewById(R.id.button4);
        chkNet = findViewById(R.id.button5);

        iv = findViewById(R.id.imageView);

        btn1.setOnClickListener(btnClick);
        btn2.setOnClickListener(btnClick);
        btn3.setOnClickListener(btnClick);
        btn4.setOnClickListener(btnClick);
        chkNet.setOnClickListener(chkNetwork);
    }

    private View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.button:
                    downloadImageAndShow(1,1);
                    break;
                case R.id.button2:
                    downloadImageAndShow(1,2);
                    break;
                case R.id.button3:
                    downloadImageAndShow(2,1);
                    break;
                case R.id.button4:
                    downloadImageAndShow(2,2);
                    break;
            }

        }
    };
    private View.OnClickListener chkNetwork = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(checkNetworkState() != true){
                Toast.makeText(getApplicationContext(),"請打開網路",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(),"網路已開啟",Toast.LENGTH_SHORT).show();
            }
        }
    };
    private boolean checkNetworkState(){
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(MainActivity.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if((ni == null ) || (ni.isConnected() == false)){
            return false;
        }
        return true;
    }
    private void downloadImageAndShow(int type,int id){
        new MyAsyncTask().execute("http://192.168.1.103:8080/Servlet/ImageShower?type=" + type + "&id=" + id);
    }

    private class MyAsyncTask extends AsyncTask<String, Void, Bitmap>{
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            //super.onPreExecute();
            iv.setImageBitmap(bitmap);
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            URL url;
            HttpURLConnection urlConnection = null;
            Bitmap bm = null;

            try{
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                if(urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK){
                    urlConnection.disconnect();
                    return null;
                }
                byte[] b = new byte[2048];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
                int len = 0;
                while((len=in.read(b)) > 0){
                    baos.write(b,0,len);
                }
                bm = BitmapFactory.decodeByteArray(baos.toByteArray(),0,baos.size());
                baos.close();
                in.close();

            } catch (MalformedURLException e){
                Log.d("MyApp","MalformedURLException");
                return null;
            } catch(IOException e){
                Log.d("MyApp","IOException");
                return null;
            } finally {
                urlConnection.disconnect();
            }
            return bm;
        }
    }
}