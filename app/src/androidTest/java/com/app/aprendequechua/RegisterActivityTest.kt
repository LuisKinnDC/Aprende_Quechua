package com.app.aprendequechua

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.intent.rule.IntentsTestRule
import com.app.aprendequechua.activitys.SplashActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterActivityTest {

    @get:Rule
    val intentsTestRule = IntentsTestRule(SplashActivity::class.java)

    @Test
    fun alRegistrarUsuario() {

        onView(withId(R.id.txtIniciarAhora)).perform(click())

        onView(withId(R.id.btnCrear)).perform(click())


        onView(withId(R.id.editTextName)).perform(typeText("Juan"), closeSoftKeyboard())
        onView(withId(R.id.editTextEmail)).perform(typeText("jan101@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.editTextContrasena)).perform(typeText("123456"), closeSoftKeyboard())
        onView(withId(R.id.editTextConfContrasena)).perform(typeText("123456"), closeSoftKeyboard())
        closeSoftKeyboard()


        onView(withId(R.id.buttonRegistro)).perform(click())

        Thread.sleep(5000)


        onView(withId(R.id.txtSaludo)).check(matches(isDisplayed()))
        onView(withId(R.id.txtSaludo)).check(matches(withText("Hola, Buenos días!")))
    }
}