package mushirih.pickup.ui;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import mushirih.pickup.mapping.MapsActivity;

/**
 * Created by p-tah on 06/08/2016.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c=Calendar.getInstance();
        int hour=c.get(Calendar.HOUR_OF_DAY);
        int minute=c.get(Calendar.MINUTE);

        int year=c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int date=c.get(Calendar.DATE);

        return new TimePickerDialog(getActivity(),this,hour,minute, android.text.format.DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        MapsActivity.setPickTime(hourOfDay,minute);
    }
}
