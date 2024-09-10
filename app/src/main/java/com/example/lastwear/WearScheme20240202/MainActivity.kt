/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.WearScheme20240202.presentation

import Cfull
import Coff
import ReadCoffFromFile
import WriteCoffToFile
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import com.example.lastwear.WearScheme20240202.Initialization
import com.example.lastwear.WearScheme20240202.OffSigncryption
import com.example.lastwear.WearScheme20240202.OnSigncryption
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.unit.TextUnit
import androidx.core.content.ContextCompat

/*
@Composable
fun MyApp() {
    val context = LocalContext.current
    val permission = Manifest.permission.MANAGE_EXTERNAL_STORAGE

    // 请求权限的Launcher
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // 权限被用户授予
        } else {
            // 权限被用户拒绝
        }
    }

    ManageExternalStoragePermissionButton(
        context = context,
        permission = permission,
        requestPermissionLauncher = requestPermissionLauncher
    )
}

@Composable
fun ManageExternalStoragePermissionButton(
    context: Context,
    permission: String,
    requestPermissionLauncher: ActivityResultLauncher<String>
) {
    Button(onClick = {
        // 检查权限是否已被授予
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            // 请求权限
            requestPermissionLauncher.launch(permission)
        } else {
            // 权限已被授予，可以执行相应操作
        }
    }) {
        Text("Request Manage External Storage Permission")
    }
}

*/


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        /********************

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
            Environment.isExternalStorageManager()) {
            // 权限已经授予，可以继续访问外部存储
        } else {
            // 权限未授予，需要请求
            if (shouldShowRequestPermissionRationale(Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {
                // 显示一些解释为什么需要这个权限的UI
            }

            ***********************/


        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)


        setContent {

            PairingDemo()

        }
    }
}



@Composable
fun PairingDemo() {

    // 创建一个状态变量，用于存储配对结果
    var pairingResult by remember { mutableStateOf("") }

    // 创建一个协程作用域，用于在可组合函数中执行协程
    val coroutineScope = rememberCoroutineScope()
    val stateVertical = rememberScrollState(0)
    var time1 by remember { mutableLongStateOf(0)    }
    var time2 by remember { mutableLongStateOf(0)    }
    var time3 by remember { mutableLongStateOf(0)    }
    var NumberOfCiphertext by remember { mutableLongStateOf(0)    }

    /*****************/
    //var gen = PairingFactory.getPairing("app/src/main/res/raw/curve")
    var gen = PairingFactory.getPairing("res/raw/curve")
    //p= Initialization(gen = gen)
    var p by remember {
        mutableStateOf ( Initialization(gen = gen) )
        //mutableStateOf ( ReadSenderParam() )
    }
    /***********

    val context = LocalContext.current
    val permission = Manifest.permission.MANAGE_EXTERNAL_STORAGE

    // 请求权限的Launcher
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            time1 = 1
        } else {
            time1 = 0// 权限被用户拒绝
        }
    }

     ******/

    // 创建一个垂直布局
    Column(
        modifier = Modifier
            .fillMaxSize()
            //.size(600.dp)
            //.height(900.dp)
            .verticalScroll(stateVertical),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        /*
        ManageExternalStoragePermissionButton(
            context = context,
            permission = permission,
            requestPermissionLauncher = requestPermissionLauncher
        )*/
/*
        Button(onClick = {
            //p=ReadInitializationFromFile()
            time1 = measureTimeMillis {
                p=ReadSenderParam()
               // WriteInitializationToFile(p)

            }
            //WriteInitializationToFile(p)
            //p=ReadInitializationFromFile()

        }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Initialization")
      //  +p.toString())

        }
*/
        /*Button(onClick = {
            var CoffTest : Coff = OffSigncryption(p)
        }, modifier = Modifier.padding(5.dp)) {
            Text(text = "Offline Phase Test")
        }*/
        Text(text = "Our Scheme",Modifier.height(50.dp) )
        Row(
            horizontalArrangement = Arrangement.Center,
            //modifier = Modifier.size(100.dp)
        )
        {
            var Coff : Coff
            var Cfull : Cfull

            Button(onClick = {
                time2 = measureTimeMillis {
                    Coff = OffSigncryption(p)
                    //WriteCoffToFile(Coff)
                }
               WriteCoffToFile(Coff)
            }, modifier = Modifier.padding(5.dp)) {
                Text(text = "Offline Phase")
            }


            Button(onClick = {
                Coff = ReadCoffFromFile(p.gen)
                //Coff = OffSigncryption(p)
                time3 = measureTimeMillis {
                    Cfull = OnSigncryption(p, Coff )
                    //WriteCfullToFile(Cfull)
                }
                //WriteCfullToFile(Cfull)
            }, modifier = Modifier.padding(5.dp)) {
                Text(text = "Online Phase")
            }

        }


        //Text(text = "Initialization 用时： ${time1.toString()} ms", modifier = Modifier.padding(3.dp))
        Text(text = "Offline 用时： ${time2.toString()} ms", modifier = Modifier.padding(3.dp))
        Text(text = "Online 用时： ${time3.toString()} ms", modifier = Modifier.padding(3.dp))
        var elapsedTime = 0L
        Button(onClick = {
            //p=ReadInitializationFromFile()
            var Coff = OffSigncryption(p)
            val startTime = System.currentTimeMillis()
            thread {
                elapsedTime = 0L
                while (elapsedTime < 60000) {
                    OnSigncryption(p, Coff )
                    NumberOfCiphertext++
                    elapsedTime = System.currentTimeMillis() - startTime
                    time1 = elapsedTime/1000
                }
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "start 1 min recording")
        }
        Text(text = "${NumberOfCiphertext.toString()} ciphertexts generated in ${time1}s", modifier = Modifier.padding(5.dp).size(200.dp))

        //Text(text = "p = ${p.toString()}", modifier = Modifier.padding(5.dp))


    }
}

/*
@Composable
fun ManageExternalStoragePermission() {
    val context = LocalContext.current
    val permission = android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
    val requestCode = 100

    if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(permission),
            requestCode
        )
    } else {
        // 权限已被授予，可以执行相应操作
    }
}

*/
