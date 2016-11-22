package mushirih.pickup.ui;



        import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

import mushirih.pickup.http.Load;

/**
 * Created by p-tah on 14/10/2016.
 */
public  class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c= Calendar.getInstance();


        int year=c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int date=c.get(Calendar.DATE);

        DatePickerDialog dpd=new DatePickerDialog(getActivity(),this,year,month,date);
        dpd.setTitle("Pick date for transport");
        return dpd;
    }


    @Override
    public  void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Load.setDate(dayOfMonth,monthOfYear+1,year);

    }

}