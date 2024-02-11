package com.example.chatapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.chatapp.R;

public class AnaSayfa extends AppCompatActivity {

    private Button button_girisyap, button_kayitol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ana_sayfa);

        button_girisyap=findViewById(R.id.button_girisyap);
        button_kayitol=findViewById(R.id.button_kayitol);

        button_girisyap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AnaSayfa.this, GirisSayfasi.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        button_kayitol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AnaSayfa.this, KayitOl.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });


    }

}