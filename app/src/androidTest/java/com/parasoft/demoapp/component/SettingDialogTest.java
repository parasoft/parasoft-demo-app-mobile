package com.parasoft.demoapp.component;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasTextColor;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.parasoft.demoapp.LoginActivity;
import com.parasoft.demoapp.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SettingDialogTest {

    private ActivityScenario<LoginActivity> loginActivityScenario;

    @Rule
    public ActivityScenarioRule<LoginActivity> loginActivityScenarioRule = new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void setUp() {
        loginActivityScenario = loginActivityScenarioRule.getScenario();

        onView(withId(R.id.setting_button)).perform(click());

        onView(withId(R.id.base_url_error_message)).check(matches(withText("")));
        onView(withId(R.id.setting_dialog_title)).check(matches(withText(R.string.settings)));
        onView(withId(R.id.base_url_input)).check(matches(withHint(R.string.base_url)));
        onView(withId(R.id.base_url_save_button)).check(matches(hasTextColor(R.color.dark_blue)));
        onView(withId(R.id.base_url_save_button)).check(matches(withText(R.string.save)));
        onView(withId(R.id.base_url_dismiss_button)).check(matches(withText(R.string.cancel)));
    }

    @After
    public void tearDown() {
        loginActivityScenario.close();
        loginActivityScenario = null;
    }

    @Test
    public void test_InputIsValid() {
        onView(withId(R.id.base_url_input)).perform(clearText(), typeText("http://localhost:8081"), closeSoftKeyboard());
        onView(withId(R.id.base_url_save_button)).check(matches(hasTextColor(R.color.dark_blue)));
        onView(withId(R.id.base_url_error_message)).check(matches(withText("")));
        onView(withId(R.id.base_url_save_button)).perform(click());
        onView(withId(R.id.setting_button)).perform(click());
        onView(withId(R.id.base_url_input)).check(matches(withText("http://localhost:8081")));
    }

    @Test
    public void test_InputIsInvalid() {
        onView(withId(R.id.base_url_input)).perform(clearText(), typeText("http"), closeSoftKeyboard());
        onView(withId(R.id.base_url_save_button)).check(matches(hasTextColor(R.color.button_disabled)));
        onView(withId(R.id.base_url_error_message)).check(matches(withText(R.string.invalid_url)));
        onView(withId(R.id.base_url_dismiss_button)).perform(click());
    }

    @Test
    public void test_InputIsEmpty() {
        onView(withId(R.id.base_url_input)).perform(clearText(), typeText(""), closeSoftKeyboard());
        onView(withId(R.id.base_url_save_button)).check(matches(hasTextColor(R.color.button_disabled)));
        onView(withId(R.id.base_url_error_message)).check(matches(withText(R.string.base_url_must_not_be_empty)));
        onView(withId(R.id.base_url_dismiss_button)).perform(click());
    }
}