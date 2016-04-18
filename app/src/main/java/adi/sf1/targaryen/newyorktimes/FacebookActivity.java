package adi.sf1.targaryen.newyorktimes;

import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;

/**
 * Created by Raiders on 4/18/16.
 */
public class FacebookActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        //Initialize Facebook SDK
        FacebookSdk.sdkInitialize(getApplicationContext());
    }

    public void setFacebookContent(String url) {
        ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
          .setContentUrl(Uri.parse(url))
          .build();
    }


}
