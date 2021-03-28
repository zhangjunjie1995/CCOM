package com.ccom.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * gateway网关全局过滤器 :用于鉴权(获取令牌 解析 判断)
 *
 * @version 1.0
 * @since 1.0
 */
@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {
    private static final String AUTHORIZE_TOKEN = "Authorization";

    //用户登录地址
    private static final String loginURL = "http://localhost:9001/oauth/login";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        //1.获取请求对象
        ServerHttpRequest request = exchange.getRequest();
        //2.获取响应对象
        ServerHttpResponse response = exchange.getResponse();

        //3.判断 是否为登录的URL 如果是则放行
        if (UrlFilter.hasAutorize(request.getURI().toString())) {
            return chain.filter(exchange);
        }
        //4.判断是否为登录的URL,如果不是则权限校验

        //4.1 从头header中获取令牌数据
        String token = request.getHeaders().getFirst(AUTHORIZE_TOKEN);
        //true:令牌在请求头中  false:令牌不在请求头中
        boolean hasToken = true;

        if (StringUtils.isEmpty(token)) {
            hasToken = false;
            //4.2 从cookie中中获取令牌数据
            HttpCookie first = request.getCookies().getFirst(AUTHORIZE_TOKEN);
            if (first != null) {
                token = first.getValue();//就是令牌的数据
            }
        }

        if (StringUtils.isEmpty(token)) {
            //4.3 从请求参数中获取令牌数据
            token = request.getQueryParams().getFirst(AUTHORIZE_TOKEN);
        }

        if (StringUtils.isEmpty(token)) {
            //4.4. 如果没有数据说明没有登录,要重定向到登录到页面
            response.setStatusCode(HttpStatus.SEE_OTHER);//303 302
            //location 指定的就是路径
            response.getHeaders().set("Location", loginURL + "?From=" + request.getURI().toString());
            return response.setComplete();
        } else {
            //请求头中没有才加入请求头，有的话就不加入请求头
            if (!hasToken) {
                //判断当前令牌是否有bearer前缀，如果没有就加上
                if (!token.startsWith("bearer ") && !token.startsWith("Bearer ")) {
                    token = "bearer " + token;
                }
                request.mutate().header(AUTHORIZE_TOKEN, token);
            }
        }


//5 解析令牌数据 ( 判断解析是否正确,正确 就放行 ,否则 结束)
//        try {
//            Claims claims = JwtUtil.parseJWT(token);
//将token添加到头信息传递给各个微服务
//            request.mutate().header(AUTHORIZE_TOKEN, "Bearer " + token);
//        } catch (Exception e) {
//            e.printStackTrace();
//            //解析失败
//            response.setStatusCode(HttpStatus.UNAUTHORIZED);
//            return response.setComplete();
//        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    public Mono<Void> needAuthorization(String url, ServerWebExchange exchange){
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.SEE_OTHER);
        response.getHeaders().set("Location",url);
        return exchange.getResponse().setComplete();
    }
}
