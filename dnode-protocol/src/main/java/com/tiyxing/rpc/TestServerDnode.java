package com.tiyxing.rpc;

import com.tiyxing.rpc.dnode.DNode;
import com.tiyxing.rpc.dnode.DNodeDecoder;
import com.tiyxing.rpc.dnode.DNodeEncoder;
import com.tiyxing.rpc.dnode.DNodeType;
import com.tiyxing.rpc.example.SettingService;
import com.zman.net.pull.netty.NettyServer;
import com.zman.net.pull.netty.codec.NettyDecoder;
import com.zman.net.pull.netty.codec.NettyEncoder;
import com.zman.pull.stream.IDuplex;
import com.zman.stream.multiplex.pull.IMultiplex;
import com.zman.stream.multiplex.pull.codec.MultiplexDecoder;
import com.zman.stream.multiplex.pull.codec.MultiplexEncoder;
import com.zman.stream.multiplex.pull.impl.DefaultMultiplex;
import lombok.extern.slf4j.Slf4j;

import static com.zman.pull.stream.util.Pull.pull;

/**
 * @author tiyxing
 * @date 2020-01-05
 * @since 1.0.0
 */
@Slf4j
public class TestServerDnode {
    public static void main(String[] args) {
        SettingService settingService=new SettingService();
        DNode dnode=new DNode(DNodeType.SERVER,settingService);
        NettyServer nettyServer = new NettyServer();
        nettyServer.onAccept((connectionId, socketDuplex) -> {

            log.info("received connection id: {}", connectionId);
            IMultiplex multiplex = createMultiplex(dnode);

            pull(socketDuplex, new NettyDecoder(), new MultiplexDecoder(),
                    multiplex.duplex());

            pull(multiplex.duplex(), new MultiplexEncoder(), new NettyEncoder(),
                    socketDuplex);

        })
                .onDisconnect((connectionId, socketDuplex) -> socketDuplex.close())
                .onThrowable(Throwable::printStackTrace)
                .listen(8081);
    }

    private static IMultiplex createMultiplex(DNode dnode) {
        return new DefaultMultiplex()
                .onAccept(channel -> {

                    log.info("receive remote channel: id:{}, resourceId:{}", channel.id(), channel.resourceId());

                    IDuplex dNodeDuplex = dnode.createDNodeStream();

                    pull(channel.duplex(), new DNodeDecoder(), dNodeDuplex);

                    pull(dNodeDuplex, new DNodeEncoder(), channel.duplex());
                })
                .onClosed(throwable -> {if(throwable!=null)throwable.printStackTrace();})
                ;
    }
}
