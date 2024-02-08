import lib.computeContours
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.colorBuffer
import org.openrndr.ffmpeg.loadVideoDevice
import org.openrndr.shape.IntRectangle
import org.openrndr.shape.ShapeContour

fun main() = application {
    configure {
        width = 1280
        height = 720
    }
    program {

        val cb = colorBuffer(width, height)

        val video = loadVideoDevice()
        video.play()

        val max = 120

        var i = 0
        video.newFrame.listen {
            i++
            it.frame.copyTo(cb.apply { flipV = true }, sourceRectangle = it.frame.bounds.toInt(), targetRectangle = drawer.bounds.toInt())
            if (i == 120) {
                video.dispose()
                video.newFrame.listeners.clear()
            }
        }

        var contours = listOf<ShapeContour>()

        extend {
            when(i) {
                in 0..< max -> {
                    drawer.clear(ColorRGBa.WHITE)
                    video.draw(drawer, true)
                }
                max -> contours = computeContours(cb)
            }


            drawer.image(cb)
            drawer.fill = null
            drawer.strokeWeight = 2.0
            drawer.contours(contours)

        }
    }
}