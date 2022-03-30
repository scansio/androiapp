package file.filefinder;

import android.app.*;
import android.content.res.Configuration;

public class App extends Application {
 @Override
  public void onCreate() {
    super.onCreate();
  }
   @Override
  public void onTerminate() {
        super.onTerminate();
    }
 
  @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
  @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }
     @Override
    public void onConfigurationChanged(Configuration c){
      super.onConfigurationChanged(c);
    }
}
