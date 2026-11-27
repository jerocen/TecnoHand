package com.atom.tecnohandapp.data.local

import android.content.Context
import android.content.SharedPreferences
import com.atom.tecnohandapp.data.models.AppLanguage
import com.atom.tecnohandapp.data.models.AppTheme

class PreferencesManager(context: Context)
{
    private val prefs: SharedPreferences = context.getSharedPreferences("tecnohand_preferences", Context.MODE_PRIVATE)

    companion object
    {
        private const val KEY_SERVER_URL = "server_url"
        private const val KEY_THEME = "app_theme"
        private const val KEY_LANGUAGE = "app_language"
        private const val KEY_OFFLINE_DEMO = "offline_demo_mode"
        //private const val DEFAULT_URL = "http://10.0.2.2:3000/api/"
        //private const val DEFAULT_URL = "https://flatfoot-underling-slick.ngrok-free.dev/api/"
        private const val DEFAULT_URL = "https://api-tecnohand.onrender.com/api/"
    }

    fun getServerUrl(): String
    {
        val saved = prefs.getString(KEY_SERVER_URL, DEFAULT_URL) ?: DEFAULT_URL
        //return prefs.getString(KEY_SERVER_URL, DEFAULT_URL) ?: DEFAULT_URL
        return if(saved.contains("ngrok") || saved.contains("10.0.2.2"))
        {
            DEFAULT_URL
        }
        else
        {
            saved
        }
    }

    fun setServerUrl(url: String)
    {
        prefs.edit().putString(KEY_SERVER_URL, url).apply()
    }

    fun getTheme(): AppTheme
    {
        val themeStr = prefs.getString(KEY_THEME, AppTheme.SYSTEM.name)
        return try
        {
            AppTheme.valueOf(themeStr ?: AppTheme.SYSTEM.name)
        }
        catch(e: Exception)
        {
            AppTheme.SYSTEM
        }
    }

    fun setTheme(theme: AppTheme)
    {
        prefs.edit().putString(KEY_THEME, theme.name).apply()
    }

    fun getLanguage(): AppLanguage
    {
        val langStr = prefs.getString(KEY_LANGUAGE, AppLanguage.SPANISH.name)
        return try
        {
            AppLanguage.valueOf(langStr ?: AppLanguage.SPANISH.name)
        }
        catch(e: Exception)
        {
            AppLanguage.SPANISH
        }
    }

    fun setLanguage(language: AppLanguage)
    {
        prefs.edit().putString(KEY_LANGUAGE, language.name).apply()
    }

    fun isOfflineDemoMode(): Boolean
    {
        return prefs.getBoolean(KEY_OFFLINE_DEMO, false)
    }

    fun setOfflineDemoMode(enabled: Boolean)
    {
        prefs.edit().putBoolean(KEY_OFFLINE_DEMO, enabled).apply()
    }
}
