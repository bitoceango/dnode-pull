package com.tiyxing.rpc;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.tiyxing.rpc.dnode.DNode;
import com.tiyxing.rpc.dnode.DNodeDecoder;
import com.tiyxing.rpc.dnode.DNodeEncoder;
import com.tiyxing.rpc.dnode.DNodeType;
import com.tiyxing.rpc.example.SettingSync;
import com.zman.pull.stream.IDuplex;
import com.zman.stream.multiplex.pull.IChannel;
import com.zman.stream.multiplex.pull.IMultiplex;
import com.zman.stream.multiplex.pull.codec.MultiplexDecoder;
import com.zman.stream.multiplex.pull.codec.MultiplexEncoder;
import com.zman.stream.multiplex.pull.impl.DefaultMultiplex;
import com.zman.stream.socket.pull.SocketClient;
import com.zman.stream.socket.pull.codec.SocketDecoder;
import com.zman.stream.socket.pull.codec.SocketEncoder;
import com.zman.thread.eventloop.impl.DefaultEventLoop;
import lombok.extern.slf4j.Slf4j;

import static com.zman.pull.stream.util.Pull.pull;

/**
 * @author tiyxing
 * @date 2020-01-05
 * @since 1.0.0
 */
@Slf4j
public class TestClientDnode {
    public static void main(String[] args) throws InterruptedException {
        SettingSync settingSync=new SettingSync();
        DNode dNode = new DNode(DNodeType.CLINET,settingSync);
        dNode.on("ready", o -> {
            String setting="{\n" +
                    "    \"voice\":80,\n" +
                    "\"soc\":\"100\"\n" +
                    "}";
            JsonArray settingArgs=new JsonArray();
            settingArgs.add(new JsonPrimitive(setting));
            dNode.invoke("handleSettingSync","syncRespond",settingArgs);
        });
        IDuplex source = dNode.createDNodeStream();

        IMultiplex multiplex = new DefaultMultiplex();
        IChannel localChannel = multiplex.createChannel("food-dnode");
        pull(source, new DNodeEncoder(), localChannel.duplex());
        pull(localChannel.duplex(), new DNodeDecoder(), source);

        new SocketClient(new DefaultEventLoop())
                .onConnected(socketDuplex->{
                    log.info("connected duplex: {}", socketDuplex);

                    socketDuplex.sink().onClosed(System.out::println);
                    socketDuplex.source().onClosed(System.out::println);

                    pull( multiplex.duplex(), new MultiplexEncoder(), new SocketEncoder(),
                            socketDuplex);

                    pull( socketDuplex, new SocketDecoder(), new MultiplexDecoder(),
                            multiplex.duplex());


                })
                .onDisconnected(()-> log.info("disconnected"))
                .onThrowable(Throwable::printStackTrace)
                .connect("localhost", 8081);
    }





}
