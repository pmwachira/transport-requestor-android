package mushirih.pickup.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.http.util.EncodingUtils;

import mushirih.pickup.R;

/**
 * Created by p-tah on 07/12/2016.
 */
public class Payments  extends ActionBarActivity {
         WebView webView;
        ProgressDialog loading;
    String cost="033";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            loading = ProgressDialog.show(this,null, "Fetching payment methods",true,false);
            setContentView(R.layout.payments);
            webView = (WebView) findViewById(R.id.webview);
            if (null != getIntent() && null != getIntent().getStringExtra("cost")) {
                cost = getIntent().getStringExtra("cost");
            }
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);


            String postData = "amount="+cost+"&type=MERCHANT&description=lol&reference=101&first_name=peter&" +
                    "last_name=Mush&email=pmwachira@gmail.com";

            webView.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url){
                    return false;
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    loading.dismiss();
                }
            });
            String url = "http://noshybakery.co.ke/PICKUP/include/payments/pesapal-iframe.php";
            webView.postUrl(url, EncodingUtils.getBytes(postData, "BASE64"));
        }

   }
