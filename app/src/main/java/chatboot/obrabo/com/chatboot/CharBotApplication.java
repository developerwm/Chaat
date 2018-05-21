package chatboot.obrabo.com.chatboot;

import android.app.Application;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(
        mailTo  = "taua.one@gmail.com",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.erro_support,
        customReportContent = {
                ReportField.APP_VERSION_CODE,
                ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION,
                ReportField.PHONE_MODEL,
                ReportField.DEVICE_ID,
                ReportField.STACK_TRACE,
                ReportField.LOGCAT,
                ReportField.APPLICATION_LOG,
                ReportField.THREAD_DETAILS}
)


public class CharBotApplication  extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //ACRA.init(this);
    }
}
