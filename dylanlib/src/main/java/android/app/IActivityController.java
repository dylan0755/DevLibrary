/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package android.app;

/**
 * Testing interface to monitor what is happening in the activity manager
 * while tests are running.  Not for normal application development.
 * {@hide}
 */
public interface IActivityController extends android.os.IInterface {
    /**
     * Default implementation for IActivityController.
     */
    public static class Default implements IActivityController {
        /**
         * The system is trying to start an activity.  Return true to allow
         * it to be started as normal, or false to cancel/reject this activity.
         */
        @Override
        public boolean activityStarting(android.content.Intent intent, String pkg) throws android.os.RemoteException {
            return false;
        }

        /**
         * The system is trying to return to an activity.  Return true to allow
         * it to be resumed as normal, or false to cancel/reject this activity.
         */
        @Override
        public boolean activityResuming(String pkg) throws android.os.RemoteException {
            return false;
        }

        /**
         * An application process has crashed (in Java).  Return true for the
         * normal error recovery (app crash dialog) to occur, false to kill
         * it immediately.
         */
        @Override
        public boolean appCrashed(String processName, int pid, String shortMsg, String longMsg, long timeMillis, String stackTrace) throws android.os.RemoteException {
            return false;
        }

        /**
         * Early call as soon as an ANR is detected.
         */
        @Override
        public int appEarlyNotResponding(String processName, int pid, String annotation) throws android.os.RemoteException {
            return 0;
        }

        /**
         * An application process is not responding.  Return 0 to show the "app
         * not responding" dialog, 1 to continue waiting, or -1 to kill it
         * immediately.
         */
        @Override
        public int appNotResponding(String processName, int pid, String processStats) throws android.os.RemoteException {
            return 0;
        }

        /**
         * The system process watchdog has detected that the system seems to be
         * hung.  Return 1 to continue waiting, or -1 to let it continue with its
         * normal kill.
         */
        @Override
        public int systemNotResponding(String msg) throws android.os.RemoteException {
            return 0;
        }

        @Override
        public android.os.IBinder asBinder() {
            return null;
        }
    }

    /**
     * Local-side IPC implementation stub class.
     */
    public static abstract class Stub extends android.os.Binder implements IActivityController {
        private static final String DESCRIPTOR = "android.app.IActivityController";

