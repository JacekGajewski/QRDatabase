package com.tnt9.qrdatabase;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

public class EditProductFragment extends Fragment {

    private EditText qrCodeEditText;
    private EditText brandNameEditText;
    private EditText priceEditText;

    private String qrCode;
    private String currentDate;
    private boolean watchlist = false;

    private float oldPrice;
    private float newPrice = 0;

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mConditionRef = mRootRef.child("PRODUKTY");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_edit_product, container, false);

        qrCodeEditText = (EditText) relativeLayout.findViewById(R.id.edit_product_id);
        brandNameEditText = (EditText) relativeLayout.findViewById(R.id.edit_product_beand_name);
        priceEditText = (EditText) relativeLayout.findViewById(R.id.edit_product_price);
        EditText dateEditText = (EditText) relativeLayout.findViewById(R.id.edit_product_date);
        Button saveButton = (Button) relativeLayout.findViewById(R.id.edit_product_save_button);

        Calendar calendar = Calendar.getInstance();
        int calendarDay = calendar.get(Calendar.DAY_OF_MONTH);
        int calendarMonth = calendar.get(Calendar.MONTH) + 1;
        int calendarYear = calendar.get(Calendar.YEAR);

        currentDate = String.valueOf(calendarDay) +"/"+ String.valueOf(calendarMonth) +"/"+ String.valueOf(calendarYear);
        final long dateOrder = 9999999999999L - calendar.getTimeInMillis();

        Bundle bundle = this.getArguments();
        if (bundle != null){
            qrCode = bundle.getCharSequence(ScanResultFragment.EXTRA_QR, "101").toString();
            qrCodeEditText.setText(qrCode);

            brandNameEditText.setText(bundle.getCharSequence(ScanResultFragment.EXTRA_NAME, "Nazwa produktu"));
            oldPrice = Float.parseFloat(bundle.getCharSequence(ScanResultFragment.EXTRA_PRICE, "0.00").toString());
            priceEditText.setText(bundle.getCharSequence(ScanResultFragment.EXTRA_PRICE, "0"));
            dateEditText.setText(bundle.getCharSequence(ScanResultFragment.EXTRA_DATE, currentDate));
            watchlist = bundle.getBoolean(ScanResultFragment.EXTRA_WATCHLIST, false);
        }

        priceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null){
                    newPrice = Float.parseFloat(editable.toString().replace(',', '.'));
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!qrCode.equals(qrCodeEditText.getText().toString())){
                    mConditionRef.child(qrCode).removeValue();
                    qrCode = qrCodeEditText.getText().toString();
                }
                String qrCode = qrCodeEditText.getText().toString();

                if (newPrice == 0) newPrice = Float.parseFloat(priceEditText.getText().toString());

                Double priceChange = new Double(newPrice - oldPrice);
                //TODO: Make decimal change more readable.

                Locale currentLocale = Locale.getDefault();
                DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(currentLocale);
                otherSymbols.setDecimalSeparator('.');

                String formatString = "#,###,###,##0.00";

                DecimalFormat df = new DecimalFormat(formatString, otherSymbols);
                df.setMaximumFractionDigits(2);

                Product product = new Product(qrCode, brandNameEditText.getText().toString(), df.format(newPrice),
                                                currentDate, dateOrder, df.format(oldPrice),
                                                df.format(priceChange.floatValue()), watchlist);

                mConditionRef.child(qrCode).setValue(product);

                Fragment newFragment = new ScanResultFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, newFragment);
                ft.addToBackStack(null);

                Bundle bundle = new Bundle();
                bundle.putCharSequence(ScanResultFragment.EXTRA_QR, qrCode);
                newFragment.setArguments(bundle);

                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        });
        return relativeLayout;
    }
}
