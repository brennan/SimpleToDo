package android.brennangamwell.com.simpletodo;

import android.app.Application;
import com.segment.analytics.Analytics;

public class SimpleToDoApp extends Application{
  @Override public void onCreate() {
    super.onCreate();

    final String WRITE_KEY = "123";

    Analytics analytics = new Analytics.Builder(this, WRITE_KEY)
        .trackApplicationLifecycleEvents()
        .recordScreenViews()
        .build();
    Analytics.setSingletonInstance(analytics);
    Analytics.with(this).track("Application Started");
  }
}
