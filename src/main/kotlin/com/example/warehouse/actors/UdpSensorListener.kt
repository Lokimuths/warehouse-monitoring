package com.example.warehouse.actors

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.Adapter
import akka.actor.typed.javadsl.Behaviors
import com.example.warehouse.Measurement

object UdpSensorListener {

    fun create(
        port: Int,
        parse: (String) -> Measurement?,
        warehouse: ActorRef<WarehouseActor.Command>,
    ): Behavior<Void> = Behaviors.setup { ctx ->
        Adapter.actorOf(
            ctx,
            UdpListenerClassicActor.props(port, parse, warehouse),
            "udp-listener-$port",
        )
        Behaviors.empty()
    }

}