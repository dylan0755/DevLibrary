package com.dylan.library.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Author: Dylan
 * Date: 2020/3/27
 * Desc:
 */
public class PermissionRequestBuilder {

    private Activity mActivity;
    private List<PermissionRequest> permissionRequestList = new ArrayList<>();
    private long requestStartTime;


    public PermissionRequestBuilder(Activity context) {
        mActivity = context;
    }

    public PermissionRequestBuilder addPerm(String permissionName, boolean isForceNeed) {
        PermissionRequest request = new PermissionRequest(permissionName, isForceNeed);
        permissionRequestList.add(request);
        return this;
    }


    public String[] build() {
        Iterator<PermissionRequest> it = permissionRequestList.iterator();
        while (it.hasNext()) {
            PermissionRequest request = it.next();
            int result = ContextCompat.checkSelfPermission(mActivity, request.getName());
            if (result == PackageManager.PERMISSION_GRANTED) {//已获得权限
                it.remove();
            }
        }
        List<String> permissionList = new ArrayList<>();
        for (PermissionRequest request : permissionRequestList) {
            permissionList.add(request.getName());
        }
        mActivity = null;
        return permissionList.toArray(new String[permissionList.size()]);
    }


    public boolean startRequest(int requestCode) {
        if (permissionRequestList == null) return false;
        Iterator<PermissionRequest> it = permissionRequestList.iterator();
        while (it.hasNext()) {
            PermissionRequest request = it.next();
            int result = ContextCompat.checkSelfPermission(mActivity, request.getName());
            if (result == PackageManager.PERMISSION_GRANTED) {//已获得权限
                it.remove();
            }
        }
        List<String> permissionList = new ArrayList<>();
        for (PermissionRequest request : permissionRequestList) {
            permissionList.add(request.getName());
        }
        if (EmptyUtils.isNotEmpty(permissionList)) {
            String[] requestPermissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(mActivity, requestPermissions, requestCode);
            requestStartTime = System.currentTimeMillis();
            return true;
        }
        mActivity = null;
        return false;
    }


    public List<PermissionRequest> getPermRequestList() {
        return permissionRequestList;
    }


    //检查是否拒绝强制权限
    public RequestReuslt onRequestPermissionsResult(String[] permissions, @NonNull int[] grantResults) {
        List<String> list = new ArrayList<>();


        if (EmptyUtils.isNotEmpty(permissions) && EmptyUtils.isNotEmpty(grantResults)) {
            for (int i = 0; i < grantResults.length; i++) {
                int result = grantResults[i];
                if (result != PackageManager.PERMISSION_GRANTED) {
                    boolean isNeed = getPermission(permissions[i]).isForceNeed();
                    if (isNeed) {//强制需要的权限没有给予
                        list.add(permissions[i]);
                    }
                }
            }
        }


        RequestReuslt requestReuslt = new RequestReuslt();
        requestReuslt.hasRejectForceNeed = !list.isEmpty();
        requestReuslt.rejectList = list;
        requestReuslt.duration = System.currentTimeMillis() - requestStartTime;
        requestStartTime = 0;
        return requestReuslt;

    }

    public PermissionRequest getPermission(String permissionName) {
        if (permissionRequestList == null) return new PermissionRequest();
        for (PermissionRequest request : permissionRequestList) {
            if (request.getName().equals(permissionName)) {
                return request;
            }
        }
        return new PermissionRequest();
    }


    public class PermissionRequest {
        private String name;//权限名称
        private boolean isForceNeed;// 必须

        private PermissionRequest() {

        }

        private PermissionRequest(String name, boolean isForceNeed) {
            this.name = name;
            this.isForceNeed = isForceNeed;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isForceNeed() {
            return isForceNeed;
        }

        public void setForceNeed(boolean forceNeed) {
            isForceNeed = forceNeed;
        }


    }


    public class RequestReuslt {
        public boolean hasRejectForceNeed;
        public long duration;
        public List<String> rejectList;

    }
}
