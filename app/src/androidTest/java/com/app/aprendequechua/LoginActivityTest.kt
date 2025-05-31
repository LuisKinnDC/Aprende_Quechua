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
        // Lanzar LoginActivity
        ActivityScenario.launch(LoginActivity::class.java)

        // Escribir email inválido
        onView(withId(R.id.editTxtName)).perform(replaceText("emailInvalido@gmail.com"))
        onView(withId(R.id.editTxtContrasena)).perform(replaceText("123456"))

        // Hacer clic en "Iniciar Sesión"
        onView(withId(R.id.buttonIniciarSesion)).perform(click())

        // Verificar mensaje de error en campo de email
        onView(withId(R.id.editTxtName))
            .check(matches(hasErrorText("Correo inválido")))
    }

    @Test
    fun login_camposVacios() {
        ActivityScenario.launch(LoginActivity::class.java)

        // Hacer clic sin llenar datos
        onView(withId(R.id.buttonIniciarSesion)).perform(click())

        // Verificar errores en ambos campos
        onView(withId(R.id.editTxtName))
            .check(matches(hasErrorText("Este campo es obligatorio")))

        onView(withId(R.id.editTxtContrasena))
            .check(matches(hasErrorText("Este campo es obligatorio")))
    }

    @Test
    fun click_recuperar_contrasena() {
        ActivityScenario.launch(LoginActivity::class.java)

        // Hacer clic en "¿Olvidaste tu contraseña?"
        onView(withId(R.id.txtForgotPassword)).perform(click())

        // Verificar que aparezca el diálogo (si está implementado)
        onView(withText("Recuperar Contraseña")).inRoot(isDialog()).check(matches(isDisplayed()))
    }

    @Test
    fun click_iniciar_con_google() {
        ActivityScenario.launch(LoginActivity::class.java)

        // Hacer clic en layout de inicio con Google
        onView(withId(R.id.layoutIniciarGoogle)).perform(click())

        // Aquí puedes verificar si se inicia GoogleSignIn
        // Por ahora, verificamos que no haya crash
        onView(withId(R.id.layoutIniciarGoogle)).check(matches(isDisplayed()))
    }
}