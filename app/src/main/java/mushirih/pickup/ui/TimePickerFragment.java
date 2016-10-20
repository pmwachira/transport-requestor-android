package mushirih.pickup.ui;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;

import java.util.Calendar;

import mushirih.pickup.http.Load;

/**
 * Created by p-tah on 06/08/2016.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c= Calendar.getInstance();
        int hour=c.get(Calendar.HOUR_OF_DAY);
        int minute=c.get(Calendar.MINUTE);

        TimePickerDialog tpd=new TimePickerDialog(getActivity(),this,hour,minute, !android.text.format.DateFormat.is24HourFormat(getActivity()));
        tpd.setTitle("Set preferred time of travel");
        return tpd;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Load.setTime(hourOfDay,minute);

    }
}
