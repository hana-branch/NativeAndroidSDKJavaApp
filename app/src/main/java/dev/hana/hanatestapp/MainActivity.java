package dev.hana.hanatestapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.json.JSONObject;

import java.util.Calendar;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.SharingHelper;
import io.branch.referral.util.BranchEvent;
import io.branch.referral.util.ContentMetadata;
import io.branch.referral.util.LinkProperties;
import io.branch.referral.util.ShareSheetStyle;


public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    public BranchUniversalObject buo;
    public LinkProperties lp;
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buo = createBranchUniversalObject();
        lp = createLinkProperties();
        preferences= getSharedPreferences("preferences", MODE_PRIVATE);
    }

    @Override
    public void onStart() {
        super.onStart();
        String approved = preferences.getString("approved", "");
        if (approved == "") {
            showTrackingDialog(this);
        }
        if (approved != "") {
            branchSDKInitialize(approved, this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent != null &&
                intent.hasExtra("branch_force_new_session") &&
                intent.getBooleanExtra("branch_force_new_session", false)) {
            Branch.sessionBuilder(this).withCallback(branchReferralInitListener).reInit();
        }
    }

    public void showTrackingDialog(Activity currentActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
        builder.setMessage(R.string.dialog_message)
                .setTitle(R.string.dialog_title)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("approved", "true");
                        branchSDKInitialize("true", currentActivity);
                        editor.commit();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("approved", "false");
                        branchSDKInitialize("false", currentActivity);
                        editor.commit();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void branchSDKInitialize(String approved, Activity currentActivity) {
        if (approved == "") {
            String preferencesApproved = preferences.getString("approved", "null");
            if (preferencesApproved != "") {
                Branch.getInstance().disableTracking(!Boolean.parseBoolean(preferencesApproved));
                Branch.sessionBuilder(currentActivity).withCallback(branchReferralInitListener)
                        .withData(getIntent() != null ? getIntent().getData() : null).init();
            }
        }
        else {
            Branch.getInstance().disableTracking(!Boolean.parseBoolean(approved));
            Branch.sessionBuilder(currentActivity).withCallback(branchReferralInitListener)
                    .withData(getIntent() != null ? getIntent().getData() : null).init();
        }

    }

    private Branch.BranchReferralInitListener branchReferralInitListener =
            new Branch.BranchReferralInitListener() {
                @Override public void onInitFinished(JSONObject referringParams, BranchError error) {
                    if (error != null) {
                        Log.e("BranchSDK", "branch init failed. Caused by -" + error.getMessage());
                    } else {
                        Log.i("BranchSDK", referringParams.toString());
                    }
                }
            };

    private Branch.BranchUniversalReferralInitListener branchUniversalReferralInitListener =
            new Branch.BranchUniversalReferralInitListener() {
                @Override public void onInitFinished(BranchUniversalObject buo, LinkProperties lp, BranchError error) {
                    if (error != null) {
                        Log.e("BranchSDK", "branch init failed. Caused by -" + error.getMessage());
                    } else {
                        if (buo != null) {
                            Log.i("BranchSDK", "Branch BUO :" + buo.getContentMetadata().convertToJson());
                        }
                        if (lp != null) {
                            Log.i("BranchSDK", "Branch LinkProperties : " + lp.getFeature());
                        }
                    }
                }
            };

    /** Called when the user taps the Send button */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);

        startActivity(intent);
    }

    /** Called when the user taps the click Link button */
    public void clickLink(View view) {
        Intent intent = new Intent(this, DisplayContentActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the Login button */
    public void userLogIn(View view) {
        Branch.getInstance().setIdentity("!23", branchReferralSetIdentityListener);
    }

    /** Called when the user taps the Logout button */
    public void userLogOut(View view) {
        try {
           new BranchEvent("ATTEMPT_LOGOUT")
                    .logEvent(MainActivity.this);
        } catch (Exception e) {
            Log.e("BranchSDK Error", "Branch Custom Event Failed  by - " + String.valueOf(e));
        }
        Branch.getInstance().logout(branchLogoutStatusListener);
    }

    private Branch.BranchReferralInitListener branchReferralSetIdentityListener =
            new Branch.BranchReferralInitListener() {
                @Override public void onInitFinished(JSONObject referringParams, BranchError error) {
                    if (error != null) {
                        Log.e("BranchSDK", "Branch set Identity failed. Caused by -" + error.getMessage());
                    } else {
                        Log.i("BranchSDK", "LoginFinished :" + referringParams.toString());
                        new BranchEvent("LOGIN").logEvent(MainActivity.this);
                    }
                }
            };

    private Branch.LogoutStatusListener branchLogoutStatusListener =
        new Branch.LogoutStatusListener() {
            @Override
            public void onLogoutFinished(boolean loggedOut, BranchError error) {
                if (error != null) {
                    Log.e("BranchSDK", "onLogoutFinished :" + loggedOut + " errorMessage " + error);
                }
                Log.i("BranchSDK", "onLogoutFinished :" + loggedOut);
            }
        };

    /** Called when the user taps the Link Create button */
    public void createLink(View view) {
        BranchUniversalObject buo = new BranchUniversalObject()
                .setCanonicalIdentifier("content/12345")
                .setContentImageUrl("https://lorempixel.com/400/400")
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setLocalIndexMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setContentMetadata(new ContentMetadata().addCustomMetadata("key1", "value1"));

        String userName = "branch1";

        LinkProperties lp = new LinkProperties()
                .setFeature("SHARE")
                .setCampaign("SHARE_MY_PAGE")
                .addControlParameter("$deeplink_path", "my_page/" + userName)
                .addControlParameter("custom", "data")
                .addControlParameter("custom_random", Long.toString(Calendar.getInstance().getTimeInMillis()));

        buo.generateShortUrl(this, lp, new Branch.BranchLinkCreateListener() {
            @Override
            public void onLinkCreate(String url, BranchError error) {
                if (error == null) {
                    Log.i("BRANCH SDK", "got my Branch link to share: " + url);
                }
            }
        });
    }

    /** Called when the user taps the Share button */
    public void shareLink(View view) {
        ShareSheetStyle ss = new ShareSheetStyle(MainActivity.this, "Check this out!", "This stuff is awesome: ")
                .setCopyUrlStyle(ContextCompat.getDrawable(this, android.R.drawable.ic_menu_send), "Copy", "Added to clipboard")
                .setMoreOptionStyle(ContextCompat.getDrawable(this, android.R.drawable.ic_menu_search), "Show more")
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.EMAIL)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.MESSAGE)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.HANGOUT)
                .setAsFullWidthStyle(true)
                .setSharingTitle("Share With");

        buo.showShareSheet(this, lp,  ss,  new Branch.BranchLinkShareListener() {
            @Override
            public void onShareLinkDialogLaunched() {
            }
            @Override
            public void onShareLinkDialogDismissed() {
            }
            @Override
            public void onLinkShareResponse(String sharedLink, String sharedChannel, BranchError error) {
                if (error == null) {
                    Log.i("BRANCH SDK Shared Link", "got my Branch link to share: " + sharedLink);
                }

            }
            @Override
            public void onChannelSelected(String channelName) {
            }
        });
    }

    private BranchUniversalObject createBranchUniversalObject() {
        BranchUniversalObject buo = new BranchUniversalObject()
                .setCanonicalIdentifier("content/12345")
                .setContentImageUrl("https://lorempixel.com/400/400")
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setLocalIndexMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setContentMetadata(new ContentMetadata().addCustomMetadata("key1", "value1"));

        return buo;
    }

    private LinkProperties createLinkProperties() {
        String userName = "branch1";

        LinkProperties lp = new LinkProperties()
                .setChannel(null)
                .setFeature("SHARE")
                .setCampaign("SHARE_MY_PAGE")
                .addControlParameter("$deeplink_path", "my_page/" + userName)
                .addControlParameter("custom", "data")
                .addControlParameter("custom_random", Long.toString(Calendar.getInstance().getTimeInMillis()));

        return lp;
    }
}
