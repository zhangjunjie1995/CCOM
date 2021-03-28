package com.ccom.oauth.config;

import com.ccom.oauth.util.UserJwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


/*****
 * 自定义授权认证类
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    ClientDetailsService clientDetailsService;

//    @Autowired
//    private UserFeign userFeign;

    /****
     * 自定义授权认证
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //============================客户端信息认证============================
        //取出身份，如果身份为空说明没有认证
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //没有认证统一采用httpbasic认证，httpbasic中存储了client_id和client_secret，开始认证client_id和client_secret
        if (authentication == null) {
            ClientDetails clientDetails = clientDetailsService.loadClientByClientId(username);
            if (clientDetails != null) {
                //秘钥
                String clientSecret = clientDetails.getClientSecret();
                //静态方式
//                return new User(
//                        username,//客户端id
//                        new BCryptPasswordEncoder().encode(clientSecret),//客户端密钥->加密
//                        AuthorityUtils.commaSeparatedStringToAuthorityList(""));//权限
                //数据库查找方式
                return new User(username,clientSecret, AuthorityUtils.commaSeparatedStringToAuthorityList(""));
            }
        }
        //============================客户端信息认证结束============================

        //============================用户信息认证============================

        if (StringUtils.isEmpty(username)) {
            return null;
        }

        //根据用户名查询用户信息
        String pwd = new BCryptPasswordEncoder().encode("szitheima");
//        String pwd = userFeign.findByUsername(username).getData().getPassword();
        //创建User对象  授予权限.GOODS_LIST  SECKILL_LIST
        String permissions = "goods_list,seckill_list";//指定用户角色信息,从数据库查询

        UserJwt userDetails = new UserJwt(username, pwd, AuthorityUtils.commaSeparatedStringToAuthorityList(permissions));

//        userDetails.setComy(songsi);
        return userDetails;
    }

    public static void main(String[] args) {
        String zhangsan = new BCryptPasswordEncoder().encode("zhangsan");
        System.out.println(zhangsan);
    }
}
