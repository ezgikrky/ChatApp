package com.example.chatapp.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chatapp.Adapter.MesajlarAdapter;
import com.example.chatapp.Model.MesajIstegi;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class MesajlarFragment extends Fragment {

   private View v;
   private RecyclerView mRecyclerView;
   private FirebaseFirestore mFireStore;
   private Query mQuery;
   private ArrayList<MesajIstegi> mArrayList;
   private MesajIstegi mesajIstegi;
   private FirebaseUser mUser;

   private MesajlarAdapter mesajlarAdapter;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v= inflater.inflate(R.layout.fragment_mesajlar, container, false);

        mFireStore=FirebaseFirestore.getInstance();
        mUser= FirebaseAuth.getInstance().getCurrentUser();
        mArrayList=new ArrayList<>();



        mRecyclerView= v.findViewById(R.id.mesajlar_fragment_recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(v.getContext(),LinearLayoutManager.VERTICAL,false));

        mQuery=mFireStore.collection("Kullanıcılar").document(mUser.getUid()).collection("Kanal");
        mQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error!=null){
                    Toast.makeText(v.getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                if(value!= null){
                    mArrayList.clear();


                    for (DocumentSnapshot snapshot: value.getDocuments()){
                        mesajIstegi= snapshot.toObject(MesajIstegi.class);



                        assert  mesajIstegi != null;
                        mArrayList.add(mesajIstegi);



                    }

                    mesajlarAdapter = new MesajlarAdapter(mArrayList,v.getContext());
                    mRecyclerView.setAdapter(mesajlarAdapter);

                }
            }
        });

        return v;
    }
}