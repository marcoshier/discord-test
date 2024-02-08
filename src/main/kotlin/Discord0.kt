import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import dev.kord.rest.NamedFile
import dev.kord.rest.builder.interaction.number
import dev.kord.rest.builder.message.AttachmentBuilder
import dev.kord.rest.builder.message.addFile
import io.ktor.client.request.forms.*
import io.ktor.util.Identity.encode
import io.ktor.utils.io.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.draw.RenderTarget
import org.openrndr.draw.isolatedWithTarget
import org.openrndr.draw.renderTarget
import org.openrndr.drawImage
import org.openrndr.internal.Driver
import org.openrndr.launch
import java.io.File
import java.nio.ByteBuffer
import kotlin.coroutines.coroutineContext
import kotlin.io.path.Path
import kotlin.math.sin

@OptIn(DelicateCoroutinesApi::class)
fun main() = application {
    configure { }
    program {

        val kord = Kord(System.getProperty("token"))

        launch {
            kord.login {
                @OptIn(PrivilegedIntent::class)
                intents += Intent.MessageContent
            }
        }

        kord.createGlobalChatInputCommand("circle", "draw a circle") {
            number("x", "x coordinate") { required = true }
            number("y", "y coordinate") { required = true }
            number("radius", "radius of the circle") { required = true }
        }

        kord.on<MessageCreateEvent> {
            if (message.author?.isBot != false) return@on

            if (message.content == ".") {
                message.channel.createMessage("good point!")
            }
        }

        launch {
            kord.on<ChatInputCommandInteractionCreateEvent>(scope = this) {
                val response = interaction.deferPublicResponse()
                val command = interaction.command
                val cX = command.numbers["x"]!!
                val cY = command.numbers["y"]!!
                val radius = command.numbers["radius"]!!

                val cb = drawImage(width, height) {
                    drawer.clear(ColorRGBa.RED)
                    drawer.circle(cX, cY, radius)
                }

                val f = File("data/test.png")
                cb.saveToFile(f)


                response.respond {
                    //addFile("file.png", ChannelProvider(cb.bufferSize(0)) { ByteReadChannel(buffer) })
                    addFile(f.toPath())
                    content = "A circle at position x: $cX, y: $cY and radius $radius"
                    //files.add(f)
                }
            }
        }






        extend {

            drawer.circle(drawer.bounds.center, sin(seconds) * 50.0 + 50.0)

        }
    }
}