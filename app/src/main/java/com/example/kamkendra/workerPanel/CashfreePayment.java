package com.example.kamkendra.workerPanel;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cashfree.pg.api.CFPaymentGatewayService;
import com.cashfree.pg.core.api.CFSession;
import com.cashfree.pg.core.api.callback.CFCheckoutResponseCallback;
import com.cashfree.pg.core.api.exception.CFException;
import com.cashfree.pg.core.api.utils.CFErrorResponse;
import com.cashfree.pg.core.api.webcheckout.CFWebCheckoutPayment;
import com.cashfree.pg.core.api.webcheckout.CFWebCheckoutTheme;
import com.example.kamkendra.R;

public class CashfreePayment extends AppCompatActivity  implements CFCheckoutResponseCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cashfree_payment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });try {
            // Set Callback for payment result
            CFPaymentGatewayService.getInstance().setCheckoutCallback(this);
        } catch (CFException exception) {
            exception.printStackTrace();
        }

        AppCompatButton btnPay = findViewById(R.id.btn_pay);
        btnPay.setOnClickListener(v -> {
            makeDoPayment();
        });
    }

    @Override
    public void onPaymentVerify(String orderID) {
        /**
         * Verify Payment status from the backend using order Status API
         */
    }


    @Override
    public void onPaymentFailure(CFErrorResponse cfErrorResponse, String orderID) {
        /**
         * Payment Failure
         */
    }

    private void makeDoPayment() {
        try {
            CFSession cfSession = new CFSession.CFSessionBuilder()
                    .setEnvironment(CFSession.Environment.SANDBOX)
                    .setPaymentSessionID("session_TAOVOGe5zR_NOwfxc1Qir06_y54nPrRP4VKzzJT4lLJSeWPn6PMxWpgzwmChNC-J0dfiXpNEqnWGY9LdfkD8Rnj0xlIjVX6OqDZQT70h_c20htzw-tiwG3u-8wpaymentpayment")
                    .setOrderId("order_102975122uOcsaQ4rf2loT2Xrx8AGWaYUBB")

                    .build();

            CFWebCheckoutTheme cfTheme = new CFWebCheckoutTheme.CFWebCheckoutThemeBuilder()
                    .setNavigationBarBackgroundColor("#98a7e2")
                    .setNavigationBarTextColor("#ffffff")
                    .build();

            CFWebCheckoutPayment cfWebCheckoutPayment = new CFWebCheckoutPayment.CFWebCheckoutPaymentBuilder()
                    .setSession(cfSession)
                    .setCFWebCheckoutUITheme(cfTheme)
                    .build();

            CFPaymentGatewayService gatewayService = CFPaymentGatewayService.getInstance();
            gatewayService.doPayment(this, cfWebCheckoutPayment);
        } catch (CFException exception) {
            exception.printStackTrace();
        }
    }
}