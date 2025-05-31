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

        // Navegar desde SplashActivity hasta Register
        onView(withId(R.id.txtIniciarAhora)).perform(click())
        onView(withId(R.id.btnCrear)).perform(click())

        // Llenar formulario
        onView(withId(R.id.editTextName)).perform(typeText("Juan"))
        onView(withId(R.id.editTextEmail)).perform(typeText("jjan101@gmail.com"))
        onView(withId(R.id.editTextContrasena)).perform(typeText("123456"))
        onView(withId(R.id.editTextConfContrasena)).perform(typeText("123456"))
        closeSoftKeyboard()

        // Registrar
        onView(withId(R.id.buttonRegistro)).perform(click())

        // Verificar que estamos en DashboardActivity
        //intended(hasComponent(DashboardActivity::class.java.name))

        // Esperar a que el fragmento cargue
       Thread.sleep(3000)

        // Validar saludo
        onView(withId(R.id.txtSaludo)).check(matches(withText("Hola, Buenos días!")))
    }

}