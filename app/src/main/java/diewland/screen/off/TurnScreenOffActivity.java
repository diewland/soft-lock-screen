package diewland.screen.off;

import util.LockScreenUtil;
import android.app.Activity;
import android.os.Bundle;

public class TurnScreenOffActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LockScreenUtil.run(this);
        
        finish();
    }
}