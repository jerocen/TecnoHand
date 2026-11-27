package com.atom.tecnohandapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.atom.tecnohandapp.data.models.UserProfile
import com.atom.tecnohandapp.data.models.UserRole
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tecnohand_user_datastore")

class UserDataStore(private val context: Context)
{
    private val gson = Gson()

    companion object
    {
        private val KEY_REGISTERED_USERS = stringPreferencesKey("registered_users_json")
        private val KEY_LAST_LOGGED_USER_EMAIL = stringPreferencesKey("last_logged_user_email")

        private val DEFAULT_USERS = listOf(
            UserProfile(
                id = "RES-1001",
                email = "residente@tecnohand.com",
                password = "123456",
                nombres = "Pedro",
                apellidos = "Ramírez",
                nombre = "Pedro Ramírez",
                telefono = "+52 55 1234 5678",
                fechaNacimiento = "15-05-1990",
                rol = UserRole.RESIDENT,
                numeroVivienda = "101",
                vivienda = "Calle Roble #101",
                latitud = 19.432608,
                longitud = -99.133209,
                ubicacionDireccion = "Caseta Principal, Av. Los Pinos 100",
                cantidadResidentes = 3,
                colonia = "Colonia Los Pinos",
                integrantesFamilia = listOf("María Ramírez (Esposa)", "Lucas Ramírez (Hijo)", "Sofía Ramírez (Hija)")
            ),
            UserProfile(
                id = "GRD-9001",
                email = "guardia@tecnohand.com",
                password = "123456",
                nombres = "Oficial",
                apellidos = "Gómez",
                nombre = "Oficial Gómez",
                telefono = "+52 55 9876 5432",
                fechaNacimiento = "10-10-1985",
                rol = UserRole.GUARD,
                numeroVivienda = "GRD-01",
                vivienda = "Caseta de Vigilancia",
                latitud = 19.432608,
                longitud = -99.133209,
                ubicacionDireccion = "Caseta de Vigilancia Principal",
                cantidadResidentes = 1,
                colonia = "Colonia Los Pinos",
                integrantesFamilia = emptyList()
            )
        )
    }

    val registeredUsersFlow: Flow<List<UserProfile>> = context.dataStore.data.map { preferences ->
        val jsonStr = preferences[KEY_REGISTERED_USERS]
        if (jsonStr.isNullOrBlank())
        {
            DEFAULT_USERS
        }
        else
        {
            try
            {
                val type = object : TypeToken<List<UserProfile>>() {}.type
                val parsedUsers: List<UserProfile>? = gson.fromJson(jsonStr, type)
                if(parsedUsers.isNullOrEmpty()) DEFAULT_USERS else parsedUsers
            }
            catch(e: Exception)
            {
                DEFAULT_USERS
            }
        }
    }

    val lastLoggedUserEmailFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[KEY_LAST_LOGGED_USER_EMAIL]
    }

    suspend fun getRegisteredUsers(): List<UserProfile>
    {
        return registeredUsersFlow.first()
    }

    suspend fun saveRegisteredUsers(users: List<UserProfile>)
    {
        val jsonStr = gson.toJson(users)
        context.dataStore.edit { preferences ->
            preferences[KEY_REGISTERED_USERS] = jsonStr
        }
    }

    suspend fun registerUser(newProfile: UserProfile): Boolean
    {
        val currentUsers = getRegisteredUsers().toMutableList()
        // Check if email already registered
        val existingIndex = currentUsers.indexOfFirst { it.email.equals(newProfile.email, ignoreCase = true) }
        if(existingIndex >= 0)
        {
            currentUsers[existingIndex] = newProfile
        }
        else
        {
            currentUsers.add(newProfile)
        }
        saveRegisteredUsers(currentUsers)
        saveLastLoggedUserEmail(newProfile.email)
        return true
    }

    suspend fun updateUserProfile(updatedProfile: UserProfile)
    {
        val currentUsers = getRegisteredUsers().toMutableList()
        val index = currentUsers.indexOfFirst { it.email.equals(updatedProfile.email, ignoreCase = true) || it.id == updatedProfile.id }
        if(index >= 0)
        {
            currentUsers[index] = updatedProfile
            saveRegisteredUsers(currentUsers)
        }
    }

    suspend fun authenticateUser(emailInput: String, passwordInput: String): UserProfile?
    {
        val users = getRegisteredUsers()
        val trimmedEmail = emailInput.trim()
        val trimmedPassword = passwordInput.trim()

        val matchedUser = users.find { user ->
            (user.email.equals(trimmedEmail, ignoreCase = true) || user.id.equals(trimmedEmail, ignoreCase = true)) &&
                    (user.password == trimmedPassword || trimmedPassword.isBlank()) // If blank password in quick login or matches password
        }

        if(matchedUser != null)
        {
            saveLastLoggedUserEmail(matchedUser.email)
        }
        return matchedUser
    }

    suspend fun saveLastLoggedUserEmail(email: String?)
    {
        context.dataStore.edit { preferences ->
            if(email == null)
            {
                preferences.remove(KEY_LAST_LOGGED_USER_EMAIL)
            }
            else
            {
                preferences[KEY_LAST_LOGGED_USER_EMAIL] = email
            }
        }
    }

    suspend fun getActiveUser(): UserProfile?
    {
        val lastEmail = lastLoggedUserEmailFlow.first() ?: return null
        val users = getRegisteredUsers()
        return users.find { it.email.equals(lastEmail, ignoreCase = true) }
    }
}
