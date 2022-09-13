package com.parasoft.demoapp.component;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.hasTextColor;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import android.widget.EditText;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.parasoft.demoapp.LoginActivity;
import com.parasoft.demoapp.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class SettingDialogTest {

    SettingDialog settingDialog;

    EditText baseUrlInput;

    @Rule
    public IntentsTestRule<LoginActivity> intentsTestRule =
          new IntentsTestRule<>(LoginActivity.class);

    @Before
    public void setUp() {
        settingDialog = intentsTestRule.getActivity().getDialog();
        onView(withId(R.id.settingButton)).perform(click());

        baseUrlInput = settingDialog.getBaseUrlInput();

        onView(withId(R.id.base_url_error_message)).check(matches(withText("")));
        onView(withId(R.id.setting_dialog_title)).check(matches(withText(R.string.settings)));
        onView(withId(R.id.base_url_input)).check(matches(withHint(R.string.base_url)));
        onView(withId(R.id.save_button)).check(matches(hasTextColor(R.color.dark_blue)));
        onView(withId(R.id.save_button)).check(matches(withText(R.string.save)));
        onView(withId(R.id.dismiss_button)).check(matches(withText(R.string.cancel)));
    }

    @Test
    public void testInputIsValid() {
        baseUrlInput.setText("http://localhost:8081");
        onView(withId(R.id.save_button)).check(matches(hasTextColor(R.color.dark_blue)));
        onView(withId(R.id.base_url_error_message)).check(matches(withText("")));
        onView(withId(R.id.save_button)).perform(click());
        onView(withId(R.id.settingButton)).perform(click());
        onView(withId(R.id.base_url_input)).check(matches(withText("http://localhost:8081")));
    }

    @Test
    public void testInputIsInvalid() {
        baseUrlInput.setText("http");
        onView(withId(R.id.save_button)).check(matches(hasTextColor(R.color.button_disabled)));
        onView(withId(R.id.base_url_error_message)).check(matches(withText(R.string.invalid_url)));
        onView(withId(R.id.dismiss_button)).perform(click());
    }

    @Test
    public void testInputIsEmpty() {
        baseUrlInput.setText(null);
        onView(withId(R.id.save_button)).check(matches(hasTextColor(R.color.button_disabled)));
        onView(withId(R.id.base_url_error_message)).check(matches(withText(R.string.base_url_must_not_be_empty)));
        onView(withId(R.id.dismiss_button)).perform(click());
    }
}