package com.example.librarymanagement;

import android.app.DatePickerDialog;

import android.content.Intent;
import android.os.Bundle;


import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class UserUI extends AppCompatActivity {
     private static final String TAG = "User_UI";
     private FirebaseAuth mAuth;
     private FirebaseAuth.AuthStateListener mAuthListener;

     private FirebaseDatabase fdatabase;
     private DatabaseReference mdatabase;
     private FirebaseDatabase mstorage;
     private FirebaseUser user;


     private Button searchbook, getbook, done, respondUpdate, update;
     private EditText bookid, bookname, txtlocation, status;
     private TextView tv;
     private DrawerLayout  drawer;
     private TabHost tab;
     private EditText uname, uemail, uphone, unic, uname2, unic2, uphone2;


     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_user_ui);
         Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarUS);
         setSupportActionBar(toolbar);

         drawer = (DrawerLayout) findViewById(R.id.drawerlayout);

        //button bring to update profile detail tab
         respondUpdate=(Button) findViewById(R.id.btnupdateuser);

         respondUpdate.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 TabHost tab=(TabHost) findViewById(R.id.tabhost1);
                 tab.setCurrentTab(2);
             }
         });

         //button update profile detail
         update=(Button) findViewById(R.id.btnupdate2);
         update.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 update();
             }
         });


        uemail=(EditText) findViewById(R.id.txtmail1);
        uname=(EditText) findViewById(R.id.txtname1);
        uphone=(EditText)findViewById(R.id.txtphone1);
        unic=(EditText) findViewById(R.id.txtnic1);

        searchbook=(Button) findViewById(R.id.btnusearchbook);
        getbook=(Button) findViewById(R.id.btngetbook);
        tv=(TextView)findViewById(R.id.textView16);

        getbook.setVisibility(View.INVISIBLE);
        tv.setVisibility(View.INVISIBLE);

        bookid=(EditText) findViewById(R.id.txtubookid);
        bookname=(EditText) findViewById(R.id.txtubookname);
        txtlocation=(EditText) findViewById(R.id.txtubooklocation);
        status=(EditText) findViewById(R.id.txtstatus);


        //update user detail
        uname2=(EditText) findViewById(R.id.txtname3);
        unic2=(EditText) findViewById(R.id.txtnic3);
        uphone2=(EditText) findViewById(R.id.txtphone3);


        searchbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_book();
            }
        });

        getbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borrow_books();
            }
        });

        fdatabase= FirebaseDatabase.getInstance();
        mstorage=FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    // User is signed out

                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        TabHost tab=(TabHost) findViewById(R.id.tabhost1);
        tab.setup();

        TabHost.TabSpec spec1=tab.newTabSpec("");
        spec1.setIndicator("",getResources().getDrawable(R.drawable.ic_queue_black_24dp));
        spec1.setContent(R.id.tab1);
        tab.addTab(spec1);

        spec1=tab.newTabSpec("");
        spec1.setIndicator("",getResources().getDrawable(R.drawable.ic_person_black_24dp));
        spec1.setContent(R.id.tab2);
        tab.addTab(spec1);

        spec1=tab.newTabSpec("");
        spec1.setIndicator("",getResources().getDrawable(R.drawable.ic_edit_black_24dp));
        spec1.setContent(R.id.tab3);
        tab.addTab(spec1);

    }



    public String id,name,locate,stat;
    public void search_book()
    {
        if(bookid.getText().toString().equals(""))
        {
            bookid.setError("Enter Book ID");
        }
        else {
            id = bookid.getText().toString().trim();


            mdatabase = FirebaseDatabase.getInstance().getReference();
            DatabaseReference child = mdatabase.child("Books");
            DatabaseReference UserDB = child.child(id);

            UserDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        name = dataSnapshot.child("Book Name").getValue().toString();
                        locate = dataSnapshot.child("Book Location").getValue().toString();
                        stat = dataSnapshot.child("Status").getValue().toString();

                        bookname.setText(name);
                        txtlocation.setText(locate);
                        status.setText(stat);

                        bookname.setEnabled(false);
                        txtlocation.setEnabled(false);
                        status.setEnabled(false);


                        if (stat.equals("IN")) {
                            getbook.setVisibility(View.VISIBLE);
                            tv.setVisibility(View.INVISIBLE);
                        } else {
                            tv.setVisibility(View.VISIBLE);
                            getbook.setVisibility(View.INVISIBLE);
                        }
                        Toast.makeText(getApplicationContext(), "Book ID Found", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Book ID not Found", Toast.LENGTH_SHORT).show();

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "Book ID not Found", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    public void borrow_books()
    {

        mdatabase= FirebaseDatabase.getInstance().getReference();
        DatabaseReference child=mdatabase.child("BorrowedBooks");
        DatabaseReference UserDB=child.child(user.getUid()).child("Book").child(id);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df1 = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate1 = df1.format(c.getTime());
        Calendar c2 = Calendar.getInstance();
        c2.add(Calendar.MONTH, 1);
        String retur=df1.format(c2.getTime());
        Calendar c3 = Calendar.getInstance();
        c3.add(Calendar.DATE, 27);
        String notify=df1.format(c3.getTime());


        UserDB.child("Book_ID").setValue(id);
        UserDB.child("Book_Name").setValue(name);
        UserDB.child("Book_Location").setValue(locate);
        UserDB.child("Status").setValue("OUT");
        UserDB.child("Borrowed_Date").setValue(formattedDate1);
        UserDB.child("Return_Date").setValue(retur);
        UserDB.child("Notify_Date").setValue(notify);
        UserDB.child("User").setValue(user.getUid());

        DatabaseReference child1=mdatabase.child("Adminlist");
        DatabaseReference UserDB1=child1.child(id);
        UserDB1.child("Book_ID").setValue(id);
        UserDB1.child("Book_Name").setValue(name);
        UserDB1.child("Book_Location").setValue(locate);
        UserDB1.child("Borrowed_Date").setValue(formattedDate1);
        UserDB1.child("Return_Date").setValue(retur);
        UserDB1.child("Notify_Date").setValue(notify);
        UserDB1.child("User").setValue(user.getUid());

        update_books();
        Toast.makeText(getApplicationContext(),"Book added successful",Toast.LENGTH_SHORT).show();
        bookname.setText("");
    }

    public void update_books()
    {
        mdatabase= FirebaseDatabase.getInstance().getReference();
        DatabaseReference child=mdatabase.child("Books");
        DatabaseReference UserDB=child.child(id);


        UserDB.child("Status").setValue("OUT");


        Toast.makeText(getApplicationContext(),"Book added successful",Toast.LENGTH_SHORT).show();
        bookname.setText("");
    }

    public void DataProfile()
    {
        DatabaseReference  mdata = FirebaseDatabase.getInstance().getReference().child("Users");
        mdata.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                uname.setText(dataSnapshot.child("name").getValue().toString());
                uemail.setText(user.getEmail().toString());
                unic.setText(dataSnapshot.child("nicNumber").getValue().toString());
                uphone.setText(dataSnapshot.child("phone").getValue().toString());

                uname2.setText(dataSnapshot.child("name").getValue().toString());
                unic2.setText(dataSnapshot.child("nicNumber").getValue().toString());
                uphone2.setText(dataSnapshot.child("phone").getValue().toString());

                uname.setEnabled(false);
                uemail.setEnabled(false);
                uphone.setEnabled(false);
                unic.setEnabled(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void update() {

        if(uname2.getText().toString().equals(""))
        {
            uname2.setError("Enter Your Name");
        }else if (uphone2.getText().toString().equals(""))
        {
            uphone2.setError("Enter Your Phone Number");
        }else if (unic2.getText().toString().equals(""))
        {
            unic2.setError("Enter NIC Number");
        }else {

            FirebaseUser user = mAuth.getCurrentUser();
            mdatabase = FirebaseDatabase.getInstance().getReference().child("Users");
            DatabaseReference UserDB = mdatabase.child(user.getUid());

            UserDB.child("name").setValue(uname2.getText().toString().trim());
            UserDB.child("phone").setValue(uphone2.getText().toString().trim());
            UserDB.child("nicNumber").setValue(unic2.getText().toString().trim());

            Toast.makeText(UserUI.this, "Details updated successfully", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        DataProfile();
    }

    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();
        if (id == R.id.logout2) {
            mAuth.signOut();
            Toast.makeText(getApplicationContext(),"Sign Out...",Toast.LENGTH_LONG).show();
            Intent login=new Intent(UserUI.this,MainActivity.class);
            startActivity(login);
            return true;
        }




        return super.onOptionsItemSelected(item);

        }}
