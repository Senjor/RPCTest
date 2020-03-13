/**
 * Copyright (C), 2009-2020, 智游集团
 * FileName: Server
 * Author:   laosun
 * Date:     2020/3/13 11:07 上午
 * Description:
 */
package remote.procedure.call.server;


/**
 * 服务中心
 */
public interface Server {
//    开启
    void start();
//    关闭
    void stop();
//    注册
    void register(Class service, Class serviceImpl);

}
