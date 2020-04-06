package com.dylan.library.service;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * Created by Dylan on 2016/12/31.
 */

public class AutoInstallService extends AccessibilityService {

    public static boolean isOpen;


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
            processAccessibilityEnvent(event);
    }


    private void processAccessibilityEnvent(AccessibilityEvent event) {
              doInstall(event);
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        return true;
    }


    private void doInstall(AccessibilityEvent event) {

        if (event.getSource() != null) {
            if (event.getPackageName().equals("com.android.packageinstaller")) {

                List<AccessibilityNodeInfo> unintall_nodes = event.getSource().findAccessibilityNodeInfosByText("安装");
                if (unintall_nodes != null && !unintall_nodes.isEmpty()) {
                    AccessibilityNodeInfo node;
                    for (int i = 0; i < unintall_nodes.size(); i++) {
                        node = unintall_nodes.get(i);
                        if (checkWidgetType(node)) {
                            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }

                List<AccessibilityNodeInfo> nex_nodes = event.getSource().findAccessibilityNodeInfosByText("继续");
                if (nex_nodes != null && !nex_nodes.isEmpty()) {
                    AccessibilityNodeInfo node;
                    for (int i = 0; i < nex_nodes.size(); i++) {
                        node = nex_nodes.get(i);
                        if (checkWidgetType(node)) {
                            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }


                List<AccessibilityNodeInfo> next_nodes = event.getSource().findAccessibilityNodeInfosByText("下一步");
                if (next_nodes != null && !next_nodes.isEmpty()) {
                    AccessibilityNodeInfo node;
                    for (int i = 0; i < next_nodes.size(); i++) {
                        node = next_nodes.get(i);
                        if (checkWidgetType(node)) {
                            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }

                if (isOpen){//安装完是否要打开应用
                    List<AccessibilityNodeInfo> ok_nodes = event.getSource().findAccessibilityNodeInfosByText("打开");
                    if (ok_nodes != null && !ok_nodes.isEmpty()) {
                        Log.e( "processinstall ", "打开");
                        AccessibilityNodeInfo node;
                        for (int i = 0; i < ok_nodes.size(); i++) {
                            node = ok_nodes.get(i);
                            if (checkWidgetType(node)) {
                                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            }
                        }
                    }
                }



            }
        }

    }

    private boolean checkWidgetType(AccessibilityNodeInfo node) {
        return (node.getClassName().equals("android.widget.Button")
                || node.getClassName().equals("android.widget.TextView")
                || node.getClassName().equals("android.widget.RelativeLayout")
                || node.getClassName().equals("android.widget.LinearLayout"))
                && node.isEnabled();
    }





}
