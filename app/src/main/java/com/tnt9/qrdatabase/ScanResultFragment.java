package com.tnt9.qrdatabase;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;


public class ScanResultFragment extends Fragment {


    public static final String EXTRA_QR = "EXTRA_QR";
    public static final String EXTRA_NAME = "EXTRA_NAME";
    public static final String EXTRA_PRICE = "EXTRA_PRICE";
    public static final String EXTRA_DATE = "EXTRA_DATE";
    public static final String EXTRA_WATCHLIST = "EXTRA_WATCHLIST";

    private TextView nameTextView;
    private TextView brandResult;
    private TextView priceResult;
    private TextView priceChangeResult;
    private TextView dateResult;
    private com.rey.material.widget.CheckBox watchlistResult;
    private String scannedQR;

    private String brandName;
    private String brandPrice;
    private String priceChange;
    private String date;
    private boolean watchlist;

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mConditionRef = mRootRef.child("PRODUKTY");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_scan_result, container, false);

        nameTextView = (TextView) relativeLayout.findViewById(R.id.product_id_scan_result);
        Button editButton = (Button) relativeLayout.findViewById(R.id.edit_button);
        brandResult = (TextView) relativeLayout.findViewById(R.id.product_name);
        priceResult = (TextView) relativeLayout.findViewById(R.id.product_price);
        priceChangeResult = (TextView) relativeLayout.findViewById(R.id.product_price_change);
        dateResult = (TextView) relativeLayout.findViewById(R.id.product_date);
        watchlistResult = (com.rey.material.widget.CheckBox) relativeLayout.findViewById(R.id.watchlist_checkbox);
        ImageView imageView = (ImageView) relativeLayout.findViewById(R.id.stats_imageView);

        Bundle bundle = this.getArguments();
        if (bundle != null){
            scannedQR = (String) bundle.getCharSequence(EXTRA_QR, "101");
            brandName = (String) bundle.getCharSequence(EXTRA_NAME, "Product name");
        }
        if (scannedQR.equals("101")){
            checkName();

        }else checkQR();
        brandResult.setText(brandName);
        priceResult.setText(String.valueOf("Price"));
        priceChangeResult.setText(String.valueOf(""));
        nameTextView.setText(scannedQR);
        dateResult.setText("Data");

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment newFragment = new EditProductFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, newFragment);

                ft.addToBackStack(null);
                Bundle bundle = new Bundle();
                bundle.putCharSequence(EXTRA_QR, scannedQR);
                bundle.putCharSequence(EXTRA_NAME, brandName);
                bundle.putCharSequence(EXTRA_PRICE, brandPrice);
                bundle.putCharSequence(EXTRA_DATE, date);
                bundle.putBoolean(EXTRA_WATCHLIST, watchlist);
                newFragment.setArguments(bundle);

                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        });

        watchlistResult.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                DatabaseReference mProductRef = mConditionRef.child(scannedQR);
                mProductRef.child("watchlist").setValue(watchlistResult.isChecked());
            }
        });

        return relativeLayout;
    }

    private void checkQR() {

        mConditionRef.orderByKey().equalTo(scannedQR).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                nameTextView.setText(scannedQR);

                if (dataSnapshot.child("brandName").getValue() == null) return;
                brandName = dataSnapshot.child("brandName").getValue().toString();

                brandResult.setText(brandName);
                brandPrice = dataSnapshot.child("price").getValue().toString();
                String displayPrice = brandPrice + " " + getResources().getString(R.string.currency);
                priceResult.setText(displayPrice);

                date = dataSnapshot.child("date").getValue().toString();
                dateResult.setText(date);

                watchlist = Boolean.valueOf(dataSnapshot.child("watchlist").getValue().toString());
                watchlistResult.setChecked(watchlist);

                priceChange = dataSnapshot.child("priceChange").getValue().toString();

                if (Float.valueOf(priceChange) > 0){
                    String placeHolder = "+" + priceChange;
                    priceChangeResult.setText(placeHolder);
                    priceChangeResult.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));

                } else if (Float.valueOf(priceChange) < 0){
                    String placeHolder = "-" + priceChange;
                    priceChangeResult.setText(placeHolder);
                    priceChangeResult.setTextColor(ContextCompat.getColor(getActivity(), R.color.green));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkName() {

        mConditionRef.orderByChild("brandName").equalTo(brandName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                scannedQR = dataSnapshot.child("qrCode").getValue().toString();
                nameTextView.setText(scannedQR);

                brandResult.setText(brandName);

                brandPrice = dataSnapshot.child("price").getValue().toString();
                String displayPrice = brandPrice + " " + getResources().getString(R.string.currency);
                priceResult.setText(displayPrice);

                date = dataSnapshot.child("date").getValue().toString();
                dateResult.setText(String.valueOf(date));

                watchlist = Boolean.valueOf(dataSnapshot.child("watchlist").getValue().toString());
                watchlistResult.setChecked(watchlist);

                priceChange = dataSnapshot.child("priceChange").getValue().toString();

                if (Float.valueOf(priceChange) > 0){

                    String placeHolder = "+" + priceChange;
                    priceChangeResult.setText(placeHolder);
                    priceChangeResult.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));

                } else if (Float.valueOf(priceChange) < 0){

                    String placeHolder = "-" + priceChange;
                    priceChangeResult.setText(placeHolder);
                    priceChangeResult.setTextColor(ContextCompat.getColor(getActivity(), R.color.green));
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
