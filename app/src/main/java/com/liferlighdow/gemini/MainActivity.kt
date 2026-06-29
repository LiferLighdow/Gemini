package com.liferlighdow.gemini

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri

class MainActivity : ComponentActivity() {

    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    private val fileChooserLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        filePathCallback?.onReceiveValue(uris.toTypedArray())
        filePathCallback = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GeminiWebViewScreen(
                onOpenFileChooser = { callback ->
                    filePathCallback = callback
                    fileChooserLauncher.launch("*/*")
                }
            )
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun GeminiWebViewScreen(onOpenFileChooser: (ValueCallback<Array<Uri>>) -> Unit) {
    var webView by remember { mutableStateOf<WebView?>(null) }
    var progress by remember { mutableIntStateOf(0) }
    var canGoBack by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var isRetrying by remember { mutableStateOf(false) }

    val density = LocalDensity.current
    val imeBottom = WindowInsets.ime.getBottom(density)
    var maxImeHeight by remember { mutableIntStateOf(0) }

    // 紀錄鍵盤出現過的最大高度
    if (imeBottom > maxImeHeight) {
        maxImeHeight = imeBottom
    }

    // 當偵測到鍵盤正在升起時（imeBottom > 0），
    // 立即使用 maxImeHeight 或當前高度，跳過中間的動畫過程。
    val instantImePadding = if (imeBottom > 0) {
        if (maxImeHeight > 0) maxImeHeight else imeBottom
    } else {
        0
    }

    BackHandler(enabled = canGoBack && !isError) {
        webView?.goBack()
    }

    val layoutDirection = androidx.compose.ui.platform.LocalLayoutDirection.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Black
    ) { paddingValues: PaddingValues ->
        val systemBottomPadding = paddingValues.calculateBottomPadding()
        val imeBottomPadding = with(density) { instantImePadding.toDp() }
        val finalBottomPadding = if (imeBottomPadding > systemBottomPadding) imeBottomPadding else systemBottomPadding

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(
                start = paddingValues.calculateStartPadding(layoutDirection),
                top = paddingValues.calculateTopPadding(),
                end = paddingValues.calculateEndPadding(layoutDirection),
                bottom = finalBottomPadding
            )
            .consumeWindowInsets(paddingValues)
            .background(Color.Black)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    WebView(ctx).apply {
                        setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)
                        
                        val cookieManager = CookieManager.getInstance()
                        cookieManager.setAcceptCookie(true)
                        cookieManager.setAcceptThirdPartyCookies(this, true)
                        
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        
                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                val url = request?.url?.toString() ?: return false
                                return if (url.contains("gemini.google.com") || url.contains("accounts.google.com") || url.contains("google.com/search")) {
                                    false
                                } else {
                                    ctx.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
                                    true
                                }
                            }

                            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                                // We don't reset isError here to keep the mask visible during retry
                            }

                            override fun onReceivedError(
                                view: WebView?,
                                request: WebResourceRequest?,
                                error: WebResourceError?
                            ) {
                                if (request?.isForMainFrame == true) {
                                    isError = true
                                    isRetrying = false
                                }
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                CookieManager.getInstance().flush()
                                
                                // If the page finished without a main-frame error, hide the error screen
                                if (isRetrying || !isError) {
                                    isError = false
                                    isRetrying = false
                                }

                                if (!isError) {
                                    canGoBack = view?.canGoBack() ?: false
                                    injectNativeBehaviors(view)
                                }
                            }
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onShowFileChooser(
                                webView: WebView?,
                                filePathCallback: ValueCallback<Array<Uri>>?,
                                fileChooserParams: FileChooserParams?
                            ): Boolean {
                                filePathCallback?.let { onOpenFileChooser(it) }
                                return true
                            }

                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                if (!isError || isRetrying) {
                                    progress = newProgress
                                }
                            }
                        }

                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            @Suppress("DEPRECATION")
                            databaseEnabled = true
                            cacheMode = WebSettings.LOAD_DEFAULT
                            useWideViewPort = true
                            loadWithOverviewMode = true
                            userAgentString = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
                            
                            val nightModeFlags = ctx.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    @Suppress("DEPRECATION")
                                    forceDark = WebSettings.FORCE_DARK_ON
                                }
                            }
                        }

                        loadUrl("https://gemini.google.com/")
                        webView = this
                    }
                },
                update = {
                    webView = it
                }
            )

            if (progress < 100 && !isError) {
                LinearProgressIndicator(
                    progress = { progress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = Color(0xFF4285F4),
                    trackColor = Color.Transparent,
                )
            }

            if (isError) {
                NoConnectionScreen(
                    isRetrying = isRetrying,
                    onRetry = {
                        isRetrying = true
                        progress = 0
                        webView?.reload()
                    }
                )
            }
        }
    }
}

@Composable
fun NoConnectionScreen(isRetrying: Boolean, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(80.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "No connection",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Please check your network and try again",
            color = Color.LightGray,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        if (isRetrying) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = Color(0xFF4285F4),
                strokeWidth = 3.dp
            )
        } else {
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4285F4)
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "Retry",
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

private fun injectNativeBehaviors(webView: WebView?) {
    val css = """
        * {
            -webkit-touch-callout: none;
            -webkit-user-select: none;
            user-select: none;
            -webkit-tap-highlight-color: transparent;
        }
        input, textarea, [contenteditable="true"] {
            -webkit-user-select: text;
            user-select: text;
        }
        body, html {
            margin: 0;
            padding: 0;
            height: 100%;
            overflow-x: hidden;
            overflow-y: auto;
            -webkit-overflow-scrolling: touch;
        }
    """.trimIndent().replace("\n", "")

    val script = """
        (function() {
            var parent = document.getElementsByTagName('head').item(0);
            var style = document.createElement('style');
            style.type = 'text/css';
            style.innerHTML = '$css';
            parent.appendChild(style);

            var meta = document.createElement('meta');
            meta.name = 'viewport';
            meta.content = 'width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no, viewport-fit=cover';
            document.getElementsByTagName('head')[0].appendChild(meta);
        })();
    """.trimIndent()
    webView?.evaluateJavascript(script, null)
}