        /**
         * Construct the stub at attach it to the interface.
         */
        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an android.app.IActivityController interface,
         * generating a proxy if needed.
         */
        public static IActivityController asInterface(android.os.IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof IActivityController))) {
                return ((IActivityController) iin);
            }
            return new Proxy(obj);
        }

        @Override
        public android.os.IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException {
            String descriptor = DESCRIPTOR;
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(descriptor);
                    return true;
                }
                case TRANSACTION_activityStarting: {
                    data.enforceInterface(descriptor);
                    android.content.Intent _arg0;
                    if ((0 != data.readInt())) {
                        _arg0 = android.content.Intent.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    String _arg1;
                    _arg1 = data.readString();
                    boolean _result = this.activityStarting(_arg0, _arg1);
                    reply.writeNoException();
                    reply.writeInt(((_result) ? (1) : (0)));
                    return true;
                }
                case TRANSACTION_activityResuming: {
                    data.enforceInterface(descriptor);
                    String _arg0;
                    _arg0 = data.readString();
                    boolean _result = this.activityResuming(_arg0);
                    reply.writeNoException();
                    reply.writeInt(((_result) ? (1) : (0)));
                    return true;
                }
                case TRANSACTION_appCrashed: {
                    data.enforceInterface(descriptor);
                    String _arg0;
                    _arg0 = data.readString();
                    int _arg1;
                    _arg1 = data.readInt();
                    String _arg2;
                    _arg2 = data.readString();
                    String _arg3;
                    _arg3 = data.readString();
                    long _arg4;
                    _arg4 = data.readLong();
                    String _arg5;
                    _arg5 = data.readString();
                    boolean _result = this.appCrashed(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
                    reply.writeNoException();
                    reply.writeInt(((_result) ? (1) : (0)));
                    return true;
                }
                case TRANSACTION_appEarlyNotResponding: {
                    data.enforceInterface(descriptor);
                    String _arg0;
                    _arg0 = data.readString();
                    int _arg1;
                    _arg1 = data.readInt();
                    String _arg2;
                    _arg2 = data.readString();
                    int _result = this.appEarlyNotResponding(_arg0, _arg1, _arg2);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                }
                case TRANSACTION_appNotResponding: {
                    data.enforceInterface(descriptor);
                    String _arg0;
                    _arg0 = data.readString();
                    int _arg1;
                    _arg1 = data.readInt();
                    String _arg2;
                    _arg2 = data.readString();
                    int _result = this.appNotResponding(_arg0, _arg1, _arg2);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                }
                case TRANSACTION_systemNotResponding: {
                    data.enforceInterface(descriptor);
                    String _arg0;
                    _arg0 = data.readString();
                    int _result = this.systemNotResponding(_arg0);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                }
                default: {
                    return super.onTransact(code, data, reply, flags);
                }
            }
        }

        private static class Proxy implements IActivityController {
            private android.os.IBinder mRemote;

            Proxy(android.os.IBinder remote) {
                mRemote = remote;
            }

            @Override
            public android.os.IBinder asBinder() {
                return mRemote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            /**
             * The system is trying to start an activity.  Return true to allow
             * it to be started as normal, or false to cancel/reject this activity.
             */
            @Override
            public boolean activityStarting(android.content.Intent intent, String pkg) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                boolean _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    if ((intent != null)) {
                        _data.writeInt(1);
                        intent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(pkg);
                    boolean _status = mRemote.transact(Stub.TRANSACTION_activityStarting, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        return getDefaultImpl().activityStarting(intent, pkg);
                    }
                    _reply.readException();
                    _result = (0 != _reply.readInt());
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            /**
             * The system is trying to return to an activity.  Return true to allow
             * it to be resumed as normal, or false to cancel/reject this activity.
             */
            @Override
            public boolean activityResuming(String pkg) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                boolean _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(pkg);
                    boolean _status = mRemote.transact(Stub.TRANSACTION_activityResuming, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        return getDefaultImpl().activityResuming(pkg);
                    }
                    _reply.readException();
                    _result = (0 != _reply.readInt());
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            /**
             * An application process has crashed (in Java).  Return true for the
             * normal error recovery (app crash dialog) to occur, false to kill
             * it immediately.
             */
            @Override
            public boolean appCrashed(String processName, int pid, String shortMsg, String longMsg, long timeMillis, String stackTrace) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                boolean _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(processName);
                    _data.writeInt(pid);
                    _data.writeString(shortMsg);
                    _data.writeString(longMsg);
                    _data.writeLong(timeMillis);
                    _data.writeString(stackTrace);
                    boolean _status = mRemote.transact(Stub.TRANSACTION_appCrashed, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        return getDefaultImpl().appCrashed(processName, pid, shortMsg, longMsg, timeMillis, stackTrace);
                    }
                    _reply.readException();
                    _result = (0 != _reply.readInt());
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            /**
             * Early call as soon as an ANR is detected.
             */
            @Override
            public int appEarlyNotResponding(String processName, int pid, String annotation) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                int _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(processName);
                    _data.writeInt(pid);
                    _data.writeString(annotation);
                    boolean _status = mRemote.transact(Stub.TRANSACTION_appEarlyNotResponding, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        return getDefaultImpl().appEarlyNotResponding(processName, pid, annotation);
                    }
                    _reply.readException();
                    _result = _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            /**
             * An application process is not responding.  Return 0 to show the "app
             * not responding" dialog, 1 to continue waiting, or -1 to kill it
             * immediately.
             */
            @Override
            public int appNotResponding(String processName, int pid, String processStats) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                int _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(processName);
                    _data.writeInt(pid);
                    _data.writeString(processStats);
                    boolean _status = mRemote.transact(Stub.TRANSACTION_appNotResponding, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        return getDefaultImpl().appNotResponding(processName, pid, processStats);
                    }
                    _reply.readException();
                    _result = _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            /**
             * The system process watchdog has detected that the system seems to be
             * hung.  Return 1 to continue waiting, or -1 to let it continue with its
             * normal kill.
             */
            @Override
            public int systemNotResponding(String msg) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                int _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(msg);
                    boolean _status = mRemote.transact(Stub.TRANSACTION_systemNotResponding, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        return getDefaultImpl().systemNotResponding(msg);
                    }
                    _reply.readException();
                    _result = _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            public static IActivityController sDefaultImpl;
        }

        static final int TRANSACTION_activityStarting = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
        static final int TRANSACTION_activityResuming = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
        static final int TRANSACTION_appCrashed = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
        static final int TRANSACTION_appEarlyNotResponding = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
        static final int TRANSACTION_appNotResponding = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
        static final int TRANSACTION_systemNotResponding = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);

        public static boolean setDefaultImpl(IActivityController impl) {
            if (Proxy.sDefaultImpl == null && impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static IActivityController getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

    /**
     * The system is trying to start an activity.  Return true to allow
     * it to be started as normal, or false to cancel/reject this activity.
     */
    public boolean activityStarting(android.content.Intent intent, String pkg) throws android.os.RemoteException;

    /**
     * The system is trying to return to an activity.  Return true to allow
     * it to be resumed as normal, or false to cancel/reject this activity.
     */
    public boolean activityResuming(String pkg) throws android.os.RemoteException;

    /**
     * An application process has crashed (in Java).  Return true for the
     * normal error recovery (app crash dialog) to occur, false to kill
     * it immediately.
     */
    public boolean appCrashed(String processName, int pid, String shortMsg, String longMsg, long timeMillis, String stackTrace) throws android.os.RemoteException;

    /**
     * Early call as soon as an ANR is detected.
     */
    public int appEarlyNotResponding(String processName, int pid, String annotation) throws android.os.RemoteException;

    /**
     * An application process is not responding.  Return 0 to show the "app
     * not responding" dialog, 1 to continue waiting, or -1 to kill it
     * immediately.
     */
    public int appNotResponding(String processName, int pid, String processStats) throws android.os.RemoteException;

    /**
     * The system process watchdog has detected that the system seems to be
     * hung.  Return 1 to continue waiting, or -1 to let it continue with its
     * normal kill.
     */
    public int systemNotResponding(String msg) throws android.os.RemoteException;
}