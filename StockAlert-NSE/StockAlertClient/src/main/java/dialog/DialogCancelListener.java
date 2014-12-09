package dialog;

import android.content.DialogInterface;
import android.widget.AutoCompleteTextView;

/**
 * Created by ashwin on 15/8/14.
 */
public class DialogCancelListener implements DialogInterface.OnCancelListener {

    private AutoCompleteTextView actv;//reference of the autocomplete edit box so that it can be cleared when dialog is cancelled
    public DialogCancelListener(AutoCompleteTextView actv)
    {
        this.actv=actv;
    }
    @Override
    public void onCancel(DialogInterface dialogInterface) {
    //Clear the stock from the autocomplete edit box
        actv.getText().clear();
    }
}
