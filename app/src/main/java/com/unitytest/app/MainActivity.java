package com.unitytest.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.unity3d.mediation.LevelPlay;
import com.unity3d.mediation.LevelPlayAdError;
import com.unity3d.mediation.LevelPlayAdInfo;
import com.unity3d.mediation.LevelPlayConfiguration;
import com.unity3d.mediation.LevelPlayInitError;
import com.unity3d.mediation.LevelPlayInitListener;
import com.unity3d.mediation.LevelPlayInitRequest;
import com.unity3d.mediation.interstitial.LevelPlayInterstitialAd;
import com.unity3d.mediation.interstitial.LevelPlayInterstitialAdListener;

public class MainActivity extends Activity {

    private static final String TAG = "LevelPlayTest";

    private static final String APP_KEY = "28269591d";
    private static final String INTERSTITIAL_AD_UNIT_ID = "5gxrq7d2t6kz7rtp";

    private TextView statusText;
    private Button loadButton;
    private Button showButton;

    private LevelPlayInterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createUI();
        initializeLevelPlay();
    }

    private void createUI() {

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 60, 40, 40);

        statusText = new TextView(this);
        statusText.setText("جاري تهيئة LevelPlay...");
        statusText.setTextSize(18);

        loadButton = new Button(this);
        loadButton.setText("تحميل الإعلان");
        loadButton.setEnabled(false);

        showButton = new Button(this);
        showButton.setText("عرض الإعلان");
        showButton.setEnabled(false);

        layout.addView(statusText);
        layout.addView(loadButton);
        layout.addView(showButton);

        setContentView(layout);

        loadButton.setOnClickListener(v -> loadInterstitial());

        showButton.setOnClickListener(v -> showInterstitial());
    }

    private void initializeLevelPlay() {

        statusText.setText("جاري تهيئة LevelPlay...");

        LevelPlayInitRequest initRequest =
                new LevelPlayInitRequest.Builder(APP_KEY)
                        .build();

        LevelPlayInitListener initListener = new LevelPlayInitListener() {

            @Override
            public void onInitFailed(@NonNull LevelPlayInitError error) {

                Log.e(
                        TAG,
                        "LevelPlay init failed: " + error.getErrorMessage()
                );

                runOnUiThread(() -> {

                    statusText.setText(
                            "فشل تهيئة LevelPlay:\n" + error.getErrorMessage()
                    );

                    loadButton.setEnabled(false);
                    showButton.setEnabled(false);
                });
            }

            @Override
            public void onInitSuccess(LevelPlayConfiguration configuration) {

                Log.d(TAG, "LevelPlay init success");

                runOnUiThread(() -> {

                    statusText.setText("تمت تهيئة LevelPlay بنجاح");

                    loadButton.setEnabled(true);
                });

                createInterstitialAd();
            }
        };

        LevelPlay.init(this, initRequest, initListener);
    }

    private void createInterstitialAd() {

        interstitialAd = new LevelPlayInterstitialAd(INTERSTITIAL_AD_UNIT_ID);

        interstitialAd.setListener(new LevelPlayInterstitialAdListener() {

            @Override
            public void onAdLoaded(@NonNull LevelPlayAdInfo adInfo) {

                Log.d(TAG, "Interstitial loaded successfully");

                runOnUiThread(() -> {

                    statusText.setText("تم تحميل الإعلان بنجاح");

                    showButton.setEnabled(true);
                    loadButton.setEnabled(true);
                });
            }

            @Override
            public void onAdLoadFailed(@NonNull LevelPlayAdError error) {

                Log.e(
                        TAG,
                        "Interstitial load failed: " + error.getErrorMessage()
                );

                runOnUiThread(() -> {

                    statusText.setText(
                            "فشل تحميل الإعلان:\n" + error.getErrorMessage()
                    );

                    showButton.setEnabled(false);
                    loadButton.setEnabled(true);
                });
            }

            @Override
            public void onAdDisplayed(@NonNull LevelPlayAdInfo adInfo) {

                Log.d(TAG, "Interstitial displayed");

                runOnUiThread(() ->
                        statusText.setText("تم عرض الإعلان")
                );
            }

            @Override
            public void onAdDisplayFailed(
                    @NonNull LevelPlayAdError error,
                    @NonNull LevelPlayAdInfo adInfo) {

                Log.e(
                        TAG,
                        "Interstitial display failed: " + error.getErrorMessage()
                );

                runOnUiThread(() -> {

                    statusText.setText(
                            "فشل عرض الإعلان:\n" + error.getErrorMessage()
                    );

                    loadButton.setEnabled(true);
                    showButton.setEnabled(false);
                });
            }

            @Override
            public void onAdClicked(@NonNull LevelPlayAdInfo adInfo) {
                Log.d(TAG, "Interstitial clicked");
            }

            @Override
            public void onAdClosed(@NonNull LevelPlayAdInfo adInfo) {

                Log.d(TAG, "Interstitial closed");

                runOnUiThread(() -> {

                    statusText.setText("تم إغلاق الإعلان");

                    loadButton.setEnabled(true);
                    showButton.setEnabled(false);
                });
            }

            @Override
            public void onAdInfoChanged(@NonNull LevelPlayAdInfo adInfo) {
                // اختياري: بيتحدث لما يتوفر إعلان بديل بسعر أعلى
            }
        });
    }

    private void loadInterstitial() {

        if (interstitialAd == null) {

            statusText.setText("LevelPlay غير جاهز بعد");
            return;
        }

        loadButton.setEnabled(false);
        showButton.setEnabled(false);

        statusText.setText("جاري تحميل الإعلان...");

        interstitialAd.loadAd();
    }

    private void showInterstitial() {

        if (interstitialAd == null || !interstitialAd.isAdReady()) {

            statusText.setText("لا يوجد إعلان جاهز للعرض");
            return;
        }

        showButton.setEnabled(false);
        loadButton.setEnabled(false);

        statusText.setText("جاري عرض الإعلان...");

        interstitialAd.showAd(this);
    }
}
