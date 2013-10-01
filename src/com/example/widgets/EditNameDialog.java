package com.example.widgets;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import com.example.R;

/**
 * Created with IntelliJ IDEA.
 * User: sukhmeet
 * Date: 17/09/13
 * Time: 4:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class EditNameDialog extends DialogFragment implements TextView.OnEditorActionListener {

    String TAG = "EDIT-NAME-DIALOG";


    EditNameDialogListener editNameDialogListener;

    public EditNameDialog() {
    }


    public interface EditNameDialogListener {
        void onFinishEditDialog(String inputText);

        void onPositiveButtonClick(DialogFragment dialogFragment);

        void onNegativeButtonClick(DialogFragment dialogFragment);
    }


    /**
     * Override to build your own custom Dialog container.  This is typically
     * used to show an AlertDialog instead of a generic Dialog; when doing so,
     * {@link #onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)} does not need
     * to be implemented since the AlertDialog takes care of its own content.
     * <p/>
     * <p>This method will be called after {@link #onCreate(android.os.Bundle)} and
     * before {@link #onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)}.  The
     * default implementation simply instantiates and returns a {@link android.app.Dialog}
     * class.
     * <p/>
     * <p><em>Note: DialogFragment own the {@link android.app.Dialog#setOnCancelListener
     * Dialog.setOnCancelListener} and {@link android.app.Dialog#setOnDismissListener
     * Dialog.setOnDismissListener} callbacks.  You must not set them yourself.</em>
     * To find out about these events, override {@link #onCancel(android.content.DialogInterface)}
     * and {@link #onDismiss(android.content.DialogInterface)}.</p>
     *
     * @param savedInstanceState The last saved instance state of the Fragment,
     *                           or null if this is a freshly created Fragment.
     * @return Return a new Dialog instance to be displayed by the Fragment.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.text_dialog, null);
        builder.setView(view);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "OK button Click");
                EditNameDialogListener activity = (EditNameDialogListener) getActivity();
                activity.onPositiveButtonClick(EditNameDialog.this);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "Cancel button click");
                EditNameDialogListener activity = (EditNameDialogListener) getActivity();
                activity.onNegativeButtonClick(EditNameDialog.this);
            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "Called onAttach");
        super.onAttach(activity);    //To change body of overridden methods use File | Settings | File Templates.
        try {
            editNameDialogListener = (EditNameDialogListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement NoticeDialog Listener");
        }
    }

    @Override
    public void onDetach() {
        editNameDialogListener = null;
        super.onDetach();
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }


}


//REF : http://developer.android.com/guide/topics/ui/dialogs.html#PassingEvents
// http://stackoverflow.com/questions/14320787/android-passing-args-from-alertdialog-to-alertdialoghost