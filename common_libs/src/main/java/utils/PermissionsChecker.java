package utils;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;


/**
 * @author : cathy
 * @package : com.nisco.family.utils
 * @time : 2018/04/08
 * @desc :
 * @version: 1.0
 */

public class PermissionsChecker {
    private final Context mContext;

    public PermissionsChecker(Context context) {
        mContext = context.getApplicationContext();
    }

    // 判断权限集合
    public boolean lacksPermissions(String... permissions) {
        for (String permission : permissions) {
            if (lacksPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    // 判断是否缺少权限
    private boolean lacksPermission(String permission) {
        return ContextCompat.checkSelfPermission(mContext, permission) ==
                PackageManager.PERMISSION_DENIED;
    }
}
