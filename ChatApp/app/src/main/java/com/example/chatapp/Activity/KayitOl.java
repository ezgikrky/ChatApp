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

import com.example.chatapp.Model.Kullanici;
import com.example.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class KayitOl extends AppCompatActivity {

    private ProgressDialog mProgress; //yükleme ekranı
    private Kullanici mKullanici;
    private LinearLayout mLinear;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser; //kullanıcı kayıt olunca user ıd ye sahip olacaktı
    private TextInputLayout inputIsim, inputEmail,inputSifre,inputSifreTekrar;
    private EditText editIsim, editEmail,editSifre,editSifreTekrar;
    private String txtIsim, txtEmail,txtSifre,txtSifreTekrar;

private void init(){
    mAuth = FirebaseAuth.getInstance();
    mFirestore = FirebaseFirestore.getInstance();

    mLinear=(LinearLayout)findViewById(R.id.kayit_ol_linear);

    inputIsim= (TextInputLayout)findViewById(R.id.kayit_ol_isim);
    inputEmail= (TextInputLayout)findViewById(R.id.kayit_ol_email);
    inputSifre= (TextInputLayout)findViewById(R.id.kayit_ol_sifre);
    inputSifreTekrar= (TextInputLayout)findViewById(R.id.kayit_ol_sifreTekrar);

    editIsim= (EditText)findViewById(R.id.kayit_ol_editIsim);
    editEmail= (EditText)findViewById(R.id.kayit_ol_editEmail);
    editSifre= (EditText)findViewById(R.id.kayit_ol_editSifre);
    editSifreTekrar= (EditText)findViewById(R.id.kayit_ol_editSifreTekrar);
}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit_ol);
        init();


    }


    public void btnKayitOl(View v){
        txtIsim=editIsim.getText().toString();
        txtEmail=editEmail.getText().toString();
        txtSifre=editSifre.getText().toString();
        txtSifreTekrar=editSifreTekrar.getText().toString();

        if (!TextUtils.isEmpty(txtIsim)){ //eğer isim yeri boş değilse
            if(!TextUtils.isEmpty(txtEmail)){
                if(!TextUtils.isEmpty(txtSifre)){
                    if(!TextUtils.isEmpty(txtSifreTekrar)){
                        if(txtSifre.equals(txtSifreTekrar)){ //girilen şifreler eşit mi

                            mProgress=new ProgressDialog(this); //yükleme ekranıdnı kaydetme butonuna tıklayınca yazılacak
                            mProgress.setTitle("Kayıt Olunuyor.");
                            mProgress.show();


                                mAuth.createUserWithEmailAndPassword(txtEmail,txtSifre) //kayıt edildi
                                        .addOnCompleteListener(KayitOl.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    mUser = mAuth.getCurrentUser();

                                                    if (mUser != null) {
                                                        mKullanici = new Kullanici(txtIsim, txtEmail, mUser.getUid(),"default"); //hashmap yerine classla gerçekleştirdim
                                                        mFirestore.collection("Kullanıcılar").document(mUser.getUid())
                                                                .set(mKullanici)
                                                                .addOnCompleteListener(KayitOl.this, task1 -> {
                                                                    if (task1.isSuccessful()) { //işlem başarı ile tamamlandıysa
                                                                        progressAyar();
                                                                        Toast.makeText(KayitOl.this, "Başarıyla Kayıt Oldunuz", Toast.LENGTH_SHORT).show();
                                                                        finish(); //bulunulan activity i yok ettik
                                                                        startActivity(new Intent(KayitOl.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));//kullanıcıyı ana sayfaya yönlendirme ve temizleme

                                                                    } else {
                                                                        progressAyar();
                                                                        Snackbar.make(mLinear, task1.getException().getMessage(), Snackbar.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                    }
                                                } else {
                                                    progressAyar();
                                                    Snackbar.make(mLinear, task.getException().getMessage(), Snackbar.LENGTH_SHORT).show();
                                                }
                                            }
                                        });




                        }else
                            Snackbar.make(mLinear, "Şifreler uyuşmuyor.", Snackbar.LENGTH_SHORT).show(); // görüntü olarak kayıt ol sayfası verildi
                    }else
                        inputSifreTekrar.setError("Lütfen Şifre Bilgisini Giriniz");
                }else
                    inputSifre.setError("Lütfen Geçerli Bir Şifre Giriniz");
            }else
                inputEmail.setError("Lütfen Geçerli Bir E-posta Giriniz");
        } else
            inputIsim.setError("Lütfen Geçerli Bir İsim Giriniz");
    }

    private void progressAyar(){
    if(mProgress.isShowing()) //hala gösterilmeye devam ediyorsa kapatsın
        mProgress.dismiss();
    }
}