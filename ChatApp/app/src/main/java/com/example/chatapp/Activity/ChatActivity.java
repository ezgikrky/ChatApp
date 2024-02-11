package com.example.chatapp.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.Adapter.ChatAdapter;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.Kullanici;
import com.example.chatapp.R;
import com.google.android.datatransport.runtime.dagger.multibindings.ElementsIntoSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private FirebaseUser mUser;
    private HashMap<String,Object> mData;

    private RecyclerView mRecyclerView;
    private EditText editMesaj;
    private String txtMesaj, docId;

    private CircleImageView hedefProfil;
    private TextView hedefIsim;
    private Intent gelenIntent;
    private DocumentReference hedefRef;
    private FirebaseFirestore mFireStore;
    private String hedefId, kanalId, hedefProfilUrl;
    private Kullanici hedefKullanici;

    private Query chatQuery;
    private ArrayList<Chat> mChatList;
    private Chat mChat;
    private ChatAdapter chatAdapter;




    private void init(){
        mRecyclerView=(RecyclerView)findViewById(R.id.chat_activity_recyclerView);
        editMesaj=(EditText)findViewById(R.id.chat_activity_editMesaj);
        hedefProfil=(CircleImageView)findViewById(R.id.chat_activity_imgHedefProfil);
        hedefIsim=(TextView)findViewById(R.id.chat_activity_txtHedefIsim);

        mFireStore=FirebaseFirestore.getInstance();
        gelenIntent=getIntent();
        hedefId=gelenIntent.getStringExtra("hedefId");
        kanalId=gelenIntent.getStringExtra("kanalId");
        hedefProfilUrl=gelenIntent.getStringExtra("hedefProfil");

        mChatList = new ArrayList<>();

        mUser= FirebaseAuth.getInstance().getCurrentUser();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();

        hedefRef=mFireStore.collection("Kullanıcılar").document(hedefId);
        hedefRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(ChatActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (value != null && value.exists()) {
                    hedefKullanici = value.toObject(Kullanici.class);

                    if (hedefKullanici != null) {
                        hedefIsim.setText(hedefKullanici.getKullaniciIsmi());

                        if (hedefKullanici.getKullaniciProfil().equals("default"))
                            hedefProfil.setImageResource(R.mipmap.ic_launcher);
                        else
                            Picasso.get().load(hedefKullanici.getKullaniciProfil()).resize(76, 76).into(hedefProfil);
                    }
                }
            }
        });

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        chatQuery= mFireStore.collection("ChatKanalları").document(kanalId).collection("Mesajlar")
                .orderBy("mesajTarihi",Query.Direction.ASCENDING);  //mesaj tarihine göre sıralayacak

        chatQuery.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null){
                    Toast.makeText(ChatActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                    return;
                }

                if(value!=null){
                    mChatList.clear();

                    for(DocumentSnapshot snapshot: value.getDocuments()){
                        mChat=snapshot.toObject(Chat.class);

                        assert mChat!= null;
                        mChatList.add(mChat);


                    }

                    chatAdapter = new ChatAdapter(mChatList,ChatActivity.this,mUser.getUid(),hedefProfilUrl);
                    mRecyclerView.setAdapter(chatAdapter);
                }
            }
        });
    }

    public void btnMesajGonder(View v){
        txtMesaj=editMesaj.getText().toString();
        
        if(!TextUtils.isEmpty(txtMesaj)){
            docId= UUID.randomUUID().toString();
            mData = new HashMap<>();
            mData.put("mesajIcerigi",txtMesaj);
            mData.put("gonderen",mUser.getUid());
            mData.put("alici", hedefId);
            mData.put("mesajTipi", "text");
            mData.put("mesajTarihi", FieldValue.serverTimestamp()); // sunucudan gelen saati aldık
            mData.put("docId", docId);

            mFireStore.collection("ChatKanalları").document(kanalId).collection("Mesajlar").document(docId)
                    .set(mData)
                    .addOnCompleteListener(ChatActivity.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                                editMesaj.setText("");
                            else
                                Toast.makeText(ChatActivity.this, task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    });
        }
        else
            Toast.makeText(ChatActivity.this,"Mesaj Göndermek İçin Bir Şeyler Yazın", Toast.LENGTH_SHORT).show();

    }

    public void btnChatKapat(View v){
            finish();
    }



}