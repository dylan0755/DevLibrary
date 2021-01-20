# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\AD\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
# -keepclassmembers class fqcn.of.javascript.interface.for.webview {
#    public *;
# }


 # 保留所有注解
 -keepattributes *Annotation*
 -keepattributes *JavascriptInterface*


 # 这句话能够使我们的项目混淆后产生映射文件
  # 包含有类名->混淆后类名的映射关系
  -verbose

  # 指定不去忽略非公共库的类成员
  -dontskipnonpubliclibraryclassmembers

  # 不做预校验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步能够加快混淆速度。
  -dontpreverify

  # 保留Annotation不混淆
  -keepattributes *Annotation*,InnerClasses

  # 避免混淆泛型
  -keepattributes Signature

  -keepattributes InnerClasses,EnclosingMethod

  # 抛出异常时保留代码行号
  -keepattributes SourceFile,LineNumberTable

############################################  模板 start ##############################################################

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
-keep class android.support.** {*;}

-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}










-keepattributes *Annotation*,EnclosingMethod,Signature
-keep public class * extends android.widget.BaseAdapter {*;}

# 保留R下面的资源
 -keep class **.R$* {*;}
# support-v4
-dontwarn android.support.v4.**
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }
# support-v7
-dontwarn android.support.v7.**
-keep class android.support.v7.internal.** { *; }
-keep interface android.support.v7.internal.** { *; }
# android design
-keep class android.support.design.widget.** { *; }
-keep interface android.support.design.widget.** { *; }
-dontwarn android.support.design.**
-keepclasseswithmembers class android.support.v7.widget.RecyclerView$ViewHolder {
   public final View *;
}



#webView
-keep public class android.webkit.**
-keepattributes SetJavaScriptEnabled
-keepattributes JavascriptInterface
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
   public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, jav.lang.String);
}



 # 保留本地native方法不被混淆
  -keepclasseswithmembernames class * {
      native <methods>;
  }

  # 保留枚举类不被混淆
   -keepclassmembers enum * {
       public static **[] values();
       public static ** valueOf(java.lang.String);
   }


   # 保留我们自定义控件（继承自View）不被混淆
    -keep public class * extends android.view.View{
        *** get*();
        void set*(***);
        public <init>(android.content.Context);
        public <init>(android.content.Context, android.util.AttributeSet);
        public <init>(android.content.Context, android.util.AttributeSet, int);
    }
    # 保留Parcelable序列化类不被混淆
     -keep class * implements android.os.Parcelable {
         public static final android.os.Parcelable$Creator *;
     }


     # 保留Serializable序列化的类不被混淆
      -keepclassmembers class * implements java.io.Serializable {
          static final long serialVersionUID;
          private static final java.io.ObjectStreamField[] serialPersistentFields;
          !static !transient <fields>;
          !private <fields>;
          !private <methods>;
          private void writeObject(java.io.ObjectOutputStream);
          private void readObject(java.io.ObjectInputStream);
          java.lang.Object writeReplace();
          java.lang.Object readResolve();
      }


      # 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
       -keepclassmembers class * {
           void *(**On*Event);
           void *(**On*Listener);
       }



  # 保留在Activity中的方法参数是view的方法，
  # 这样以来我们在layout中写的onClick就不会被影响
  -keepclassmembers class * extends android.app.Activity{
      public void *(android.view.View);
  }


-keep public class android.net.http.SslError
# -dontwarn android.webkit.WebView
 -dontwarn android.net.http.SslError

# -dontwarn Android.webkit.WebViewClient


############################################  模板  end ##############################################################





################################## 被 NotProguard 标记过的不混淆

-keep @com.dylan.library.proguard.NotProguard class * {*;}
-keep class * {
@com.dylan.library.proguard.NotProguard <fields>;
}
-keepclassmembers class * {
@com.dylan.library.proguard.NotProguard <methods>;
}



###################################


-keep class com.dylan.library.adapter.CommonAbsListView.Adapter {*;}

-keep class android.support.** {*;}
-keep class com.bumptech.glide.**{*;}


#module中除了widget包进行混淆，其它包中的类不进行混淆
-keep class com.dylan.library.activity.**{
      public *;
      protected *;
}

-keep class com.dylan.library.adapter.**{
      public *;
      protected *;
}
-keep class com.dylan.library.bean.** {
      public *;
      protected *;
 }

 -keep class com.dylan.library.callback.** {
      public *;
      protected *;
  }

   -keep class com.dylan.library.contentObserver.** {
        public *;
        protected *;
    }

-keep class com.dylan.library.device.** {
      public *;
      protected *;
}

-keep class com.dylan.library.dialog.** {
      public *;
      protected *;
}


-keep class com.dylan.library.exception.** {
      public *;
      protected *;
}

-keep class com.dylan.library.fragment.** {
      public *;
      protected *;
}

-keep class com.dylan.library.graphics.** {
      public *;
      protected *;
}

-keep class com.dylan.library.io.** {
      public *;
      protected *;
}

-keep class com.dylan.library.manager.** {
      public *;
      protected *;
}
-keep class com.dylan.library.media.** {
      public *;
      protected *;
}

-keep class com.dylan.library.net.** {
      public *;
      protected *;
}
-keep class com.dylan.library.opengl.** {
      public *;
      protected *;
}
-keep class com.dylan.library.preferecen.** {
      public *;
      protected *;
}

-keep class com.dylan.library.proguard.** {
      public *;
      protected *;
}


-keep class com.dylan.library.screen.** {
      public *;
      protected *;
}

-keep class com.dylan.library.search.** {
      public *;
      protected *;
}

-keep class com.dylan.library.service.** {
      public *;
      protected *;
}
-keep class com.dylan.library.widget.tab.** {
      public *;
      protected *;
}

-keep class com.dylan.library.test.** {
      public *;
      protected *;
}
-keep class com.dylan.library.utils.** {
      public *;
      protected *;
}
-keep class com.dylan.library.webview.** {
      public *;
      protected *;
}




-keep class com.dylan.library.widget.** {
      public <init>(android.content.Context);
      public <init>(android.content.Context, android.util.AttributeSet);
      public <init>(android.content.Context, android.util.AttributeSet, int);
      public *;
      protected *;
}












