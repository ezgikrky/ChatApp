package com.example.chatapp.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chatapp.Activity.GirisSayfasi;
import com.example.chatapp.Activity.KayitOl;
import com.example.chatapp.Activity.MainActivity;
import com.example.chatapp.Adapter.KullaniciAdapter;
import com.example.chatapp.Model.Kullanici;
import com.example.chatapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class KullanicilarFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private View v;
    private KullaniciAdapter mAdapter;

    private ArrayList<Kullanici> mKullaniciList;
    private Kullanici mKullanici;

    private FirebaseUser mUser;  //kullanıcı listesinde kendimizi görmemizi engelleyen kod

    private Query mQuery;
    private FirebaseFirestore mFireStore;


    private DocumentReference mRef;
    private Kullanici kullanici;


    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_kullanicilar, container, false);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mFireStore = FirebaseFirestore.getInstance();

        mKullaniciList = new ArrayList<>();

        mRecyclerView = v.findViewById(R.id.kullanici_fragment_recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL, false));

        mRef= mFireStore.collection("Kullanıcılar").document(mUser.getUid());
        mRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()) {
                            kullanici = documentSnapshot.toObject(Kullanici.class);

                            mQuery = mFireStore.collection("Kullanıcılar");
                            mQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) { // QuerySnapshot geriye koleksiyon yapısı şeklinde değerleri döndürüyor
                                    if (error != null) {
                                        Toast.makeText(v.getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    if (value != null) {
                                        mKullaniciList.clear();

                                        //DocumentSnapshot dökümanlara tek tek erişmemizi sağlar
                                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                                            mKullanici = snapshot.toObject(Kullanici.class); //gelen veriler sınıfın içerisinde dolduruldu

                                            assert mKullanici != null;
                                            if (!mKullanici.getKullaniciId().equals(mUser.getUid()))
                                                mKullaniciList.add(mKullanici);
                                        }

                                        mRecyclerView.addItemDecoration(new LinearDecoration(20, mKullaniciList.size()));


                                        mAdapter = new KullaniciAdapter(mKullaniciList, v.getContext(),kullanici.getKullaniciId(), kullanici.getKullaniciIsmi(), kullanici.getKullaniciProfil());


                                        mRecyclerView.setAdapter(mAdapter);

                                    }
                                }
                            });


                        }

                    }
                });




        return v;
    }


    class LinearDecoration extends RecyclerView.ItemDecoration {
        // her kullanıcıdan sonra latta bira boşluk olsun ama bu son kullanıcıya kada olanlarda dedvam edecek
        private int boslukMiktari;
        private int veriSayisi;

        public LinearDecoration(int boslukMiktari, int veriSayisi) {
            this.boslukMiktari = boslukMiktari;
            this.veriSayisi = veriSayisi;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int pos = parent.getChildAdapterPosition(view); //içeriye eklediğim her değer için pozisyon ekledi

            if (pos != (veriSayisi - 1))
                outRect.bottom = boslukMiktari; //boşluk miktarı kadar boşluk bırakacak
        }
    }
}