package com.ibits.react_native_in_app_review;

import android.widget.Toast;

import androidx.annotation.NonNull;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;

public class AppReviewModule extends ReactContextBaseJavaModule {

    public AppReviewModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @NonNull
    @Override
    public String getName() {
        return "InAppReviewModule";
    }

    @ReactMethod
    public void show() {
        // TODO
        // These try-catch blocks are a lazy workaround
        //
        // It will be better if this code validates if the play store is installed and based on that returns meaningful
        // error codes (e.g. using the Promise RN module), so it gives feedback to the JS layer of what is going on
        // Some resources to review:
        // https://github.com/MinaSamir11/react-native-in-app-review/issues/15
        // https://reactnative.dev/docs/native-modules-android
        // https://github.com/KjellConnelly/react-native-rate/blob/master/android/src/main/java/com/reactnativerate/RNRateModule.java
        try {
            ReviewManager manager = ReviewManagerFactory.create(getReactApplicationContext());
            Task<ReviewInfo> request = manager.requestReviewFlow();

            request.addOnCompleteListener(task -> {
                try {
                    if (task.isSuccessful()) {
                        // We can get the ReviewInfo object
                        ReviewInfo reviewInfo = task.getResult();
                        Task<Void> flow = manager.launchReviewFlow(getCurrentActivity(), reviewInfo);

                        flow.addOnCompleteListener(taski -> {
                            try {
                                // The flow has finished. The API does not indicate whether the user
                                // reviewed or not, or even whether the review dialog was shown. Thus, no
                                // matter the result, we continue our app flow.
                                Log.e("Review isSuccessful",""+taski.isSuccessful());
                            } catch (Exception e) {
                                Log.e("RNInAppReview Error", "Error while displaying the RN In-AppReview Module (3)");
                            }
                        });

                    } else {
                        Log.e("Review Error",task.getResult().toString());
                    }
                } catch (Exception e) {
                    Log.e("RNInAppReview Error", "Error while displaying the RN In-AppReview Module (2)");
                }
            });
        } catch (Exception e) {
            Log.e("RNInAppReview Error", "Error while displaying the RN In-AppReview Module (1)");
        }
    }

}
