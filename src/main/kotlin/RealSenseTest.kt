import lib.RS2Sensor
import org.openrndr.application
import org.openrndr.draw.colorBuffer

fun main() = application {
    configure {
        width = 320
        height = 240
    }
    program {

        val rs2 = RS2Sensor.listSensors().map {
            it.open(640, 480)
        }.first()

        val cb = colorBuffer(width, height)

        rs2.depthFrameReceived.listen {
            println("received")
            it.copyTo(cb)
        }

        extend {
            rs2.waitForFrames()

            drawer.image(cb)

        }
    }
}