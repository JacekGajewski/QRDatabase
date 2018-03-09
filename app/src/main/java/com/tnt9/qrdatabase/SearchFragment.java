package com.tnt9.qrdatabase;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SearchFragment extends Fragment {


    public SearchFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RelativeLayout searchLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_search, container, false);

        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mConditionRef = mRootRef.child("PRODUKTY");

        final EditText brandEditText = (EditText) searchLayout.findViewById(R.id.search_brand);
        final EditText idEditText = (EditText) searchLayout.findViewById(R.id.search_id);
        Button searchButton = (Button) searchLayout.findViewById(R.id.search_button);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String brandKey = brandEditText.getText().toString();
                String idKey = idEditText.getText().toString();
                if (brandKey.equals("") && idKey.equals("")){
                    Toast.makeText(getActivity(),"Wprowadź nazwę produktu lub kod", Toast.LENGTH_LONG).show();
                }
                else if (brandKey.equals("")){
                    commitFragment(new ScanResultFragment(), idKey, ScanResultFragment.EXTRA_QR);
                } else {
                    commitFragment(new ScanResultFragment(), brandKey, ScanResultFragment.EXTRA_NAME);
                }
            }
        });
        return searchLayout;
    }

    private void commitFragment(Fragment fragment, String scannedData, String message){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.addToBackStack(null);

        Bundle bundle = new Bundle();
        bundle.putCharSequence(message, scannedData);
        fragment.setArguments(bundle);

        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

}
