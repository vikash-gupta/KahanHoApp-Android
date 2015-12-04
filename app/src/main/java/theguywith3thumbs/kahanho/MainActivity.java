package theguywith3thumbs.kahanho;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import android.util.Log;


public class MainActivity extends Activity {

    static InComingCallListener phoneListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        if(intent !=null)
        {
            String trackerNumber = intent.getStringExtra(Constants.CallerNumber);
            EditText inputNumberPattern = (EditText) findViewById(R.id.numberRegex);
            inputNumberPattern.setText(trackerNumber);
        }
        SetupButtonClickHandler();
        SetupToggleButtonHandler();

    }

    private void SetupButtonClickHandler() {

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), NumberPickerActivity.class);
                startActivity(i);
            }
        });
    }

/*    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }*/

    private void SetupToggleButtonHandler()
    {
        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EditText inputNumberPattern = (EditText) findViewById(R.id.numberRegex);


                /*SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("5554", null, "hello world", null, null);*/

                /*Uri uri = Uri.parse("smsto:5554");
                Intent it = new Intent(Intent.ACTION_SENDTO, uri);
                it.putExtra("sms_body", "The SMS text");
                startActivity(it);*/

                if (isChecked) {
                    // The toggle is enabled
                    Caller.number = inputNumberPattern.getText().toString();

                    if(phoneListener ==null) {
                        Log.i(Constants.AppNameForLogging, "Registering incoming call listener");
                        Context context = getApplicationContext();
                        TelephonyManager telephony = (TelephonyManager) context
                                .getSystemService(Context.TELEPHONY_SERVICE);
                        phoneListener = new InComingCallListener(context);
                        telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
                        ShowToast(true,Caller.number);
                    }

                } else {
                    Caller.number = null;

                    Log.i(Constants.AppNameForLogging, "Deregistering incoming call listener");
                    Context context = getApplicationContext();
                    TelephonyManager telephony = (TelephonyManager) context
                            .getSystemService(Context.TELEPHONY_SERVICE);
                    telephony.listen(phoneListener, PhoneStateListener.LISTEN_NONE);
                    phoneListener = null;
                    ShowToast(false,null);

                }
            }
        });
    }

    private void ShowToast(boolean registered, String number)
    {
        String msg = "Tracking on missed call ";
        if(registered) {
            msg += "from " + number + " on";
        }
        else
        {
            msg += "off";
        }

        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.show();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
