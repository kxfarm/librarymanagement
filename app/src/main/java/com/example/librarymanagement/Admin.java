package com.example.librarymanagement;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Admin extends AppCompatActivity {
    private static final String Tag="Admin_UI";
    private List<String> nomeConsulta = new ArrayList<String>();
    private ArrayAdapter<String> dataAdapter;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mdatabase;


    private Button insertbook,searchbook,updatebook,deletebook,searchborrow,returnb;
    private EditText bookid,bookname,bookid2,bookname2,txtcategory,txtlocation;
    private Spinner bookcategory,booklocation,bookcategory2,booklocation2;
    private TextView tv;
    private DrawerLayout drawer;
    private EditText sbib,sname,snic,sbd,srd,sphone;
    private TabHost tab;

    private String userid;
    private ImageView profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAD);
        setSupportActionBar(toolbar);

        drawer=(DrawerLayout) findViewById(R.id.drawer2);

        mAuth=FirebaseAuth.getInstance();

        updatebook=(Button) findViewById(R.id.btnupdatebook);
        deletebook=(Button) findViewById(R.id.btndeletebook);
        tv=(TextView) findViewById(R.id.tv1);

        tv.setVisibility(View.INVISIBLE);
        updatebook.setVisibility(View.INVISIBLE);
        deletebook.setVisibility(View.INVISIBLE);

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatebook.setVisibility(View.VISIBLE);
                deletebook.setVisibility(View.VISIBLE);
                txtlocation.setVisibility(View.INVISIBLE);
                txtcategory.setVisibility(View.INVISIBLE);
                tv.setVisibility(View.INVISIBLE);
            }
        });



        /*Including tab items*/
        TabHost tab=(TabHost) findViewById(R.id.tabhost2);
        tab.setup();

        TabHost.TabSpec spec1=tab.newTabSpec("");
        spec1.setIndicator("",getResources().getDrawable(R.drawable.ic_queue_black_24dp));
        spec1.setContent(R.id.Atab1);
        tab.addTab(spec1);


        spec1=tab.newTabSpec("");
        spec1.setIndicator("",getResources().getDrawable(R.drawable.ic_edit_black_24dp));
        spec1.setContent(R.id.Atab2);
        tab.addTab(spec1);


        spec1=tab.newTabSpec("");
        spec1.setIndicator("",getResources().getDrawable(R.drawable.ic_search_black_24dp));
        spec1.setContent(R.id.Atab3);
        tab.addTab(spec1);


        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(Tag, "onAuthStateChanged:signed_in:" + user.getUid());

                } else {

                    Log.d(Tag, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        //search borrowed books
        sbib=(EditText) findViewById(R.id.txtsbookid);
        sname=(EditText) findViewById(R.id.txtsname);
        sphone=(EditText) findViewById(R.id.txtsphone);
        snic=(EditText) findViewById(R.id.txtsnic);
        sbd=(EditText) findViewById(R.id.txtsBD);
        srd=(EditText) findViewById(R.id.txtsRD);

        searchborrow=(Button) findViewById(R.id.btnsearchborrow);

        searchborrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sbib.getText().toString().equals("")) {
                    sbib.setError("Enter Book ID");
                } else {

                    try {
                        String SBID = sbib.getText().toString().trim();
                        mdatabase = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference child = mdatabase.child("Adminlist");


                        child.child(SBID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    sbd.setText(dataSnapshot.child("Borrowed_Date").getValue().toString());
                                    srd.setText(dataSnapshot.child("Return_Date").getValue().toString());
                                    userid = dataSnapshot.child("User").getValue().toString();
                                    memberdetails();

                                }
                                else
                                {
                                    sbib.setError("This Book Not Borrowed");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(getApplicationContext(), "Book was not borrowed", Toast.LENGTH_SHORT).show();

                            }
                        });


                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Book Deleted Successfully", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        returnb=(Button) findViewById(R.id.btnsreturn);
        returnb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sbib.getText().toString().equals("")) {
                    sbib.setError("Enter Book ID");

                } else {


                    String SBID = sbib.getText().toString().trim();
                    mdatabase = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference child = mdatabase.child("Adminlist");
                    child.child(SBID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(getApplicationContext(), "Book Returned successfully", Toast.LENGTH_LONG).show();
                        }
                    });


                    mdatabase = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference child2 = mdatabase.child("BorrowedBooks").child(userid);
                    DatabaseReference DB = child2.child("Book");
                    DB.child(SBID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Book Returned successfully", Toast.LENGTH_LONG).show();
                        }
                    });

                    DatabaseReference child3 = mdatabase.child("Books");
                    DatabaseReference UserDB = child3.child(SBID);


                    UserDB.child("Status").setValue("IN");

                    sbib.setText("");
                    sname.setText("");
                    sphone.setText("");
                    snic.setText("");
                    sbd.setText("");
                    srd.setText("");
                }
            }
        });







