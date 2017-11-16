package com.example.jubinjacob.twaingg

import java.util.Random
import java.util.*

/**
 * Created by jubinjacob on 13/11/17.
 */
enum class KnobState(val angle: Float) {
    TOP(0.0f), RIGHT(90.0f), BOTTOM(180.0f), LEFT(270.0f);
    fun next() : KnobState {
        when (this) {
            TOP->return RIGHT
            RIGHT->return BOTTOM
            BOTTOM->return LEFT
            LEFT->return TOP
        }
    }
    companion object {
        fun random() : KnobState {
            val random = Random().nextInt(4)
            when (random) {
                0->return KnobState.TOP
                1->return KnobState.RIGHT
                2->return KnobState.BOTTOM
                3->return KnobState.LEFT
                else->return KnobState.LEFT
            }
        }
    }
}