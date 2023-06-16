package com.ml.testch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    Button btn;
    TextView tv;
    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn=(Button)findViewById(R.id.button);
        tv = (TextView) findViewById(R.id.textview);
        iv = (ImageView) findViewById(R.id.imageview);

        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }




        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
                //PyObject obj = pyobj.callAttr("main", "working");
                //tv.setText(obj.toString());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultcode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultcode, data);

        if(requestCode==1 && resultcode==RESULT_OK && null!=data){
            Uri selectedimg = data.getData();
            iv.setImageURI(selectedimg);

            String encoded="";
            byte[] bytes = readBytes(selectedimg, getContentResolver());
            encoded = Base64.encodeToString(bytes, 0);

            Python py = Python.getInstance();
            PyObject pyobj = py.getModule("checkfile");

            PyObject obj = pyobj.callAttr("main", encoded);

            tv.setText("The detected number is "+obj.toString());

        }
    }


    private byte[] readBytes(Uri selectedimg, ContentResolver contentResolver) {

        // this dynamically extends to take the bytes you read
        InputStream inputStream = null;
        try {
            inputStream = contentResolver.openInputStream(selectedimg);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the
        // byteBuffer
        int len = 0;
        while (true) {
            try {
                if (!((len = inputStream.read(buffer)) != -1)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            byteBuffer.write(buffer, 0, len);
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }
}
