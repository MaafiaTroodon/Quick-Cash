package com.example.quickcash.ui;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quickcash.R;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class Paypal extends AppCompatActivity {

    public EditText editText;

    private Button button;

    public TextView textView;

    private static final String TAG = Paypal.class.getName();

    public PayPalConfiguration payPalConfiguration;
    private Button backToListButton;


    private static final String PAYPAL_CLIENT_ID = "AYE-cXrpkIxybHaz2OJba69SZ55vSIS_vDnNI7J00CMT-Bgqyt0UHtvDtXTXX04R6puPP1m5rg_k7NVK";

    public ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypal);

        init();
        configPaypal();
        initActivityLauncher();
        setListeners();
    }

    private void init(){
        editText = findViewById(R.id.editTextNumber);
        button = findViewById(R.id.payButton);
        textView = findViewById(R.id.passOrFail);
        backToListButton = findViewById(R.id.backToListButton);
    }

    public String getAmount() {
        return editText.getText().toString().trim();
    }

    public void configPaypal(){
        payPalConfiguration = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_SANDBOX).clientId(PAYPAL_CLIENT_ID);
    }

    private void setListeners(){
        button.setOnClickListener(v -> processPayment());

        backToListButton.setOnClickListener(v -> {
            Intent intent = new Intent(Paypal.this, EmployeeListPage.class);
            startActivity(intent);
            finish();
        });
    }


    public void initActivityLauncher(){
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->{
            if (result.getResultCode() == RESULT_OK){
                final PaymentConfirmation paymentConfirmation = result.getData().getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (paymentConfirmation != null){
                    try {
                        String paymentDetails = paymentConfirmation.toJSONObject().toString(4);
                        JSONObject payObj = new JSONObject(paymentDetails);
                        String payID = payObj.getJSONObject("response").getString("id");
                        String state = payObj.getJSONObject("response").getString("state");
                        textView.setText("Payment Details: " + payID + "\n" + state);
                    }
                    catch (JSONException e){
                        Log.e("Error", "error");
                    }
                }
            }
        });
    }
    public void processPayment() {
        final String amount = editText.getText().toString().trim();

        // Clear any previous errors
        editText.setError(null);

        if (amount.isEmpty()) {
            editText.setError("Please enter an amount");
            editText.requestFocus();  // Focus on the field for better UX
            return;
        }

        BigDecimal paymentAmount;
        try {
            paymentAmount = new BigDecimal(amount);
        } catch (NumberFormatException e) {
            editText.setError("Invalid amount format");
            editText.requestFocus();
            return;
        }

        if (paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            editText.setError("Amount must be greater than 0");
            editText.requestFocus();
            return;
        }

        final PayPalPayment payPalPayment = new PayPalPayment(paymentAmount, "CAD", "Job done", PayPalPayment.PAYMENT_INTENT_SALE);

        final Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, payPalConfiguration);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);

        activityResultLauncher.launch(intent);
    }
}
