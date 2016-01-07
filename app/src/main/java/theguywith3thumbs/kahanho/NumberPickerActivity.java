package theguywith3thumbs.kahanho;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

/**
 * Created by home on 4/12/15.
 */
public class NumberPickerActivity
        extends ListActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getCallLog();
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

                /*Intent i = new Intent(getApplicationContext(), MainActivity.class);
                // sending data to new activity
                i.putExtra(Constants.CallerNumber, selectedNumber);
                startActivity(i);
*/
                Intent intent = new Intent();
                intent.putExtra(Constants.CallerNumber, selectedNumber);
                setResult(Constants.NumberPickerIntent, intent);
                finish(); //That's when you onActivityResult() in the main activity will be called


                callLog_cursor.close();
            }
        };
        myversionOfCallLog.setCursor(callLog_cursor, listener,
                android.provider.CallLog.Calls.NUMBER);
        myversionOfCallLog.setTitle("Choose from Call Log");
        myversionOfCallLog.create().show();
    }
}
