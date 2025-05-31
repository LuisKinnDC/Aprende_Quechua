package com.app.aprendequechua

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.app.aprendequechua.activitys.SplashActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BienvenidaTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(SplashActivity::class.java)

    @Test
    fun alHacerClickEnComenzar_navegaALoginORegistro(){

        onView(withId(R.id.txtIniciarAhora)).check(matches(isDisplayed()))


        onView(withId(R.id.txtIniciarAhora)).perform(click())


        onView(withId(R.id.btnEntrar)).check(matches(isDisplayed()))
    }

}