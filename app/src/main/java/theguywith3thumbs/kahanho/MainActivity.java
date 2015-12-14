package theguywith3thumbs.kahanho;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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

    private BackgroundService s;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Intent intent= new Intent(this, BackgroundService.class);
        //bindService(intent, mConnection,
          //      Context.BIND_AUTO_CREATE);

        ChekIfServiceRunning();
        CheckIfComingFromNumberPicker();
        SetupButtonClickHandler();
        SetupToggleButtonHandler();

    }

    private void CheckIfComingFromNumberPicker() {
        Intent intent = getIntent();
        if(intent !=null) {
            String trackerNumber = intent.getStringExtra(Constants.CallerNumber);
            Logger.i(Constants.AppNameForLogging, "coming fron number picker");
            if(trackerNumber !=null) {
                EditText inputNumberPattern = (EditText) findViewById(R.id.numberRegex);
                inputNumberPattern.setText(trackerNumber);
            }
        }
    }

    private void ChekIfServiceRunning() {
        if(BackgroundService.number != null)
        {
            Logger.i(Constants.AppNameForLogging, "Service is running");
            EditText inputNumberPattern = (EditText) findViewById(R.id.numberRegex);
            inputNumberPattern.setText(BackgroundService.number);
        }
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


    /*private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,
                                       IBinder binder) {
            BackgroundService.MyBinder b = (BackgroundService.MyBinder) binder;
            s = b.getService();
            String number = s.getTracker();
        *//*Intent intent = getIntent();
        if(intent !=null)
        {
            String trackerNumber = intent.getStringExtra(Constants.CallerNumber);
            EditText inputNumberPattern = (EditText) findViewById(R.id.numberRegex);
            inputNumberPattern.setText(trackerNumber);
        }*//*


        }

        public void onServiceDisconnected(ComponentName className) {
            s = null;
        }
    };*/
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
                    String number = inputNumberPattern.getText().toString();

                    Logger.i(Constants.AppNameForLogging, "Registering incoming call listener");
                    Context context = getApplicationContext();

                    Intent i= new Intent(context, BackgroundService.class);
                    i.putExtra(Constants.CallerNumber, number);
                    context.startService(i);

                    ShowToast(true,number);

                } else {
                    Logger.i(Constants.AppNameForLogging, "Deregistering incoming call listener");
                    Context context = getApplicationContext();
                    Intent i= new Intent(context, BackgroundService.class);
                    context.stopService(i);
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

    /*@Override
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
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.i(Constants.AppNameForLogging, "Destroying main activity");
    }
}
