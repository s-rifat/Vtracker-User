package com.example.vtracker2;




import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.vtracker2.Interface.IFirebaseLoadDone;
import com.example.vtracker2.Interface.IRecyclerItemClickListener;
import com.example.vtracker2.Model.MyResponse;
import com.example.vtracker2.Model.Request;
import com.example.vtracker2.Model.User;
import com.example.vtracker2.Remote.IFCMService;
import com.example.vtracker2.Utils.Common;
import com.example.vtracker2.ViewHolder.UserViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class AllPeopleActivity extends AppCompatActivity implements IFirebaseLoadDone {


    FirebaseRecyclerAdapter<User, UserViewHolder> adapter, searchAdapter;
    RecyclerView recycler_all_user;
    IFirebaseLoadDone firebaseLoadDone;

    MaterialSearchBar searchBar;
    List<String> suggestList = new ArrayList<>();

    IFCMService ifcmService;
    CompositeDisposable compositeDisposable = new CompositeDisposable();



    

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_people);
        //init API
        ifcmService = Common.getFCMService();
        //init view

        searchBar = (MaterialSearchBar)findViewById(R.id.material_search_bar);
        ///




        ///
        searchBar.setCardViewElevation(10);
        searchBar.addTextChangeListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String>suggest = new ArrayList<>();
                for(String search: suggestList)
                {
                    if(search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                searchBar.setLastSuggestions(suggest);

            }


            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if(!enabled)
                {
                    if(adapter!= null)
                    {
                        recycler_all_user.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text.toString());
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        recycler_all_user =(RecyclerView) findViewById(R.id.recycler_all_people);
        recycler_all_user.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_all_user.setLayoutManager(layoutManager);
        recycler_all_user.addItemDecoration(new DividerItemDecoration(this,((LinearLayoutManager) layoutManager).getOrientation()));

        firebaseLoadDone = this;

        loadUserList();
        loadSearchData();

      //  loadDriverList();
      //  loadDriverSearchData();





    }



    private void loadSearchData() {
        final List<String> lstUserEmail = new ArrayList<>();
        DatabaseReference userList = FirebaseDatabase.getInstance()
                .getReference(Common.USER_INFORMATION);

        if(Common.VEHICLE_LIST)
        {
            userList = FirebaseDatabase.getInstance()
                    .getReference(Common.Driver_INFORMATION);
        }

        userList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapShot:dataSnapshot.getChildren())
                {
                    User user = userSnapShot.getValue(User.class);
                   // lstUserEmail.add(user.getEmail());
                    lstUserEmail.add(user.getRout());
                }
                firebaseLoadDone.onFirebaseLoadUserNameDone(lstUserEmail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                firebaseLoadDone.onFirebaseLoadFailed(databaseError.getMessage());
            }
        });
    }

   /* private void loadDriverSearchData() {
        final List<String> lstUserEmail = new ArrayList<>();
        DatabaseReference userList = FirebaseDatabase.getInstance()
                .getReference(Common.Driver_INFORMATION);

        userList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapShot:dataSnapshot.getChildren())
                {
                    User user = userSnapShot.getValue(User.class);
                    lstUserEmail.add(user.getEmail());
                }
                firebaseLoadDone.onFirebaseLoadUserNameDone(lstUserEmail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                firebaseLoadDone.onFirebaseLoadFailed(databaseError.getMessage());
            }
        });
    }*/


    private void loadUserList() {

        Query query = FirebaseDatabase.getInstance().getReference().child(Common.USER_INFORMATION);
        if(Common.VEHICLE_LIST)
        {
            query = FirebaseDatabase.getInstance().getReference().child(Common.Driver_INFORMATION);
        }
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query,User.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull final User model) {
                if(model.getEmail().equals(Common.loggeduser.getEmail()))
                {
                    holder.txt_user_email.setText(new StringBuilder(model.getRout()).append(" (me)"));
                    holder.txt_user_email.setTypeface(holder.txt_user_email.getTypeface(), Typeface.ITALIC);
                }
                else
                {
                    holder.txt_user_email.setText(new StringBuilder(model.getRout()));
                }
                //event
                holder.setiRecyclerItemClickListener(new IRecyclerItemClickListener() {
                    @Override
                    public void onItemClickListener(View view, int position) {
                        //implement late
                        showDialogueRequest(model);
                    }


                });
            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                if(Common.VEHICLE_LIST)
                {
                    View itemView = LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.layout_bus,viewGroup,false);
                    return new UserViewHolder(itemView);
                }
                else
                {
                    View itemView = LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.layout_user,viewGroup,false);
                    return new UserViewHolder(itemView);
                }


            }
        };

        //dont forget this line if you dont want all your blank line in load user
        adapter.startListening();
        recycler_all_user.setAdapter(adapter);
    }

   /* private void loadDriverList() {

        Query query = FirebaseDatabase.getInstance().getReference().child(Common.Driver_INFORMATION);
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query,User.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull final User model) {
                if(model.getEmail().equals(Common.loggeduser.getEmail()))
                {
                    holder.txt_user_email.setText(new StringBuilder(model.getEmail()).append(" (me)"));
                    holder.txt_user_email.setTypeface(holder.txt_user_email.getTypeface(), Typeface.ITALIC);
                }
                else
                {
                    holder.txt_user_email.setText(new StringBuilder(model.getEmail()));
                }
                //event
                holder.setiRecyclerItemClickListener(new IRecyclerItemClickListener() {
                    @Override
                    public void onItemClickListener(View view, int position) {
                        //implement late
                        showDriverDialogueRequest(model);
                    }
                });
            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.layout_user,viewGroup,false);
                return new UserViewHolder(itemView);
            }
        };

        //dont forget this line if you dont want all your blank line in load user
        adapter.startListening();
        recycler_all_user.setAdapter(adapter);
    }*/

    private void showDialogueRequest(final User model) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this,R.style.MyRequestDialog);
       if(Common.VEHICLE_LIST)
        {
            alertDialog.setTitle("Add to list");
            alertDialog.setMessage("Do you want to add "+model.getBusName()+" to your bus and friend list?");
            alertDialog.setIcon(R.drawable.ic_baseline_directions_bus_24);
        }
       else
       {
           alertDialog.setTitle("Request Friend");
           alertDialog.setMessage("Do you want to send friend request to "+model.getEmail());
           alertDialog.setIcon(R.drawable.ic_account_circle_black_24dp);
       }





        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        String s = "SEND";
        if(Common.VEHICLE_LIST)
            s = "ADD";
        alertDialog.setPositiveButton(s, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Add to accept list
                final DatabaseReference acceptList = FirebaseDatabase.getInstance()
                        .getReference(Common.USER_INFORMATION)
                        .child(Common.loggeduser.getUid())
                        .child(Common.ACCEPT_LIST);

                final DatabaseReference acceptList2 = FirebaseDatabase.getInstance()
                        .getReference(Common.Driver_INFORMATION)
                        .child(model.getUid())
                        .child(Common.ACCEPT_LIST);

                final DatabaseReference acceptList3 = FirebaseDatabase.getInstance()
                        .getReference(Common.USER_INFORMATION)
                        .child(model.getUid())
                        .child(Common.ACCEPT_LIST);

                acceptList.orderByKey().equalTo(model.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getValue()==null)//if not friend before
                                {

                                    //acceptList.setValue(model);
                                 //   acceptList2.setValue(Common.loggeduser);


                                   if(Common.VEHICLE_LIST)
                                   {
                                        acceptList.child(model.getUid()).setValue(model);
                                       Toast.makeText(AllPeopleActivity.this, model.getBusName() + " is Added to your bus and friend list", Toast.LENGTH_LONG).show();

                                       // acceptList2.child(Common.loggeduser.getUid()).setValue(Common.loggeduser);
                                    }
                                    else
                                    {
                                        sendFriendRequest(model);
                                       // acceptList.child(model.getUid()).setValue(model);
                                        //acceptList3.child(Common.loggeduser.getUid()).setValue(Common.loggeduser);
                                    }

                                }

                                else
                                {
                                    if(Common.VEHICLE_LIST)
                                    {
                                        Toast.makeText(AllPeopleActivity.this,model.getBusName()+ " is already added",Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                        Toast.makeText(AllPeopleActivity.this,"You and "+model.getEmail()+ " already are friends",Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


            }
        });
        alertDialog.show();// don't forget it!
    }

   /* private void showDriverDialogueRequest(final User model) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this,R.style.MyRequestDialog);
        alertDialog.setTitle("Request Friend");
        alertDialog.setMessage("Do you want to send friend request to "+model.getEmail());
        alertDialog.setIcon(R.drawable.ic_account_circle_black_24dp);

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.setPositiveButton("SEND", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Add to accept list
                final DatabaseReference acceptList = FirebaseDatabase.getInstance()
                        .getReference(Common.USER_INFORMATION)
                        .child(Common.loggeduser.getUid())
                        .child(Common.ACCEPT_LIST);

                final DatabaseReference acceptList2 = FirebaseDatabase.getInstance()
                        .getReference(Common.USER_INFORMATION)
                        .child(Common.loggeduser.getUid())
                        .child(Common.ACCEPT_LIST);


                acceptList.orderByKey().equalTo(model.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getValue()==null)//if not friend before
                                {
                                    acceptList.setValue(model);
                                    acceptList2.setValue(Common.loggeduser);
                                }

                                else
                                    Toast.makeText(AllPeopleActivity.this,"You and "+model.getEmail()+ " already are friends",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


            }
        });
        alertDialog.show();// don't forget it!
    }*/

    private void sendFriendRequest(final User model) {
        //get token to send
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference(Common.TOKENS);

        tokens.orderByKey().equalTo(model.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() == null)
                            Toast.makeText(AllPeopleActivity.this,"Token Error",Toast.LENGTH_SHORT).show();
                        else
                        {
                            //create request
                            Request request = new Request();
                            //create data
                            Map<String,String> dataSend = new HashMap<>();
                            dataSend.put(Common.FROM_UID,Common.loggeduser.getUid());
                            dataSend.put(Common.FROM_NAME,Common.loggeduser.getEmail());

                            dataSend.put(Common.TO_UID,model.getUid());
                            dataSend.put(Common.TO_NAME,model.getEmail());

                            request.setTo(dataSnapshot.child(model.getUid()).getValue(String.class));
                            request.setData(dataSend);
                            //send

                            compositeDisposable.add(ifcmService.sendFriendRequestToUser(request)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<MyResponse>() {
                                        @Override
                                        public void accept(MyResponse myResponse) throws Exception {
                                            if (myResponse.success == 1)
                                                Toast.makeText(AllPeopleActivity.this, "Request Sent!", Toast.LENGTH_LONG).show();
                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {
                                            Toast.makeText(AllPeopleActivity.this,throwable.getMessage(),Toast.LENGTH_LONG).show();
                                        }
                                    }));

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    //cntrl+o


    @Override
    protected void onStop() {
        if(adapter!=null)
            adapter.stopListening();
        if(searchAdapter!=null)
            searchAdapter.stopListening();

        compositeDisposable.clear();
        super.onStop();
    }
    protected void onResume() {
        super.onResume();
        if(adapter!=null)
        {
            adapter.startListening();
        }
        if(searchAdapter!=null)
            searchAdapter.startListening();
    }





    private void startSearch(String text_search) {

        Query query = FirebaseDatabase.getInstance()
                .getReference(Common.USER_INFORMATION)
                .orderByChild("name")
                .startAt(text_search);
        if(Common.VEHICLE_LIST)
        {
             query = FirebaseDatabase.getInstance()
                    .getReference(Common.Driver_INFORMATION)
                    .orderByChild("name")
                    .startAt(text_search);
        }
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query,User.class)
                .build();
        searchAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull final User model) {
                if(model.getEmail().equals(Common.loggeduser.getEmail()))
                {
                    holder.txt_user_email.setText(new StringBuilder(model.getEmail()).append(" (me)"));
                    holder.txt_user_email.setTypeface(holder.txt_user_email.getTypeface(), Typeface.ITALIC);
                }
                else
                {
                    holder.txt_user_email.setText(new StringBuilder(model.getEmail()));
                }
                //event
                holder.setiRecyclerItemClickListener(new IRecyclerItemClickListener () {
                    @Override
                    public void onItemClickListener(View view, int position) {
                        showDialogueRequest(model);
                    }


                });
            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.layout_user,viewGroup,false);
                return new UserViewHolder(itemView);
            }
        };






            //dont forget this line if you dont want all your blank line in load user
        searchAdapter.startListening();
        recycler_all_user.setAdapter(searchAdapter);

    }

    @Override
    public void onFirebaseLoadUserNameDone(List<String> lstEmail) {
        searchBar.setLastSuggestions(lstEmail);
    }

    @Override
    public void onFirebaseLoadFailed(String message) {
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}