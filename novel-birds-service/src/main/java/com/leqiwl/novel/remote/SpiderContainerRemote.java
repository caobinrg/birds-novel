package com.leqiwl.novel.remote;

import us.codecraft.webmagic.Request;

/**
 * @author 飞鸟不过江
 */
public interface SpiderContainerRemote {

    Integer getSpiderStatus(String domain);

    void spiderClose(String domain,String countDownSpace);

    void spiderStop(String domain,String countDownSpace);

    void spiderStart(String domain);

    void spiderStart(String domain,String countDownSpace);

    void spiderJumpQueue(Request request);

    void spiderJumpQueue(Request request, String domain);

}
