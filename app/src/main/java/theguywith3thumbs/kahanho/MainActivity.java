package theguywith3thumbs.kahanho;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


public class MainActivity extends Activity {

    private Tracker mTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        SendScreenHitAnalytics("Main");
        //SetupFeedbackButtonClickHandler();
        CheckIfServiceRunning();
        SetupButtonClickHandler();
        SetupToggleButtonHandler();

    }

    private void CheckIfServiceRunning() {
        if(BackgroundService.number != null)
        {
            Logger.i(Constants.AppNameForLogging, "Service is running");
            EditText inputNumberPattern = (EditText) findViewById(R.id.numberRegex);
            inputNumberPattern.setText(BackgroundService.number);
            Switch toggle = (Switch) findViewById(R.id.switch1);
            toggle.setChecked(true);
        }
    }

    private void SendScreenHitAnalytics(String name)
    {
        mTracker.setScreenName(name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }
    private void SetupButtonClickHandler() {

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SendScreenHitAnalytics("Number Picker");
                Intent i = new Intent(getApplicationContext(), NumberPickerActivity.class);
                startActivityForResult(i, Constants.NumberPickerIntent);
            }
        });
    }

    /*private void SetupFeedbackButtonClickHandler() {

        final Button button = (Button) findViewById(R.id.feedback);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = "Feedback";

                Logger.i(Constants.AppNameForLogging, "Setting screen name: " + name);
                mTracker.setScreenName(name);
                mTracker.send(new HitBuilders.ScreenViewBuilder().build());

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + getPackageName()));
                if (intent.resolveActivity(getPackageManager()) != null)
                    startActivity(intent);
                else {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName()));
                    if (browserIntent.resolveActivity(getPackageManager()) != null)
                        startActivity(browserIntent);
                }

            }
        });
    }*/

    private void SetupToggleButtonHandler()
    {
        Switch toggle = (Switch) findViewById(R.id.switch1);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EditText inputNumberPattern = (EditText) findViewById(R.id.numberRegex);

                if (isChecked) {
                    // The toggle is enabled
                    String number = inputNumberPattern.getText().toString();
                    if(number.isEmpty() || number == null || number  == "")
                    {
                        ShowToast("Enter caller number first!");
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Tracking")
                                .setAction("NoNumber")
                                .build());
                        return;
                    }
                    SharedPreferences settings = getSharedPreferences(Constants.SharedPreferencesFile, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(Constants.CallerNumber, number);
                    editor.commit();

                    Logger.i(Constants.AppNameForLogging, "Registering incoming call listener");
                    Context context = getApplicationContext();
                    Intent i = new Intent(context, BackgroundService.class);
                    context.startService(i);

                    ShowToast("Tracking is turned on");

                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Tracking")
                            .setAction("Started")
                            .build());

                } else {
                    Logger.i(Constants.AppNameForLogging, "De-registering incoming call listener");
                    Context context = getApplicationContext();
                    Intent i = new Intent(context, BackgroundService.class);
                    context.stopService(i);
                    //ShowToast(false, null);
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Tracking")
                            .setAction("Stopped")
                            .build());
                }
            }
        });
    }

    private void ShowToast(String msg)
    {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(intent ==null)
            return;

        String trackerNumber = intent.getStringExtra(Constants.CallerNumber);
        Logger.i(Constants.AppNameForLogging, "Coming from number picker");
        if(trackerNumber !=null) {
            EditText inputNumberPattern = (EditText) findViewById(R.id.numberRegex);
            inputNumberPattern.setText(trackerNumber);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.i(Constants.AppNameForLogging, "Destroying main activity");
    }
}
