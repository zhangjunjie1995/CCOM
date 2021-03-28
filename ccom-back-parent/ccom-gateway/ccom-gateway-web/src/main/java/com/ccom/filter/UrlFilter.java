package com.ccom.filter;

/**
 * 用于过滤需要用户登录的地址
 * @version 1.0
 * @package com.changgou.filter *
 * @since 1.0
 */
public class UrlFilter {

    /**
     * 要放行的路径
     */
    private static  final String nointerceterurl="/api/user/login,/api/user/add";

    /**
     * 用来判断 如果当前的请求在放行的请求中存在(不需要拦截 :true,否则需要拦截:false)
     * @return
     */
    public static boolean hasAutorize(String uri){
        String[] split = nointerceterurl.split(",");

        for (String s : split) {
            if(s.equals(uri)){
                //不需要拦截
                return true;
            }
        }
        //要拦截
        return false;
    }
}
