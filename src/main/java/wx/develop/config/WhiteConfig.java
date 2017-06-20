package wx.develop.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by lzh on 2017/6/8.
 */
@ConfigurationProperties("white.config")
@Component("whiteConfig")
public class WhiteConfig {

    /**
     * 京东库存查询sku数量限制
     */
    private String accessIp;

    public String getAccessIp() {
        return accessIp;
    }

    public void setAccessIp(String accessIp) {
        this.accessIp = accessIp;
    }
}
