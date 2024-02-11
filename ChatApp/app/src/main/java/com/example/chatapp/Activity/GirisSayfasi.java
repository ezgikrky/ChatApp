package com.example.chatapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GirisSayfasi extends AppCompatActivity {


    private ProgressDialog mProgress; //yükleme ekranı
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private LinearLayout mLinear;
    private TextInputLayout inputEmail,inputSifre;
    private EditText editEmail,editSifre;
    private String txtEmail,txtSifre;

    private void init(){
        mLinear=(LinearLayout)findViewById(R.id.giris_linear);
        mAuth= FirebaseAuth.getInstance();

        mUser=mAuth.getCurrentUser(); // her seferinde tekrardan giriş yapmak zorunda kalmayacak. giriş özelliğii

        inputEmail=(TextInputLayout)findViewById(R.id.giris_email);
        inputSifre=(TextInputLayout)findViewById(R.id.giris_sifre);

        editEmail=(EditText)findViewById(R.id.giris_editEmail);
        editSifre=(EditText) findViewById(R.id.giris_editSifre);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris_sayfasi);
        init();

        if(mUser!= null){ // mUser null değilse direkt aktiviteye geçiş yapmasını sağlar
            finish();
            startActivity(new Intent(GirisSayfasi.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
    }

    public void btnGirisYap(View v){
            txtEmail=editEmail.getText().toString(); //textleri çağırma
            txtSifre=editSifre.getText().toString();

            if(!TextUtils.isEmpty(txtEmail)){ // email boş değilse
                if(!TextUtils.isEmpty(txtSifre)){  //şifre boş değilse kullanıcı girişini kontrol ettiririz

                    mProgress=new ProgressDialog(this);
                    mProgress.setTitle("Giriş Yapılıyor...");
                    mProgress.show();

                    mAuth.signInWithEmailAndPassword(txtEmail,txtSifre)
                            .addOnCompleteListener(GirisSayfasi.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){  // eğer işlem başarı ile gerçekleştiyse mesaj gönderecek

                                        progressAyar(); //başarıyla gerçekleştiyse processi kapatacak

                                        Toast.makeText(GirisSayfasi.this, "Başarıyla Giriş Yaptınız.", Toast.LENGTH_SHORT).show();
                                        finish();
                                        startActivity(new Intent(GirisSayfasi.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    }else {
                                        progressAyar();
                                        Snackbar.make(mLinear, task.getException().getMessage(), Snackbar.LENGTH_SHORT).show(); //kısa bir mesaj verecek
                                    }
                                }
                            });

                } else{
                    inputSifre.setError("Lütfen Geçerli Bir Şifre Adresi Giriniz");
                }
            }else{
                inputEmail.setError("Lütfen Geçerli Bir Email Adresi Giriniz");
            }
    }

    /* Kayıt olmaya gider
    public void btnGitKayitOl(View v){
        startActivity(new Intent(GirisSayfasi.this, KayitOl.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

    }
    */

    private void progressAyar(){
        if(mProgress.isShowing()) //hala gösterilmeye devam ediyorsa kapatsın
            mProgress.dismiss();
    }
}