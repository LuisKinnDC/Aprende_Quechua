package com.app.aprendequechua
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.app.aprendequechua.activitys.LoginActivity
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @Test
    fun login_con_email_invalido() {

        ActivityScenario.launch(LoginActivity::class.java)


        onView(withId(R.id.editTxtName)).perform(replaceText("emailInvalido@gmail.com"))
        onView(withId(R.id.editTxtContrasena)).perform(replaceText("123456"))


        onView(withId(R.id.buttonIniciarSesion)).perform(click())


        onView(withId(R.id.editTxtName))
            .check(matches(hasErrorText("Correo inválido")))
    }

    @Test
    fun login_camposVacios() {
        ActivityScenario.launch(LoginActivity::class.java)


        onView(withId(R.id.buttonIniciarSesion)).perform(click())


        onView(withId(R.id.editTxtName))
            .check(matches(hasErrorText("Este campo es obligatorio")))

        onView(withId(R.id.editTxtContrasena))
            .check(matches(hasErrorText("Este campo es obligatorio")))
    }

    @Test
    fun click_recuperar_contrasena() {
        ActivityScenario.launch(LoginActivity::class.java)


        onView(withId(R.id.txtForgotPassword)).perform(click())


        onView(withText("Recuperar Contraseña")).inRoot(isDialog()).check(matches(isDisplayed()))
    }

    @Test
    fun click_iniciar_con_google() {
        ActivityScenario.launch(LoginActivity::class.java)


        onView(withId(R.id.layoutIniciarGoogle)).perform(click())


        onView(withId(R.id.layoutIniciarGoogle)).check(matches(isDisplayed()))
    }
}