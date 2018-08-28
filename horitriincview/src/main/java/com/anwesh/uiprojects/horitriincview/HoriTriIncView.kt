package com.anwesh.uiprojects.horitriincview

/**
 * Created by anweshmishra on 29/08/18.
 */

import android.view.View
import android.view.MotionEvent
import android.content.Context
import android.graphics.*

val nodes : Int = 5

fun Canvas.drawHTINode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.strokeWidth = Math.min(w, h) / 60
    paint.strokeCap = Paint.Cap.ROUND
    paint.color = Color.parseColor("#009688")
    val gap : Float = w / nodes
    val sc : Float = Math.min(0.5f, Math.max(0f, scale - 0.5f)) * 2
    val size : Float = gap / 2
    save()
    translate(gap * i, h/2)
    drawLine(0f, 0f, gap * sc, 0f, paint)
    val cy : Float = -size
    val currY : Float = size - size * sc
    save()
    translate(gap/2, 0f)
    scale(1f, 1f - 2 * (i % 2))
    val clippedPath : Path = Path()
    clippedPath.addRect(RectF(-size/2, cy, size/2, cy + size), Path.Direction.CW)
    clipPath(clippedPath)
    val path : Path = Path()
    path.moveTo(-size/2, currY)
    path.lineTo(size/2, currY)
    path.lineTo(0f, currY - size)
    drawPath(path, paint)
    restore()
    restore()
}

class HoriTriIncView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var prevScale : Float = 0f, var dir : Float = 0f) {
        fun update(cb : (Float) -> Unit) {
            scale += 0.1f * this.dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(scale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }
    }

    data class HTINode(var i : Int, val state : State = State()) {
        var prev : HTINode? = null
        var next : HTINode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = HTINode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawHTINode(i, state.scale, paint)
            prev?.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            state.update {
                cb(i, it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : HTINode {
            var curr : HTINode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class LinkedHoriTriInc(var i : Int) {
        private var curr : HTINode = HTINode(i)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            curr.update {i, scl ->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(i, scl)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }
 }