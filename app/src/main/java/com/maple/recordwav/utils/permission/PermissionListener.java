package com.maple.recordwav.utils.permission;

/**
 * 权限开启监听
 *
 * @author maple
 * @time 2017/1/5
 */
public interface PermissionListener {

    /**
     * 同意
     */
    void onPermissionGranted();

    /**
     * 拒绝
     */
    void onPermissionDenied(String[] deniedPermissions);
}
