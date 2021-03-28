package intercepter;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Component
public class MyfeignIntercepter implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        if (requestAttributes!=null){
            //获取请求对象
            HttpServletRequest request = requestAttributes.getRequest();
            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                //获取请求对象中的所有的头信息(网关传递过来的)
                while (headerNames.hasMoreElements()) {
                    String name = headerNames.nextElement();//头的名称
                    String value = request.getHeader(name);//头名称对应的值
//                    System.out.println("name:" + name + "  value:" + value);
                    //将头信息传递给fegin (restTemplate)
                    template.header(name,value);
                }
            }
        }
    }
}
