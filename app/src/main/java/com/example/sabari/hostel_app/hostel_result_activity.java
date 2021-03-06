package com.example.sabari.hostel_app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class hostel_result_activity extends AppCompatActivity {

    private DatabaseReference mfirebase;

    private RecyclerView mRecyclerview;

    private Bundle mbundle;

    public StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hostel_result_activity);

       Bundle b = getIntent().getExtras();

       String searchkey = b.getString("sv");
        String searchfield = b.getString("field");

        mStorageRef = FirebaseStorage.getInstance().getReference();

        mfirebase = FirebaseDatabase.getInstance().getReference().child("profile");
        final Bundle mbundle = new Bundle();
        mRecyclerview= findViewById(R.id.hr_recyclerview);
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        Query query = mfirebase.orderByChild(searchfield).startAt(searchkey).endAt(searchkey + "\uf8ff");



        FirebaseRecyclerAdapter<hostel_students,cviewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<hostel_students, cviewHolder>(
                hostel_students.class,
                R.layout.hostel_result_view,
                cviewHolder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(cviewHolder viewHolder, final hostel_students model, int position) {

                viewHolder.setDetails(getApplicationContext(),model.getName(),model.getRno(), model.getRoom_no());

                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent testleintent = new Intent(hostel_result_activity.this, full_hostel_details.class);
                        String val = model.getRno();
                       mbundle.putString("rollno",val);
                       testleintent.putExtras(mbundle);
                      startActivity(testleintent);
                    }
                });

            }
        };

        mRecyclerview.setAdapter(firebaseRecyclerAdapter);


    }

    public static class cviewHolder extends RecyclerView.ViewHolder{

        View mview;

        public StorageReference mdatabaseref;
        public Uri downloaduri;
        private ImageView mimageview;

        public cviewHolder(View itemView) {
            super(itemView);
            mview = itemView;

        }

        public void setDetails(Context ctx,String name, String rno ,String room_no){

            mdatabaseref = FirebaseStorage.getInstance().getReference();
            TextView mname = mview.findViewById(R.id.hsv_name);
            TextView mrno = mview.findViewById(R.id.hsv_rollno);
            TextView mnumber = mview.findViewById(R.id.hsv_room);



            try {
                mdatabaseref.child("profileimg/" + rno + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        downloaduri = uri;
                        setpic();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mimageview = mview.findViewById(R.id.hsv_image);
                        mimageview.setImageResource(R.drawable.man);
                    }
                });
            }catch (Exception e){
                Toast.makeText(ctx, "error", Toast.LENGTH_SHORT).show();
            }
            mname.setText(name);
            mrno.setText(rno);
            mnumber.setText(room_no);


        }

        private void setpic() {
            mimageview = mview.findViewById(R.id.hsv_image);
            Picasso.get().load(downloaduri).into(mimageview);
        }


    }
}
