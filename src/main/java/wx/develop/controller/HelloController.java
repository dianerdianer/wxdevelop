package wx.develop.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by lzh on 2017/6/20.
 */
@RestController
public class HelloController {

    @RequestMapping("/hello")
    @ResponseBody
    public String hello(){
        return "this is hello";
    }
}
