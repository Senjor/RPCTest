/**
 * Copyright (C), 2009-2020
 * FileName: HelloServiceImpl
 * Author:   laosun
 * Date:     2020/3/13 11:05 上午
 * Description:
 */
package remote.procedure.call.server;

public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return "hello " + name;
    }
}
