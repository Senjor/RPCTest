/**
 * Copyright (C), 2009-2020
 * FileName: Test
 * Author:   laosun
 * Date:     2020/3/13 1:08 下午
 * Description:
 */
package remote.procedure.call.test;

import remote.procedure.call.server.HelloService;
import remote.procedure.call.server.HelloServiceImpl;
import remote.procedure.call.server.Server;
import remote.procedure.call.server.ServerCenter;

public class RPCServerTest {
    public static void main(String[] args) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                //       1 服务中心
                Server server = new ServerCenter(9999);
                //       2 将接口及实现类注册到服务中心
                server.register(HelloService.class, HelloServiceImpl.class);
                //       3
                server.start();

            }
        }).start();

    }
}
