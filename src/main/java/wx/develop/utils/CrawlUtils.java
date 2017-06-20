package wx.develop.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lzh on 2017/6/2.
 * 抓取工具
 */
public class CrawlUtils {

    // 编码
    private static final String ECODING = "UTF-8";
    // 获取img标签正则
    private static final String IMGELEMENT_REG = "<img.*src=(.*?)[^>]*?/>";
    // 获取src路径的正则
    private static final String IMGSRC_REG = "src=(.*?)(?:png|jpg|jpeg|bmp|eps|gif|mif|miff|tif|tiff|svg|wmf|jpe|jpeg|dib|ico|tga|cut|pic)";
//    private static final String IMGSRC_REG = "http:\"?(.*?)(\"|>|\\s+)";

    /**
     * 抓取字符串中的手机号
     * @param param
     * @return  抓取到的手机号,以英文逗号分隔
     */
    public static String getPhoneNum(String param){
        if(StringUtils.isBlank(param)){
            return "";
        }
        //正则说明 ^ 以某个字符开头(匹配的字符串必须以指定字符开头)
        //正则说明 $ 以某个字符结尾(匹配的字符串必须以指定字符结尾)
//        Pattern pattern = Pattern.compile("^1[34578]\\d{9}$");//手机号校验使用的正则,这个是完全匹配,限制开头和结尾的
        //正则说明 ()表示一个分组,括号内正则匹配的数据,可以使用group获取
        Pattern pattern = Pattern.compile("(1[34578]\\d{9})");//这里使用分组,是要匹配符合正则的字符串,然后抓取;
//        Pattern pattern = Pattern.compile("(1[34578]\\d{9}$*)");//这里使用不适用$*都可以,是对1[34578]\\d{9}的多次重复,多个手机号连在一起也可以被匹配
        Matcher matcher = pattern.matcher(param);
        StringBuffer bf = new StringBuffer();
        //使用正则分组进行匹配获取
        while (matcher.find()) {
            bf.append(matcher.group()).append(",");
        }
        int len = bf.length();
        if (len > 0) {
            bf.deleteCharAt(len - 1);
        }
        return bf.toString();
    }


    /**
     * 抓取img标签的src
     * @param param
     * @return
     */
    public static String getImgUrl(String param){
        if(StringUtils.isBlank(param)){
            return "";
        }
        //正则说明 ()表示一个分组,括号内正则匹配的数据,可以使用group获取
        Pattern pattern = Pattern.compile(IMGSRC_REG);
        Matcher matcher = pattern.matcher(param);
        StringBuffer bf = new StringBuffer();
        //使用正则分组进行匹配获取
        while (matcher.find()) {
            String tmp = matcher.group();
            System.out.println("matcher.group  >> " +tmp);
            tmp = tmp.replace("src=\"","");
            System.out.println("matcher.src  >> " + tmp);
            bf.append(tmp).append(",");
        }
        int len = bf.length();
        if (len > 0) {
            bf.deleteCharAt(len - 1);
        }
        return bf.toString();
    }

    /**
     * 抓取字符串中的img标签
     * @param param
     * @return  抓取到的<img>元素,以英文逗号分隔
     */
    public static String getImgElement(String param){
        if(StringUtils.isBlank(param)){
            return "";
        }
        //正则说明 ()表示一个分组,括号内正则匹配的数据,可以使用group获取
        String result = patternGroup(param, IMGELEMENT_REG);
        return result;
    }

    /**
     * 抽取使用group抓取数据
     * @param param
     * @param rex
     * @return  返回匹配的结果,每个结果用英文逗号分隔
     */
    public static String patternGroup(String param,String rex){
        if(StringUtils.isBlank(param) || StringUtils.isBlank(rex) ){
            return "";
        }
        Pattern pattern = Pattern.compile(rex);
        Matcher matcher = pattern.matcher(param);
        StringBuffer bf = new StringBuffer();
        //使用正则分组进行匹配获取
        while (matcher.find()) {
            bf.append(matcher.group()).append(",");
        }
        int len = bf.length();
        if (len > 0) {
            bf.deleteCharAt(len - 1);
        }
        return bf.toString();
    }

    /**
     *  将图片url列表转换为<img>标签带url的字符串
     * @param param
     * @return
     */
    public static String contactImgUrl(String param){
        if(StringUtils.isBlank(param) ){
            return "";
        }
        //1)将\"替换为"   2)将\n  替换为""
        //TODO 2017.06.07 商品详情只抓取img标签
        String desc = CrawlUtils.getImgUrl(param.replace("\\", "\"").replace("\\n", ""));
        String[] split = desc.split(",");

        StringBuilder tmp = new StringBuilder();
        for(String s : split){
            if(StringUtils.isNotBlank(s)){
                tmp.append("<img src=\"").append(s).append("\" />");
            }
        }

        return tmp.toString();
    }
}
