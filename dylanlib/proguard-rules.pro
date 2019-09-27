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



#指定代码的压缩级别
-optimizationpasses 5

#包明不混合大小写
-dontusemixedcaseclassnames

#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses

 #优化  不优化输入的类文件
-dontoptimize

 #预校验
-dontpreverify

 #混淆时是否记录日志
-verbose

 # 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#保护注解
-keepattributes *Annotation*

# 保持哪些类不被混淆
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
#如果有引用v4包可以添加下面这行
-keep public class * extends android.support.v4.app.Fragment


#忽略警告
-ignorewarning

##记录生成的日志数据,gradle build时在本项目根目录输出##
#apk 包内所有 class 的内部结构
-dump proguard/class_files.txt
#未混淆的类和成员
-printseeds proguard/seeds.txt
#列出从 apk 中删除的代码
-printusage proguard/unused.txt
#混淆前后的映射
-printmapping proguard/mapping.txt
########记录生成的日志数据，gradle build时 在本项目根目录输出-end######

#如果引用了v4或者v7包
-dontwarn android.support.**


#-keepclasseswithmembernames 通过成员来指定哪些类的类名和成员不被混淆


#保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}
#保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable
#保持枚举 enum 类不被混淆
-keepclassmembers enum * {
  public static **[] values();
  public static ** valueOf(java.lang.String);
}
#资源类不被混淆
-keepclassmembers class **.R$* {
    public static <fields>;
}
#保护注解
-keepattributes Annotation
#忽略警告
-ignorewarning

  # 保持哪些类不被混淆
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference







 # 构造器需要传入View的类  保留
-keepclasseswithmembers class * {
      public <init>(android.view.View);
      public <init>(android.content.Context, android.util.AttributeSet);
}


#<-----保持自定义控件类不被混淆----->

#这个包括了 继承LinearLayout ，FrameLayout等ViewGroup的类都不会被混淆，而不是直接继承View类的形式才不会被混淆的
-keep public class * extends android.view.View {
# *;  如果里面只保留一个通配符  那么所定义类里面的基本数据类型全部保留原样  并不会变成  a,b,c,d,f的形式
          public <init>(android.content.Context);
          public <init>(android.content.Context, android.util.AttributeSet);
          public <init>(android.content.Context, android.util.AttributeSet, int);
          public *;
          protected *;

}

-keepclassmembers class * extends android.app.Activity {
public void *(android.view.View);
}
#<-----保持自定义控件类不被混淆----->




-keep class com.dylan.library.adapter.CommonAbsListView.Adapter {*;}

-keep class android.support.** {*;}
-keep class com.bumptech.glide.**{*;}
#module中除了widget包进行混淆，其它包中的类不进行混淆
-keep class com.dylan.library.adapter.**{
      *;
}
-keep class com.dylan.library.bean.** {
      *;
 }

 -keep class com.dylan.library.callback.** {
       *;
  }

-keep class com.dylan.library.device.** {
      *;
}

-keep class com.dylan.library.dialog.** {
      *;
}


-keep class com.dylan.library.exception.** {
      *;
}

-keep class com.dylan.library.fragment.** {
      *;
}

-keep class com.dylan.library.graphics.** {
      *;
}

-keep class com.dylan.library.io.** {
      *;
}

-keep class com.dylan.library.manager.** {
      *;
}
-keep class com.dylan.library.media.** {
      *;
}

-keep class com.dylan.library.net.** {
      *;
}
-keep class com.dylan.library.preferecen.** {
      *;
}

-keep class com.dylan.library.screen.** {
      *;
}

-keep class com.dylan.library.search.** {
      *;
}

-keep class com.dylan.library.service.** {
      *;
}
-keep class com.dylan.library.tab.** {
      *;
}

-keep class com.dylan.library.test.** {
      *;
}
-keep class com.dylan.library.utils.** {
      *;
}
-keep class com.dylan.library.webview.** {
      *;
}
-keep class com.dylan.library.widget.** {
     #module中除了widget包进行混淆，其它包中的类不进行混淆,并且public 和protected 成员不被混淆
      public <init>(android.content.Context);
      public <init>(android.content.Context, android.util.AttributeSet);
      public <init>(android.content.Context, android.util.AttributeSet, int);
      public *;
      protected *;
}












