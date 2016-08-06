package mushirih.pickup.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;


/**
 * Created by p-tah on 06/08/2016.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c=Calendar.getInstance();


        int year=c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int date=c.get(Calendar.DATE);

        return new DatePickerDialog(getActivity(),this,year,month,date);
    }


    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Toast.makeText(getActivity().getApplicationContext(),year+","+monthOfYear+","+dayOfMonth,Toast.LENGTH_SHORT).show();
    }
}
