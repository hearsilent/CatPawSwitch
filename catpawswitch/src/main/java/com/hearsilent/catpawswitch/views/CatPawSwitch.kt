package com.hearsilent.catpawswitch.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.materialswitch.MaterialSwitch
import com.hearsilent.catpawswitch.R

class CatPawSwitch @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.materialSwitchStyle,
) : MaterialSwitch(context, attrs, defStyleAttr) {

    private var currentFrame = 0
    private var isAnimating = false
    private var elbowInFront = false

    // Preloaded frames for body (behind) and paw (in front)
    private val bodyFrames: Array<Drawable?> by lazy {
        arrayOf(
            ContextCompat.getDrawable(context, R.drawable.ic_cat_arm_body_1)?.mutate(),
            ContextCompat.getDrawable(context, R.drawable.ic_cat_arm_body_2)?.mutate(),
            ContextCompat.getDrawable(context, R.drawable.ic_cat_arm_body_3)?.mutate(),
            ContextCompat.getDrawable(context, R.drawable.ic_cat_arm_body_4)?.mutate(),
            ContextCompat.getDrawable(context, R.drawable.ic_cat_arm_body_5)?.mutate(),
        )
    }

    private val pawFrames: Array<Drawable?> by lazy {
        arrayOf(
            ContextCompat.getDrawable(context, R.drawable.ic_cat_arm_paw_1)?.mutate(),
            ContextCompat.getDrawable(context, R.drawable.ic_cat_arm_paw_2)?.mutate(),
            ContextCompat.getDrawable(context, R.drawable.ic_cat_arm_paw_3)?.mutate(),
            ContextCompat.getDrawable(context, R.drawable.ic_cat_arm_paw_4)?.mutate(),
            ContextCompat.getDrawable(context, R.drawable.ic_cat_arm_paw_5)?.mutate(),
        )
    }

    // Callback when toggle should be turned off
    var onPushToggle: (() -> Unit)? = null

    // Callback when animation finishes
    var onAnimationEnd: (() -> Unit)? = null

    override fun onDraw(canvas: Canvas) {
        if (isAnimating) {
            val bodyDrawable = bodyFrames.getOrNull(currentFrame)
            val pawDrawable = pawFrames.getOrNull(currentFrame)

            // Calculate arm dimensions: same height as switch, width = height * 640/155
            val armHeight = height
            val armWidth = (armHeight * 640f / 155f).toInt()

            // Center the arm on the switch
            val left = (width - armWidth) / 2
            val top = 0

            bodyDrawable?.setBounds(left, top, left + armWidth, top + armHeight)
            pawDrawable?.setBounds(left, top, left + armWidth, top + armHeight)

            // The body component is always drawn behind the switch
            bodyDrawable?.draw(canvas)

            if (!elbowInFront) {
                // The elbow/paw component is also behind the switch
                pawDrawable?.draw(canvas)
                super.onDraw(canvas)
            } else {
                // The elbow/paw component is in front of the switch
                super.onDraw(canvas)
                pawDrawable?.draw(canvas)
            }
        } else {
            super.onDraw(canvas)
        }
    }

    fun startPawAnimation() {
        if (isAnimating) return
        isAnimating = true
        currentFrame = 0
        elbowInFront = false

        // Disable clipping on parents so arm extends beyond switch bounds
        disableParentClipping()

        // Animation timing from reference (toggle-proactive.vue):
        // IN:  1→2 (200ms) → 2→3 (50ms) → 3→4 (50ms) → toggle + 4→5 (200ms)
        // OUT: 5→4 (100ms) → 4→3 (60ms) → 3→2 (60ms) → 2→1 (100ms)
        var t = 0L

        // Frame 2: arm extends, elbow comes in front (z-index: 1 in reference)
        postDelayed({
            currentFrame = 1
            elbowInFront = true
            invalidate()
        }, t)
        t += 200L

        // Frame 3: arm bends
        postDelayed({
            currentFrame = 2
            invalidate()
        }, t)
        t += 50L

        // Frame 4: arm curves down
        postDelayed({
            currentFrame = 3
            invalidate()
        }, t)
        t += 50L

        // Frame 5: push! Toggle OFF simultaneously
        postDelayed({
            currentFrame = 4
            onPushToggle?.invoke()
            invalidate()
        }, t)
        t += 200L

        // OUT: Frame 4
        postDelayed({
            currentFrame = 3
            invalidate()
        }, t)
        t += 100L

        // OUT: Frame 3
        postDelayed({
            currentFrame = 2
            invalidate()
        }, t)
        t += 60L

        // OUT: Frame 2 — elbow goes behind (z-index: 0 in reference)
        postDelayed({
            currentFrame = 1
            elbowInFront = false
            invalidate()
        }, t)
        t += 60L

        // OUT: Frame 1
        postDelayed({
            currentFrame = 0
            invalidate()
        }, t)
        t += 100L

        // Cleanup
        postDelayed({
            isAnimating = false
            elbowInFront = false
            restoreParentClipping()
            onAnimationEnd?.invoke()
            invalidate()
        }, t)
    }

    private var savedParentClipChildren: Boolean? = null
    private var savedGrandParentClipChildren: Boolean? = null

    private fun disableParentClipping() {
        (parent as? ViewGroup)?.let {
            savedParentClipChildren = it.clipChildren
            it.clipChildren = false
            it.clipToPadding = false
            (it.parent as? ViewGroup)?.let { gp ->
                savedGrandParentClipChildren = gp.clipChildren
                gp.clipChildren = false
                gp.clipToPadding = false
            }
        }
    }

    private fun restoreParentClipping() {
        (parent as? ViewGroup)?.let {
            savedParentClipChildren?.let { saved -> it.clipChildren = saved }
            it.clipToPadding = true
            (it.parent as? ViewGroup)?.let { gp ->
                savedGrandParentClipChildren?.let { saved -> gp.clipChildren = saved }
                gp.clipToPadding = true
            }
        }
        savedParentClipChildren = null
        savedGrandParentClipChildren = null
    }
}