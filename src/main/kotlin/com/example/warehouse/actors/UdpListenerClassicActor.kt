package com.example.warehouse.actors

import akka.actor.AbstractActor
import akka.actor.CoordinatedShutdown
import akka.actor.Props
import akka.actor.typed.ActorRef
import akka.io.Udp
import akka.io.UdpMessage
import com.example.warehouse.Measurement
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress

class UdpListenerClassicActor(
    private val port: Int,
    private val parse: (String) -> Measurement?,
    private val warehouse: ActorRef<WarehouseActor.Command>,
) : AbstractActor() {

    object UdpBindFailure : CoordinatedShutdown.Reason
    private val log = LoggerFactory.getLogger(UdpListenerClassicActor::class.java)
    private var socket: akka.actor.ActorRef? = null

    override fun preStart() {
        val manager = Udp.get(context.system).manager
        manager.tell(UdpMessage.bind(self, InetSocketAddress(port)), self)
    }

    override fun postStop() {
        socket?.tell(UdpMessage.unbind(), self)
    }

    override fun createReceive(): Receive =
        receiveBuilder()
            .match(Udp.Bound::class.java) { _ ->
                socket = sender
                log.info("Listening for sensor data on UDP port {}", port)
                context.become(ready())
            }
            .match(Udp.CommandFailed::class.java) { failed ->
                log.error("UDP bind failed on port {}: {}. Shutting down system.", port, failed)
                CoordinatedShutdown.get(context.system).run(UdpBindFailure)
                context.stop(self)
            }
            .build()

    private fun ready(): Receive =
        receiveBuilder()
            .match(Udp.Received::class.java) { received ->
                val text = received.data().utf8String().trim()
                val measurement = parse(text)
                if (measurement != null) {
                    warehouse.tell(WarehouseActor.Reading(measurement))
                } else {
                    log.warn("Ignored unparseable message on port {}: '{}'", port, text)
                }
            }
            .match(Udp.Unbound::class.java) { _ -> context.stop(self) }
            .build()

    companion object {
        fun props(
            port: Int,
            parse: (String) -> Measurement?,
            warehouse: ActorRef<WarehouseActor.Command>,
        ): Props = Props.create(UdpListenerClassicActor::class.java) {
            UdpListenerClassicActor(port, parse, warehouse)
        }
    }
}