package dialog;

import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by ashwin on 2/8/14.
 */
public class DialogNoOnClickListener implements DialogInterface.OnClickListener {


    public void onClick(DialogInterface dialog, int which)
    {

        //What should happend when NO button is pressed
        dialog.cancel();
    }

}
