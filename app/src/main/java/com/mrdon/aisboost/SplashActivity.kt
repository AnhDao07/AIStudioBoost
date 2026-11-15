package com.mrdon.aisboost

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Shader
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import java.text.Normalizer

/** Màn hình mở đầu: gõ chữ từng ký tự "Một Ngàn Nỗi Nhớ", bên dưới là link FB chạy 7 sắc cầu vồng. */
class SplashActivity : Activity() {

    private val handler = Handler(Looper.getMainLooper())
    private val title = Normalizer.normalize("Một Ngàn Nỗi Nhớ", Normalizer.Form.NFC)
    private val link = "fb.com/troidongconang"
    private val typeDelayMs = 90L
    private val holdAfterMs = 1500L
    private var rainbowAnim: ValueAnimator? = null
    private var launched = false

    private lateinit var titleView: TextView
    private lateinit var linkView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        titleView = TextView(this).apply {
            setTextColor(Color.WHITE)
            textSize = 30f
            typeface = android.graphics.Typeface.MONOSPACE
            gravity = Gravity.CENTER
        }
        linkView = TextView(this).apply {
            textSize = 18f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setPadding(0, dp(18), 0, dp(18))
            setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://$link")))
            }
        }
        applyRainbow(linkView)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#0B0B12"))
            setPadding(dp(24), dp(24), dp(24), dp(24))
            layoutParams = ViewGroup.LayoutParams(-1, -1)
            addView(titleView, LinearLayout.LayoutParams(-2, -2))
            addView(linkView, LinearLayout.LayoutParams(-2, -2))
            // Chạm ra ngoài vùng link để bỏ qua splash
            setOnClickListener { goMain() }
        }
        setContentView(root)

        typeText(titleView, title, 0) {
            handler.postDelayed({
                typeText(linkView, link, 0) {
                    handler.postDelayed({ goMain() }, holdAfterMs)
                }
            }, 250)
        }
    }

    /** Gõ từng ký tự, kèm con trỏ nhấp nháy ở cuối khi đang gõ. */
    private fun typeText(tv: TextView, full: String, i: Int, onDone: () -> Unit) {
        if (isFinishing) return
        if (i > full.length) { tv.text = full; onDone(); return }
        tv.text = full.substring(0, i) + if (i < full.length) "▌" else ""
        handler.postDelayed({ typeText(tv, full, i + 1, onDone) }, typeDelayMs)
    }

    /** Dải gradient 7 sắc cầu vồng chạy ngang qua chữ. */
    private fun applyRainbow(tv: TextView) {
        val w = tv.paint.apply { textSize = tv.textSize; typeface = tv.typeface }
            .measureText(link).coerceAtLeast(1f)
        val colors = intArrayOf(
            Color.parseColor("#FF0000"), Color.parseColor("#FF7F00"), Color.parseColor("#FFFF00"),
            Color.parseColor("#00FF00"), Color.parseColor("#0000FF"), Color.parseColor("#4B0082"),
            Color.parseColor("#9400D3"), Color.parseColor("#FF0000")
        )
        val shader = LinearGradient(0f, 0f, w, 0f, colors, null, Shader.TileMode.REPEAT)
        tv.paint.shader = shader
        val m = Matrix()
        rainbowAnim = ValueAnimator.ofFloat(0f, w).apply {
            duration = 2500
            repeatCount = ValueAnimator.INFINITE
            interpolator = android.view.animation.LinearInterpolator()
            addUpdateListener {
                m.setTranslate(it.animatedValue as Float, 0f)
                shader.setLocalMatrix(m)
                tv.invalidate()
            }
            start()
        }
    }

    private fun goMain() {
        if (launched) return
        launched = true
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        rainbowAnim?.cancel()
        super.onDestroy()
    }
}
