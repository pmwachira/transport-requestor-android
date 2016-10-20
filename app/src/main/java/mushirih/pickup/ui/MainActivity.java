package mushirih.pickup.ui;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import mushirih.pickup.R;
import mushirih.pickup.RegisterActivity;
import mushirih.pickup.internal.MyApplication;
import mushirih.pickup.internal.User;
import mushirih.pickup.mapping.AppUtils;
import mushirih.pickup.mapping.MapsActivity;


public class MainActivity extends AppCompatActivity {


    @InjectView(R.id.et_useremail)
    EditText etUseremail;
    @InjectView(R.id.et_password)
    EditText etPassword;
    @InjectView(R.id.bt_go)
    Button btGo;
    @InjectView(R.id.cv)
    CardView cv;
    @InjectView(R.id.register)
    TextView register;
    String email,password;
    public String TAG="MainActivity.class";
    private TextInputLayout inputLayoutEmail, inputLayoutPass;
    Context context;
    ProgressDialog loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main0);
        context=MainActivity.this;
        ButterKnife.inject(this);
        inputLayoutEmail= (TextInputLayout) findViewById(R.id.input_layout_email);

        if (!AppUtils.isDataEnabled(MainActivity.this)){
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setMessage("Internet not enabled!");
                dialog.setPositiveButton("Open Internet settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_SETTINGS);
                        startActivity(myIntent);

                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub
                    }
                });
                dialog.show();
            }
    }

    @OnClick({R.id.bt_go, R.id.register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setExitTransition(null);
                    getWindow().setEnterTransition(null);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options =
                            ActivityOptions.makeSceneTransitionAnimation(this, register, register.getTransitionName());
                    startActivity(new Intent(this, RegisterActivity.class), options.toBundle());
                } else {
                    startActivity(new Intent(this, RegisterActivity.class));
                }
                break;
            case R.id.bt_go:
                email = etUseremail.getText().toString().trim();
                password = etPassword.getText().toString().trim();
                if(email.equals("1234")){
                      /*TODO REMOVE FOR DEBUG*/
                    //todo debug trapdoor
                    startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                    finish();
                }else{
                        if (email.isEmpty()) {
                            etUseremail.setError("Enter a valid email");
                            if (etUseremail.requestFocus()) {
                                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                            }
                        } else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                            etUseremail.setError("Enter a valid email");
                            if (etUseremail.requestFocus()) {
                                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                            }
                        }
                        else {
                            inputLayoutEmail.setErrorEnabled(false);
                            login();
                        }
                }

//                Explode explode = null;
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                    explode = new Explode();
//                    explode.setDuration(500);
//
//                    getWindow().setExitTransition(explode);
//                    getWindow().setEnterTransition(explode);
//                    ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(this);
//                    Intent i2 = new Intent(this,MapsActivity.class);
//                    startActivity(i2, oc2.toBundle());
//                    finish();
//                }

                    break;
                }

    }

    private void login() {
        loading = ProgressDialog.show(context, "Authenticating", "Please wait...",true,true);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                MyApplication.Online_Login, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (obj.getString("error").equals("false")) {
                        // user successfully logged in
                        loading.dismiss();
                        JSONObject userObj = obj.getJSONObject("user");
                        User user = new User(userObj.getString("requestor_id"),
                                userObj.getString("name"),
                                userObj.getString("email"));

                        // storing user in shared preferences
                        MyApplication.getInstance().getPrefManager().storeUser(user);
//                        Toast.makeText(getApplicationContext(), "Logged in as: " + userObj.getString("name")+"::"+
//                                userObj.getString("email"), Toast.LENGTH_LONG).show();
                        // start main activity
                        startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                        finish();

                    } else {
                        // login error - simply toast the message
                        loading.dismiss();
                        Toast.makeText(getApplicationContext(), "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                loading.dismiss();
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse+" and "+error.getMessage());
                Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }) {

            @Override

            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
//                params.put("name", name);
                params.put("email",email);
                params.put("password",password);

                Log.e(TAG, "params: " + params.toString());
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
