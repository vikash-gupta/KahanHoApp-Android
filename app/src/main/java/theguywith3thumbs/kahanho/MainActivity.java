package theguywith3thumbs.kahanho;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);
            toggle.setChecked(true);
        }
    }

    private void SetupButtonClickHandler() {

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), NumberPickerActivity.class);
                startActivityForResult(i,Constants.NumberPickerIntent);
            }
        });
    }


    private void SetupToggleButtonHandler()
    {
        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EditText inputNumberPattern = (EditText) findViewById(R.id.numberRegex);

                if (isChecked) {
                    // The toggle is enabled
                    String number = inputNumberPattern.getText().toString();

                    SharedPreferences settings = getSharedPreferences(Constants.SharedPreferencesFile, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(Constants.CallerNumber, number);
                    editor.commit();

                    Logger.i(Constants.AppNameForLogging, "Registering incoming call listener");
                    Context context = getApplicationContext();
                    Intent i = new Intent(context, BackgroundService.class);
                    context.startService(i);

                    ShowToast(true, number);

                } else {
                    Logger.i(Constants.AppNameForLogging, "De-registering incoming call listener");
                    Context context = getApplicationContext();
                    Intent i = new Intent(context, BackgroundService.class);
                    context.stopService(i);
                    ShowToast(false, null);
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
