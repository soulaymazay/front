package com.example.transportenligne;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

/**
 * A dialog that explains the use of the location permission and requests the necessary
 * permission.
 * <p>
 * The activity should implement {@link androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback}
 * to handle permit or denial of this permission request.
 */
public class RationaleDialog extends DialogFragment {

    private static final String ARGUMENT_PERMISSION_REQUEST_CODE = "requestCode";

    private static final String ARGUMENT_FINISH_ACTIVITY = "finish";

    private boolean finishActivity = false;

    /**
     * Creates a new instance of a dialog displaying the rationale for the use of the location
     * permission.
     * <p>
     * The permission is requested after clicking 'ok'.
     *
     * @param requestCode    Id of the request that is used to request the permission. It is
     *                       returned to the {@link androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback}.
     * @param finishActivity Whether the calling Activity should be finished if the dialog is
     *                       cancelled.
     */
    public static RationaleDialog newInstance(int requestCode, boolean finishActivity) {
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PERMISSION_REQUEST_CODE, requestCode);
        arguments.putBoolean(ARGUMENT_FINISH_ACTIVITY, finishActivity);
        RationaleDialog dialog = new RationaleDialog();
        dialog.setArguments(arguments);
        return dialog;
    }


}
