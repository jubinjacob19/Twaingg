package com.example.jubinjacob.twaingg

/**
 * Created by jubinjacob on 10/11/17.
 */

import android.graphics.drawable.GradientDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import java.util.Random
import io.reactivex.subjects.PublishSubject

data class RotateEvent(val position: Int, val state : KnobState, val color : Int)

data class ColorChangeEvent(val position: Int, val color : Int)

class KnobGridAdaptor(val rows : Int, val columns : Int) : RecyclerView.Adapter<KnobGridAdaptor.KnobViewHolder>() {
    val moveCounterEmitter = PublishSubject.create<Int>()
    private var moveCount = 0
    override fun onBindViewHolder(holder: KnobViewHolder?, position: Int) {
        holder?.bind(columns=columns,onClick = {
            moveCount += 1
            moveCounterEmitter.onNext(moveCount)
        })
    }

    fun reset() {
        moveCount = 0
        moveCounterEmitter.onNext(moveCount)
    }

    override fun getItemCount(): Int {
        return columns*rows
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): KnobViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.cell_layout, parent, false)
        return KnobGridAdaptor.KnobViewHolder(itemView = v)
    }


    class KnobViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var knobState = KnobState.TOP
        var color : Int = 0

        fun bind(columns: Int, onClick:()->Unit) {
            setRandomColor()
            setRandomState()
            listenToNeighbours(columns= columns)
            itemView.setOnClickListener{
                incrementState()
                onClick()
            }
        }

        fun incrementState() {
            val newState = knobState.next()
            var degree = newState.angle-knobState.angle
            if (degree<0) {
                degree += 360
            }
            rotateBy(degree = degree, newState = newState, completion = {
                RxBus.publish(RotateEvent(position = adapterPosition, state = knobState, color = color))
            })
        }

        fun listenToNeighbours(columns:Int) {
            RxBus.listen(RotateEvent::class.java).subscribe {
                if (itemView.isEnabled) {
                    val currentPosition = adapterPosition
                    when (it.position) {
                        currentPosition-1->{
                            if (currentPosition%columns != 0 && isHorizontallyInline(left = it.state , right = knobState)) {
                                incrementState()
                                setBgColor(bgColor = it.color)
                            }
                        }
                        currentPosition+1-> {
                            if (it.position%columns != 0 && isHorizontallyInline(left = knobState, right = it.state)) {
                                incrementState()
                                setBgColor(bgColor = it.color)
                            }
                        }
                        currentPosition-columns ->  {
                            if (isVerticallyInline(bottom = knobState, top = it.state)) {
                                incrementState()
                                setBgColor(bgColor = it.color)
                            }
                        }
                        currentPosition+columns ->  {
                            if (isVerticallyInline(bottom = it.state, top = knobState)) {
                                incrementState()
                                setBgColor(bgColor = it.color)
                            }
                        }
                    }
                }
            }
        }

        fun rotateBy(degree:Float, newState: KnobState, completion: () -> Unit) {
            itemView.isEnabled = false
            itemView.animate()
                    .rotationBy(degree)
                    .setDuration(300)
                    .setInterpolator(LinearInterpolator())
                    .withEndAction {
                        knobState = newState
                        completion()
                        itemView.isEnabled = true
                    }
                    .start()
        }

        fun setRandomState() {
            val newState = KnobState.random()
            rotateBy(degree = newState.angle-knobState.angle, newState = newState, completion = {})
        }
        fun setBgColor(bgColor:Int) {
            val bg = itemView.background
            if (bg is GradientDrawable) {
                color = bgColor
                RxBus.publish(ColorChangeEvent(position = adapterPosition, color = color))
                bg.setColor(bgColor)
            }
        }
        private fun setRandomColor() {
            val random = Random().nextInt(5)
            val bgColor : Int
            when (random) {
                0->bgColor = ContextCompat.getColor(itemView.context, R.color.blue)
                1->bgColor = ContextCompat.getColor(itemView.context, R.color.green)
                2->bgColor = ContextCompat.getColor(itemView.context, R.color.red)
                3->bgColor = ContextCompat.getColor(itemView.context, R.color.orange)
                4->bgColor = ContextCompat.getColor(itemView.context, R.color.lightBlue)
                else->bgColor = ContextCompat.getColor(itemView.context, R.color.lightBlue)
            }
            setBgColor(bgColor)
        }
        fun isVerticallyInline(bottom:KnobState, top:KnobState):Boolean {
            when (Pair(bottom, top)) {
                Pair(KnobState.TOP,KnobState.RIGHT)->return true
                Pair(KnobState.TOP,KnobState.BOTTOM)->return true
                Pair(KnobState.LEFT,KnobState.RIGHT)->return true
                Pair(KnobState.LEFT,KnobState.BOTTOM)->return true
                else->return false
            }
        }

        fun isHorizontallyInline(left:KnobState, right:KnobState):  Boolean {
            when (Pair(left, right)) {
                Pair(KnobState.TOP,KnobState.BOTTOM)->return true
                Pair(KnobState.TOP,KnobState.LEFT)->return true
                Pair(KnobState.RIGHT,KnobState.BOTTOM)->return true
                Pair(KnobState.RIGHT,KnobState.LEFT)->return true
                else->return false
            }
        }
    }
}
