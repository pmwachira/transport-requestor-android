package mushirih.pickup;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gcm.GCMRegistrar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import mushirih.pickup.cm.ServerUtilities;
import mushirih.pickup.cm.WakeLocker;
import mushirih.pickup.internal.MyApplication;
import mushirih.pickup.internal.User;
import mushirih.pickup.mapping.MapsActivity;

import static mushirih.pickup.cm.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static mushirih.pickup.cm.CommonUtilities.EXTRA_MESSAGE;
import static mushirih.pickup.cm.CommonUtilities.SENDER_ID;

public class RegisterActivity extends AppCompatActivity {

    @InjectView(R.id.fab)
    FloatingActionButton fab;
    @InjectView(R.id.cv_add)
    CardView cvAdd;
    @InjectView(R.id.et_username)
    EditText etusername;
    @InjectView(R.id.et_useremail)
    EditText etuseremail;
    @InjectView(R.id.et_userid)
    EditText etuser_id;
    @InjectView(R.id.et_password)
    EditText etpassword1;
    @InjectView(R.id.et_repeatpassword)
    EditText etrepeat_password;
    @InjectView(R.id.register)
    Button register;
    @InjectView(R.id.tipass)
    TextInputLayout tipass;
    @InjectView(R.id.tiemail)
    TextInputLayout tiemail;
    String user_name,user_email,user_id,user_password,user_repeat_password,user_gcm;
    Context mContext;
    AsyncTask<Void, Void, Void> mRegisterTask;
    ProgressDialog loading;
    String TAG="REGISTER_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.inject(this);
        mContext=this;
        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(this);

        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(this);

        registerReceiver(mHandleMessageReceiver, new IntentFilter(
                DISPLAY_MESSAGE_ACTION));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ShowEnterAnimation();
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateRevealClose();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            user_name=etusername.getText().toString().trim();
                user_email=etuseremail.getText().toString().trim();
                user_id=etuser_id.getText().toString().trim();
                user_password=etpassword1.getText().toString().trim();
                user_repeat_password=etrepeat_password.getText().toString().trim();
                if(!user_password.equals(user_repeat_password)){
                        etpassword1.setError("Passwords do not match");
                        requestFocus(etpassword1);

                    } else {
                        tipass.setErrorEnabled(false);
                        registerUser(user_name, user_id, user_email, user_password);
                    }
            }
        });
    }

    private void registerUser(final String username, final String userid, final String useremail, final String password1) {

        loading = ProgressDialog.show(mContext, "Authenticating", "Please wait...",true,false);
        if (!validateEmail(useremail)) {
            return;
        }
        StringRequest strReqXS = new StringRequest(Request.Method.POST,
                MyApplication.ADD_USER, new Response.Listener<String>() {

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
                        user_email=userObj.getString("email");
                        user_name= userObj.getString("name");
                        user_id=userObj.getString("user_id");
                        User user = new User(user_email,
                                user_name,
                                user_id);

                        // storing user in shared preferences
                        MyApplication.getInstance().getPrefManager().storeUser(user);
                        registerCM(user_name,user_email);
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
                params.put("requestor_id",userid);
                params.put("email",useremail);
                params.put("password",password1);
                params.put("phone_number","0712613052");
                params.put("name",username);

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
        MyApplication.getInstance().addToRequestQueue(strReqXS);
    }

    private boolean validateEmail(String useremail) {

            if (useremail.isEmpty() || !isValidEmail(useremail)) {
                etuseremail.setError("Enter a valid email");
                requestFocus (etuseremail);
                return false;
            } else {
                tiemail.setErrorEnabled(false);
                return true;
            }

    }

    private static boolean isValidEmail(String email) {
        if(TextUtils.isEmpty(email) && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            return false;
        }else {
            return true;
        }

    }


    private void registerCM(final String user_name, final String DRIVER_EMAIL) {

        // Get GCM registration id
        final String regId = GCMRegistrar.getRegistrationId(mContext);
        // Check if regid already presents
        if (regId.equals("")) {
            // Registration is not present, register now with GCM
            GCMRegistrar.register(mContext,SENDER_ID);
            loading.dismiss();
        } else {
            // Device is already registered on GCM
            if (GCMRegistrar.isRegisteredOnServer(mContext)) {
                ServerUtilities.register(mContext, user_name, DRIVER_EMAIL, regId);
                loading.dismiss();
                Toast.makeText(getApplicationContext(), "Already registered with GCM", Toast.LENGTH_LONG).show();
            } else {

                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.
                final Context context = mContext;
                mRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        // Register on our server
                        // On server creates a new user
                        ServerUtilities.register(mContext, user_name, DRIVER_EMAIL, regId);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                        loading.dismiss();
                    }

                };
                mRegisterTask.execute(null, null, null);
            }
        }
    }
    /**
     * Receiving push messages
     * */
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
            // Waking up mobile if it is sleeping
            WakeLocker.acquire(getApplicationContext());

            /**
             * Take appropriate action on this message
             * depending upon your app requirement
             * For now i am just displaying it on the screen
             * */

            // Showing received message
            Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();

            // Releasing wake lock
            WakeLocker.release();
        }
    };
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    private void ShowEnterAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
        getWindow().setSharedElementEnterTransition(transition);

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                cvAdd.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }


        });
    }

    public void animateRevealShow() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth()/2,0, fab.getWidth() / 2, cvAdd.getHeight());
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                cvAdd.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    public void animateRevealClose() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd,cvAdd.getWidth()/2,0, cvAdd.getHeight(), fab.getWidth() / 2);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cvAdd.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                fab.setImageResource(R.drawable.plus);
                RegisterActivity.super.onBackPressed();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }
    @Override
    public void onBackPressed() {
        animateRevealClose();
    }
    @Override
    protected void onDestroy() {
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        try {
            unregisterReceiver(mHandleMessageReceiver);
            GCMRegistrar.onDestroy(this);
        } catch (Exception e) {
            Log.e("UnRegister Receiver Error", "> " + e.getMessage());
        }
        super.onDestroy();
    }
}