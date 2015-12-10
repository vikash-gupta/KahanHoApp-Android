package theguywith3thumbs.kahanho;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by home on 4/12/15.
 */
public class NumberPickerActivity
        extends ListActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getCallLog();

        // storing string resources into Array
        /*String[] numbers = {"one","two","three","four"};
        // here you store the array of string you got from the database

        // Binding Array to ListAdapter
        this.setListAdapter(new ArrayAdapter<String>(this, R.layout.activity_call_log,R.id.textView2,numbers));

        // refer the ArrayAdapter Document in developer.android.com
        ListView lv = getListView();

        // listening to single list item on click
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // selected item
                //String num = ((TextView) view).getText().toString();
                String num = (String) parent.getItemAtPosition(position);
                // Launching new Activity on selecting single List Item
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                // sending data to new activity
                i.putExtra(Constants.CallerNumber, num);
                startActivity(i);

            }
        });*/
    }

    public void getCallLog() {

        String[] callLogFields = { android.provider.CallLog.Calls._ID,
                android.provider.CallLog.Calls.NUMBER,
                android.provider.CallLog.Calls.CACHED_NAME /* im not using the name but you can*/};
        String viaOrder = android.provider.CallLog.Calls.DATE + " DESC";
        //String WHERE = android.provider.CallLog.Calls.NUMBER + " >0"; /*filter out private/unknown numbers */

        final Cursor callLog_cursor = this.getContentResolver().query(
                android.provider.CallLog.Calls.CONTENT_URI, callLogFields,
                null, null, viaOrder);

        AlertDialog.Builder myversionOfCallLog = new AlertDialog.Builder(this);

        android.content.DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int item) {
                callLog_cursor.moveToPosition(item);

                String selectedNumber = callLog_cursor.getString(callLog_cursor
                                .getColumnIndex(android.provider.CallLog.Calls.NUMBER));
                Logger.d(Constants.AppNameForLogging,"Selected number from call log is " + selectedNumber);

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                // sending data to new activity
                i.putExtra(Constants.CallerNumber, selectedNumber);
                startActivity(i);

                callLog_cursor.close();
            }
        };
        myversionOfCallLog.setCursor(callLog_cursor, listener,
                android.provider.CallLog.Calls.NUMBER);
        myversionOfCallLog.setTitle("Choose from Call Log");
        myversionOfCallLog.create().show();
    }
}
