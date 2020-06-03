-keepclassmembers  class com.mr.k.mvp.net.DataRequest{
    private void setDataType(*);
    private * getDataType();

}

-keep interface com.mr.k.mvp.net.INetEntity{*;}



# ---------------okhttp ------------------
# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform


# ---------------okhttp ------------------






# ------------------ RXJAVA ------------------

-dontwarn java.util.concurrent.Flow*

# ------------------ RXJAVA ------------------







# ------------------ Glide ------------------

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
#pro

-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder

# for DexGuard only
#-keep resourcexmlelements manifest/application/meta-data@value=GlideModule

# ------------------ Glide ------------------



# ---------------- greendao -------------


-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties { *; }

# If you DO use SQLCipher:
-keep class org.greenrobot.greendao.database.SqlCipherEncryptedHelper { *; }

# If you do NOT use SQLCipher:
-dontwarn net.sqlcipher.database.**
# If you do NOT use RxJava:
-dontwarn rx.**

# ---------------- greendao -------------
