package com.dylan.library.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;

import com.dylan.library.R;
import com.dylan.library.utils.ContextUtils;
import com.dylan.library.utils.PermissionUtils;
import com.dylan.library.utils.ToastUtils;

/**
 * Author: Dylan
 * Date: 2022/4/6
 * Desc:
 */

public class AndroidRStorageAccessPermDialog extends Dialog {
    public static final int GRANT_PERSISTABLE_URI_PERMISSION=84;
    public AndroidRStorageAccessPermDialog(@NonNull final Context context) {
        super(context,R.style.DLCustomFloatingDialog);
        setContentView(R.layout.dl_dialog_android_r_access_storage);
        setCancelable(false);

        findViewById(R.id.tvIKnow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Uri uri = Uri.parse("content://com.android.externalstorage.documents/document/primary%3AAndroid%2Fdata");
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
                //flag看实际业务需要可再补充
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                ContextUtils.getActivity(context).startActivityForResult(intent, GRANT_PERSISTABLE_URI_PERMISSION);
            }
        });

        findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    private GrantPermissionCallBack mGrantPermissionCallBack;
    public void show(GrantPermissionCallBack callBack) {
        super.show();
        mGrantPermissionCallBack=callBack;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case AndroidRStorageAccessPermDialog.GRANT_PERSISTABLE_URI_PERMISSION:
                if (resultCode == Activity.RESULT_OK) {
                    getContext().getContentResolver().takePersistableUriPermission(data.getData(),
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    if (PermissionUtils.isGrantAndroidData(getContext())) {
                       if (mGrantPermissionCallBack!=null)mGrantPermissionCallBack.hasGrant();
                    }else{
                        if (mGrantPermissionCallBack!=null)mGrantPermissionCallBack.hasReject();
                    }
                }
                break;
        }

    }


    public interface GrantPermissionCallBack{
        void hasGrant();
        void hasReject();
    }

}
