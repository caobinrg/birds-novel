package com.leqiwl.novel.remote;

import us.codecraft.webmagic.Request;

/**
 * @author 飞鸟不过江
 */
public interface SpiderContainerRemote {

    Integer getSpiderStatus();

    void spiderClose(String countDownSpace);

    void spiderStop(String countDownSpace);

    void spiderStart();

    void spiderStart(String countDownSpace);

    void spiderJumpQueue(Request request);

    void spiderJumpQueue(Request request, String domain);

    String getSpiderUUID();
}
