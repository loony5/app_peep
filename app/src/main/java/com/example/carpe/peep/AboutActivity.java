package com.example.carpe.peep;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    public void callClick(View v){
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:0000-0000")));
    }

    public void mailClick(View v){

        Uri uri = Uri.parse("mailto:peep@address.com");
        Intent email = new Intent(Intent.ACTION_SENDTO, uri); //sendto 특정 누군가한테 email을 보낸다.
        startActivity(email);

    }
}