// add book declare
        bookid=(EditText) findViewById(R.id.txtbookID);
        bookname=(EditText) findViewById(R.id.txtbookname);
        bookcategory=(Spinner) findViewById(R.id.spincategory);
        booklocation=(Spinner) findViewById(R.id.spinlocation);

        insertbook=(Button) findViewById(R.id.btnaddbook);
        insertbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_books();
            }
        });

        //search book
        bookid2=(EditText) findViewById(R.id.txtbookid2);
        bookname2=(EditText) findViewById(R.id.txtbookname2);
        txtcategory=(EditText) findViewById(R.id.txtcategory);
        txtlocation=(EditText) findViewById(R.id.txtlocation);

        bookcategory2=(Spinner) findViewById(R.id.spincategory2);
        booklocation2=(Spinner) findViewById(R.id.spinlocation2);

        searchbook=(Button) findViewById(R.id.btnsearchbook);
        searchbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_book();

            }
        });

        updatebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update_book();
            }
        });

        deletebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_book();
            }
        });

    }

    public void memberdetails()
    {

        DatabaseReference  mdata = FirebaseDatabase.getInstance().getReference().child("Users");
        mdata.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                sname.setText(dataSnapshot.child("name").getValue().toString());
                snic.setText(dataSnapshot.child("nicNumber").getValue().toString());
                sphone.setText(dataSnapshot.child("phone").getValue().toString());


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    public void add_books()
    {
        if(bookid.getText().toString().equals(""))
        {
            bookid.setError("Enter Book ID");
        }
        else if (bookname.getText().toString().equals(""))
        {
            bookname.setError("Enter Book Name");
        }else {
            String id = bookid.getText().toString().trim();
            String name = bookname.getText().toString().trim();
            String Category = bookcategory.getSelectedItem().toString().trim();
            String locat = booklocation.getSelectedItem().toString().trim();


            mdatabase = FirebaseDatabase.getInstance().getReference();
            DatabaseReference child = mdatabase.child("Books");
            DatabaseReference UserDB = child.child(id);


            UserDB.child("Book ID").setValue(id);
            UserDB.child("Book Name").setValue(name);
            UserDB.child("Book Category").setValue(Category);
            UserDB.child("Book Location").setValue(locat);
            UserDB.child("Status").setValue("IN");


            Toast.makeText(getApplicationContext(), "Book added successful", Toast.LENGTH_SHORT).show();
            bookname.setText("");
        }
    }



    public void search_book()
    {
        if(bookid2.getText().toString().equals(""))
        {
            bookid2.setError("Enter Book ID");
        }
        else {
            String id2 = bookid2.getText().toString().trim();
            String name2 = bookname2.getText().toString().trim();
            String cate2 = txtcategory.getText().toString().trim();
            String locate2 = txtlocation.getText().toString().trim();


            mdatabase = FirebaseDatabase.getInstance().getReference();
            DatabaseReference child = mdatabase.child("Books");
            DatabaseReference UserDB = child.child(id2);

            UserDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists()) {
                        bookname2.setText(dataSnapshot.child("Book Name").getValue().toString());
                        txtcategory.setText(dataSnapshot.child("Book Category").getValue().toString());
                        txtlocation.setText(dataSnapshot.child("Book Location").getValue().toString());


                        Toast.makeText(getApplicationContext(), "Book ID Found", Toast.LENGTH_SHORT).show();
                        tv.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        bookid2.setError("Book ID Not Found");
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "Book ID not Found", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    public void update_book()
    {
        if(bookid2.getText().toString().equals(""))
        {
            bookid2.setError("Enter Book ID");
        }else if(bookname2.getText().toString().equals("")) {
            bookname2.setError("Enter Book Name");

        }
        else {
            String id = bookid2.getText().toString().trim();
            String name = bookname2.getText().toString().trim();
            String Category = bookcategory2.getSelectedItem().toString().trim();
            String locat = booklocation2.getSelectedItem().toString().trim();


            mdatabase = FirebaseDatabase.getInstance().getReference();
            DatabaseReference child = mdatabase.child("Books");
            DatabaseReference UserDB = child.child(id);

            UserDB.child("Book Name").setValue(name);
            UserDB.child("Book Category").setValue(Category);
            UserDB.child("Book Location").setValue(locat);


            Toast.makeText(getApplicationContext(), "Book added successful", Toast.LENGTH_SHORT).show();

            bookid2.setText("");
            bookname2.setText("");
            txtcategory.setText("");
            txtlocation.setText("");
            tv.setVisibility(View.INVISIBLE);
            updatebook.setVisibility(View.INVISIBLE);
            deletebook.setVisibility(View.INVISIBLE);
            txtlocation.setVisibility(View.VISIBLE);
            txtcategory.setVisibility(View.VISIBLE);
        }
    }
    public void delete_book()
    {

        String id = bookid2.getText().toString().trim();


        mdatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference child = mdatabase.child("Books");
        DatabaseReference UserDB = child.child(id);
        UserDB.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Book Deleted successful", Toast.LENGTH_SHORT).show();
                bookid2.setText("");
                bookname2.setText("");
                txtcategory.setText("");
                txtlocation.setText("");
                tv.setVisibility(View.INVISIBLE);
                updatebook.setVisibility(View.INVISIBLE);
                deletebook.setVisibility(View.INVISIBLE);
                txtlocation.setVisibility(View.VISIBLE);
                txtcategory.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Book Deleted Failed", Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
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
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.logout2) {
            mAuth.signOut();
            Toast.makeText(getApplicationContext(),"Sign Out...",Toast.LENGTH_LONG).show();
            Intent login=new Intent(Admin.this,MainActivity.class);
            startActivity(login);
            return true;
        }





        return super.onOptionsItemSelected(item);
    }
}
