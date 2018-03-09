package com.tnt9.qrdatabase;


import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


public class RecentFragment extends Fragment {

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mConditionRef = mRootRef.child("PRODUKTY");

    public RecentFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_recent, container, false);

        Query query = mConditionRef.orderByChild("dateOrder");

        FirebaseRecyclerAdapter<Product, myViewHolder> adapter = new FirebaseRecyclerAdapter<Product, myViewHolder>(
                Product.class, R.layout.fragment_card, myViewHolder.class, query) {
            @Override
            protected void populateViewHolder(myViewHolder viewHolder, final Product model, int position) {
                viewHolder.setBrand(model.getBrandName(), model.getPrice(), model.getDate());

                viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        android.app.Fragment fragment = new ScanResultFragment();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.content_frame, fragment);
                        ft.addToBackStack(null);

                        Bundle bundle = new Bundle();
                        bundle.putCharSequence(ScanResultFragment.EXTRA_QR, model.getQrCode());
                        fragment.setArguments(bundle);

                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        ft.commit();
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        return recyclerView;
    }

    public static class myViewHolder extends RecyclerView.ViewHolder{

        private CardView cardView;

        public myViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
        }
        public void setBrand(String name, String price, String date){
            TextView brandTextView = cardView.findViewById(R.id.card_view_brand);
            brandTextView.setText(name);
            TextView priceTextView = cardView.findViewById(R.id.card_view_price);
            String displayPrice = price + " PLN";
            priceTextView.setText(displayPrice);
            TextView dateTextView = cardView.findViewById(R.id.card_view_date);
            dateTextView.setText(date);

        }
    }



}
