package com.example.gamlab;

import static com.example.gamlab.MainActivity.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.text.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;


public class ReservationFragment extends Fragment{
    private RecyclerView rvTimeSlot;
    private FirebaseFirestore fb;
    private FirestoreRecyclerAdapter adapter;
    private FirebaseAuth mAuth;
    private String formatted;
    private String setTime;
    private String selectedDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservation, container, false);
        setContentView(R.layout.fragment_reservation);

        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, 0);

        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 2);

        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(view, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(7)
                .build();
        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                Log.e("TAG", "CURRENT DATE IS " + date.DAY_OF_MONTH + date.MONTH);
                Calendar cal = horizontalCalendar.getDateAt(position);

                //formatting date
//                cal.add(Calendar.DATE,1);
                SimpleDateFormat format1 = new SimpleDateFormat("MM/dd/yyyy");
                formatted = format1.format(cal.getTime());
                SimpleDateFormat format2 = new SimpleDateFormat("MMddyyyy");
                selectedDate = format2.format(cal.getTime());
                Toast.makeText(getContext(), "Selected date has changed", Toast.LENGTH_SHORT).show();
                Log.d("Selected Date", formatted);
            }
        });

        //btn to pick time
        Button btnDate = (Button) view.findViewById(R.id.btnDate);

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getting users information
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();

                //adding date to the collection
                Map<String, Object> dates = new HashMap<>();
                dates.put("date", formatted);
                Log.d(TAG, "get uID:" + currentUser.getUid());
                fb.collection("users/" + currentUser.getUid() + "/" + selectedDate)
                        .add(dates)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                Toast.makeText(getContext(), formatted, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });
            }
        });


        //getting information from firestore
        fb = FirebaseFirestore.getInstance();
        rvTimeSlot = view.findViewById(R.id.rvTimeSlot);


        Query query = fb.getInstance()
                .collection("TimePc1").orderBy("order");
        FirestoreRecyclerOptions<TimeSlots> options = new FirestoreRecyclerOptions.Builder<TimeSlots>()
                .setQuery(query, TimeSlots.class)
                .build();

        //using adapter to connect the time data with recyclerView
        adapter = new FirestoreRecyclerAdapter<TimeSlots, TimeSlotsViewHolder>(options) {
            @NonNull
            @Override
            public TimeSlotsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slot, parent, false);
                return new TimeSlotsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull TimeSlotsViewHolder holder, int position, @NonNull TimeSlots model) {
                holder.tvItem.setText(model.getTime());
            }
        };

        rvTimeSlot.setHasFixedSize(true);
        rvTimeSlot.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvTimeSlot.setAdapter(adapter);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void setContentView(int fragment_reservation) {
    }




    //getting data to display in the recycler view
    class TimeSlotsViewHolder extends RecyclerView.ViewHolder {

        private TextView tvItem;

        public TimeSlotsViewHolder(@NonNull View itemView) {
            super(itemView);

            tvItem = itemView.findViewById(R.id.tvItem);

            //action when the user select the item
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //getting users information
                    mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = mAuth.getCurrentUser();

                    //getting time information
                    int position = getAbsoluteAdapterPosition();
                    switch(position){
                        case 0:
                            setTime = "12:00pm~2:00pm";
                            break;
                        case 1:
                            setTime = "2:00pm~4:00pm";
                            break;
                        case 2:
                            setTime = "4:00pm~6:00pm";
                            break;
                        case 3:
                            setTime = "6:00pm~8:00pm";
                            break;
                        case 4:
                            setTime = "8:00pm~10:00pm";
                            break;
                        case 5:
                            setTime = "10:00pm~0:00am";
                            break;
                    }

                    //adding date to the the user collection
                    Map<String, Object> times = new HashMap<>();
                    times.put("time", setTime);
                    fb.collection("users/" + currentUser.getUid() + "/" + selectedDate)
                            .add(times)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                    Toast.makeText(getContext(), "time picked", Toast.LENGTH_SHORT).show();

                                    //goes to profile fragment
                                    FragmentTransaction fr = getFragmentManager().beginTransaction();
                                    fr.replace(R.id.fragment_container, new ProfileFragment());
                                    fr.commit();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                }
                            });



                }

            });

        }



    }






    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }



}



