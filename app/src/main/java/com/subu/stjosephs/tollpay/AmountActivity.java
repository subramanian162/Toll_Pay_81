package com.subu.stjosephs.tollpay;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.subu.stjosephs.tollpay.config.Api;
import com.subu.stjosephs.tollpay.config.Checksum;
import com.subu.stjosephs.tollpay.config.Constants;
import com.subu.stjosephs.tollpay.config.Paytm;
import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class AmountActivity extends AppCompatActivity  implements PaytmPaymentTransactionCallback{


    protected EditText edit_amount_text;
    protected String safe_amount="0";
    private ProgressBar progressBar;
    protected TextView balance;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amount);
        edit_amount_text = findViewById(R.id.enter_amount_id);
        progressBar = findViewById(R.id.amount_progress_bar_id);
        balance = findViewById(R.id.amount_balance);
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Amount")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String amount = dataSnapshot.getValue(String.class);
                        balance.setText("Account Balance = Rs."+amount);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void payment(View view)
    {
        //calling the method generateCheckSum() which will generate the paytm checksum for payment
        progressBar.setVisibility(View.VISIBLE);
        generateCheckSum();
    }


    private void generateCheckSum() {

        //getting the tax amount first.
        String txnAmount = edit_amount_text.getText().toString().trim();
        safe_amount = txnAmount;

        //creating a retrofit object.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //creating the retrofit api service
        Api apiService = retrofit.create(Api.class);

        //creating paytm object
        //containing all the values required
        final Paytm paytm = new Paytm(
                Constants.M_ID,
                Constants.CHANNEL_ID,
                txnAmount,
                Constants.WEBSITE,
                Constants.CALLBACK_URL,
                Constants.INDUSTRY_TYPE_ID
        );

        //creating a call object from the apiService
        Call<Checksum> call = apiService.getChecksum(
                paytm.getmId(),
                paytm.getOrderId(),
                paytm.getCustId(),
                paytm.getChannelId(),
                paytm.getTxnAmount(),
                paytm.getWebsite(),
                paytm.getCallBackUrl(),
                paytm.getIndustryTypeId()
        );

        //making the call to generate checksum
        call.enqueue(new Callback<Checksum>() {
            @Override
            public void onResponse(Call<Checksum> call, Response<Checksum> response) {

                //once we get the checksum we will initiailize the payment.
                //the method is taking the checksum we got and the paytm object as the parameter
                initializePaytmPayment(response.body().getChecksumHash(), paytm);
            }

            @Override
            public void onFailure(Call<Checksum> call, Throwable t) {

            }
        });
    }

    private void initializePaytmPayment(String checksumHash, Paytm paytm) {

        //getting paytm service
        PaytmPGService Service = PaytmPGService.getStagingService();

        //creating a hashmap and adding all the values required
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("MID", Constants.M_ID);
        paramMap.put("ORDER_ID", paytm.getOrderId());
        paramMap.put("CUST_ID", paytm.getCustId());
        paramMap.put("CHANNEL_ID", paytm.getChannelId());
        paramMap.put("TXN_AMOUNT", paytm.getTxnAmount());
        paramMap.put("WEBSITE", paytm.getWebsite());
        paramMap.put("CALLBACK_URL", paytm.getCallBackUrl());
        paramMap.put("INDUSTRY_TYPE_ID", paytm.getIndustryTypeId());
        paramMap.put("CHECKSUMHASH", checksumHash);

        //creating a paytm order object using the hashmap
        PaytmOrder order = new PaytmOrder(paramMap);

        //intializing the paytm service
        Service.initialize(order, null);

        //finally starting the payment transaction
        Service.startPaymentTransaction(this, true, true,
                this);

    }

    //all these overriden method is to detect the payment result accordingly
    @Override
    public void onTransactionResponse(Bundle bundle) {
        edit_amount_text.setText("");
                FirebaseDatabase.getInstance().getReference("Users")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child("Amount").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String update_amount = Integer.toString(Integer.parseInt(dataSnapshot.getValue(String.class))
                                +Integer.parseInt(safe_amount));

                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("Amount")
                                .setValue(update_amount);
                        balance.setText("Account Balance = Rs."+update_amount);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(AmountActivity.this,"Transaction success",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void networkNotAvailable() {
        progressBar.setVisibility(View.GONE);
        edit_amount_text.setText("");
        Toast.makeText(this, "check your internet connection", Toast.LENGTH_LONG).show();
    }

    @Override
    public void clientAuthenticationFailed(String s) {
        progressBar.setVisibility(View.GONE);
        edit_amount_text.setText("");
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
        Toast.makeText(this, "This message is for the transaction failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void someUIErrorOccurred(String s)
    {
        progressBar.setVisibility(View.GONE);
        edit_amount_text.setText("");
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onErrorLoadingWebPage(int i, String s, String s1) {
        progressBar.setVisibility(View.GONE);
        edit_amount_text.setText("");
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressedCancelTransaction() {
        progressBar.setVisibility(View.GONE);
        edit_amount_text.setText("");
        Toast.makeText(this, "Back Pressed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        progressBar.setVisibility(View.GONE);
        edit_amount_text.setText("");
        Toast.makeText(this, s + bundle.toString(), Toast.LENGTH_LONG).show();
    }
}
