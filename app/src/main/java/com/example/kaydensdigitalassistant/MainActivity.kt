@file:Suppress("DEPRECATION")

package com.example.kaydensdigitalassistant

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import com.example.kaydensdigitalassistant.ui.theme.KaydensDigitalAssistantTheme

class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        enableEdgeToEdge()

        setContent {
            KaydensDigitalAssistantTheme {
                EmployeeDetailProvider {
                    AppProvider {
                        ProductsProvider {
                            ReceiptProvider {
                                CustomerDetailProvider {
                                    SalesProvider {
                                        MainScreen()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                )
    }
}


