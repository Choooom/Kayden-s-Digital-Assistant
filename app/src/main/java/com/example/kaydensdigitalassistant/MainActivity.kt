@file:Suppress("DEPRECATION")

package com.example.kaydensdigitalassistant

import android.graphics.drawable.Icon
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.room.util.TableInfo
import com.example.kaydensdigitalassistant.ui.theme.KaydensDigitalAssistantTheme
import com.example.kaydensdigitalassistant.ui.theme.Red
import org.osmdroid.config.Configuration
import java.io.File

class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        enableEdgeToEdge()

        // Initialize OSMDroid before setContent
        Configuration.getInstance().load(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )
        Configuration.getInstance().userAgentValue = applicationContext.packageName
        Configuration.getInstance().osmdroidTileCache = File(
            applicationContext.cacheDir,
            "tiles"
        )

        setContent {
            KaydensDigitalAssistantTheme {
                UserRoleProvider {
                    LocationProvider {
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
            }
        }

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                )
    }
}

@Composable
fun TopBar(navController: NavController, text: String) {
    val userRoleViewModel = LocalUserRoleViewModel.current
    val isAdmin by userRoleViewModel.isAdmin.collectAsState()

    var showProfile by remember { mutableStateOf(false) }

    val employeeViewModel = LocalEmployeeViewModel.current
    val currentEmployee by employeeViewModel.currentEmployee.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontFamily = kanit_bold,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            color = Color.White
        )
        Spacer(modifier = Modifier.fillMaxWidth(0.26f))
        Icon(
            painter = painterResource(id = R.drawable.face_man),
            contentDescription = "Profile",
            modifier = Modifier
                .size(70.dp)
                .padding(end = 15.dp)
                .clickable { showProfile = true }
        )
    }

    if (showProfile) {
        Dialog(onDismissRequest = { showProfile = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.face_man),
                            contentDescription = "Profile Picture",
                            modifier = Modifier.size(100.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        currentEmployee?.let { employee ->
                            Text(
                                text = if(isAdmin) "Dennis Maipid" else employee.name,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = if(isAdmin) "Blk 20, Lot 15 Delaware Street" else employee.emailAddress,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = if(isAdmin) "09986726194" else employee.contactNumber,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = if(isAdmin) "Birthday: December 1 1995" else "Birthday: ${employee.birthdate}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Button(
                        onClick = {
                            navController.navigate("login") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Red)
                    ) {
                        Text("LOG OUT")
                    }
                }
            }
        }
    }
}


