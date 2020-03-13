/**
 * Copyright (C), 2009-2020
 * FileName: Client
 * Author:   laosun
 * Date:     2020/3/13 11:27 上午
 * Description:
 */
package remote.procedure.call.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {
//    获取代表服务端接口的动态代理对象HelloService，将来直接调用方法就ok了
    /**
     * @param serviceName  请求的接口类
     * @param address      请求服务端的ip，port
     * @return             获取代表服务端接口的动态代理对象（任意的接口对象）
     */
    @SuppressWarnings("unchecked")
    public static <T> T getRemoteProxyObj(Class serviceInterface, InetSocketAddress addr) {

//       参数1：类加载器，需要代理哪个类 比如HelloService类 为了凑语法
//       参数2：需要代理的对象具备哪些功能 即有哪些方法 ---接口  因为一个对象可能实现多个接口，所以这里传递的是接口数组
//       参数3：动态代理最终要实现的方法

        return (T)Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class<?>[]{serviceInterface}, new InvocationHandler() {
//            proxy 代理的对象   method 要调用的方法  args 参数
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//                客户端向服务端发送请求

                ObjectOutputStream outputStream = null;
                ObjectInputStream  inputStream = null;
                try {

                    Socket socket = new Socket();
                    socket.connect(addr);

                    outputStream = new ObjectOutputStream(socket.getOutputStream()) ;
    //               需要发送 接口名，方法名，方法参数，参数类型
                    outputStream.writeUTF(serviceInterface.getName());
                    outputStream.writeUTF(method.getName());

                    outputStream.writeObject(method.getParameterTypes());
                    outputStream.writeObject(args);

    //               等待服务端处理-----去编写服务端的代码拉。。。。。

    //               接收服务端处理过的返回值
                    inputStream = new ObjectInputStream(socket.getInputStream());
                    return inputStream.readObject();
                }
                catch(Exception e) {
                    e.printStackTrace();
                    return  null;
                }finally {
                    if (inputStream != null){
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (null != outputStream) {
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }



            }
        });
    }

}
