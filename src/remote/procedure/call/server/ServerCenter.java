/**
 * Copyright (C), 2009-2020
 * FileName: ServerCenter
 * Author:   laosun
 * Date:     2020/3/13 11:09 上午
 * Description:
 */
package remote.procedure.call.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//服务中心的具体实现
public class ServerCenter implements Server {

//    map 保存接口
//    key :接口名"HelloService"
//    value: 接口的具体实现 HelloServiceImpl  这里使用Class代表所有的类
    private static HashMap<String,Class> serviceRegister = new HashMap<>();

    private  static  int port ;

//  线程池 里面放多少线程连接，和CPU的个数等有关，可以通过获取系统信息初始化。
    private static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private static boolean isRunning = false;

    public ServerCenter(int port) {
        this.port = port;
    }
    @Override
    public void start() {


        //          使用socket，指定端口号9999，将来供client连接
        ServerSocket server = null;
        try {
            server = new ServerSocket();
            server.bind(new InetSocketAddress(port));

        } catch (IOException e) {
            e.printStackTrace();
        }

        isRunning = true;

        while (true){

            System.out.println("start server...");
            Socket socket = null;

//          客户端每请求一次连接（发出一个请求），服务端从连接池中获取一个线程对象去处理
            try {
                socket = server.accept();//等待客户端连接
            } catch (IOException e) {
                e.printStackTrace();
            }
//              从连接池中取连接
            executorService.execute(new ServiceTask(socket));

        }


    }
    /**
     *
     * @param service 拿到客户端返回的字符串对应的反射入口
     * @param serviceImpl  接口实现类
     */
    @Override
    public void register(Class service, Class serviceImpl) {

        serviceRegister.put(service.getName(),serviceImpl);
    }


    @Override
    public void stop() {
        isRunning = false;
        executorService.shutdown();

    }


    private static  class ServiceTask implements Runnable{

        private Socket socket;

        public ServiceTask() {
        }
        public ServiceTask(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {

            ObjectInputStream inputStream = null;
            ObjectOutputStream outputStream = null;
            try {

                //          接下来接收到客户端的连接及请求，处理
                inputStream = new ObjectInputStream(socket.getInputStream());
                //          根据之前客户端发送请求的顺序，逐个对参数解析

                String serviceName = inputStream.readUTF();//接口名
                String methodName = inputStream.readUTF();//方法名

                Class[] parameterTypes = (Class[]) inputStream.readObject();//方法的参数类型
                Object[] arguments = (Object[]) inputStream.readObject();//方法的参数名

                //          根据客户端请求，找到具体的接口，通过map寻找
                Class serviceClass = serviceRegister.get(serviceName);
                Method method = serviceClass.getMethod(methodName, parameterTypes);

                //          执行方法
                Object result = method.invoke(serviceClass.newInstance(), arguments);

                //          将结果发送给客户端
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeObject(result);


            } catch (IOException | ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                e.printStackTrace();
            } finally {
                if (null != inputStream) {
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
    }
}
