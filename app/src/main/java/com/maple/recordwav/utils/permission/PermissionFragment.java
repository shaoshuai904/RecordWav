package com.maple.recordwav.utils.permission;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import java.util.ArrayList;

/**
 * 权限开启
 *
 * @author maple
 * @time 17/1/5 上午11:00
 */
public class PermissionFragment extends Fragment {
    public static final String TAG = "PermissionFragment";

    private static final int PERMISSIONS_REQUEST_CODE = 42;
    private static final int REQUEST_APP_SETTINGS = 43;

    public boolean isStatus;
    private PermissionListener mPermissionListener;

    public static PermissionFragment getPermissionFragment(Activity activity) {
        PermissionFragment permissionFragment = (PermissionFragment) activity.getFragmentManager().findFragmentByTag(PermissionFragment.TAG);
        if (permissionFragment == null) {
            permissionFragment = new PermissionFragment();
            FragmentManager fragmentManager = activity.getFragmentManager();
            fragmentManager.beginTransaction()
                    .add(permissionFragment, PermissionFragment.TAG)
                    .commit();
            fragmentManager.executePendingTransactions();
        }
        return permissionFragment;
    }

    public PermissionFragment setPermissionListener(PermissionListener permissionListener) {
        mPermissionListener = permissionListener;
        return this;
    }

    private void requestPermissions(@NonNull String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isStatus = shouldShowRequestPermissionRationale(permissions[0]);
            requestPermissions(permissions, PERMISSIONS_REQUEST_CODE);
        } else {
            // API < 23 no need request
        }
    }

    // @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                ArrayList<String> deniedPermissions = new ArrayList<>();
                if (permissions.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        int grantResult = grantResults[i];
                        if (grantResult == PackageManager.PERMISSION_DENIED) {
                            deniedPermissions.add(permissions[i]);
                        }
                    }
                }
                if (deniedPermissions.size() > 0) {
                    String[] string = new String[deniedPermissions.size()];
                    string = deniedPermissions.toArray(string);
                    denied(string);
                    if (!shouldShowRequestPermissionRationale(permissions[0]) && !isStatus) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_APP_SETTINGS);
                    }
                } else {
                    granted();
                }
                break;
            case REQUEST_APP_SETTINGS:

                break;
            default:
                break;
        }
    }

    public void checkPermissions(@NonNull String[] permissions) {
        checkPermissions(permissions, null);
    }

    public void checkPermissions(@NonNull String[] permissions, String permissionMessage) {
        if (Build.VERSION.SDK_INT < 23) {
            granted();
            return;
        }

        ArrayList<String> needPermissions = new ArrayList();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                needPermissions.add(permission);
            }
        }

        if (needPermissions.isEmpty()) {
            granted();
            return;
        }

        String[] string = new String[needPermissions.size()];
        needPermissions.toArray(string);
        if (TextUtils.isEmpty(permissionMessage)) {
            requestPermissions(string);
        } else {
            showRequestPermissionDialog(string, permissionMessage);
        }
    }

    private void showRequestPermissionDialog(@NonNull final String[] permissions, String permissionMessage) {
        AlertDialog builder = new AlertDialog.Builder(getActivity())
                .setMessage(permissionMessage)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.dismiss();
                        denied(permissions);
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(permissions);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        denied(permissions);
                    }
                }).create();

        builder.setCanceledOnTouchOutside(false);
        builder.show();
    }

    // 同意
    private void granted() {
        if (mPermissionListener != null) {
            mPermissionListener.onPermissionGranted();
        }
    }

    // 拒绝
    private void denied(@NonNull String[] permissions) {
        if (mPermissionListener != null) {
            mPermissionListener.onPermissionDenied(permissions);
        }
    }

}
