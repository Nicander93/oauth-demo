package com.demo.manual.auth.service;

import com.demo.manual.auth.model.AuthUserLink;
import com.demo.manual.auth.model.BizUser;
import com.demo.manual.auth.repository.AuthUserLinkRepository;
import com.demo.manual.auth.repository.BizUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 统一身份（auth 用户）与各业务系统用户的映射；
 * UserInfo 按 client 的 systemCode 附带 biz_* 扩展字段。
 */
@Service
public class AuthUserLinkService {

    private static final Logger log = LoggerFactory.getLogger(AuthUserLinkService.class);

    private final AuthUserLinkRepository linkRepository;
    private final BizUserRepository bizUserRepository;

    public AuthUserLinkService(AuthUserLinkRepository linkRepository, BizUserRepository bizUserRepository) {
        this.linkRepository = linkRepository;
        this.bizUserRepository = bizUserRepository;
    }

    public Optional<BizUser> findBizUser(Long authUserId, String systemCode) {
        Optional<AuthUserLink> link = linkRepository.findByAuthUserIdAndSystemCode(authUserId, systemCode);
        if (link.isEmpty()) {
            log.info("未找到用户关联: authUserId={}, systemCode={}", authUserId, systemCode);
            return Optional.empty();
        }
        Optional<BizUser> bizUser = bizUserRepository.findById(link.get().bizUserId());
        bizUser.ifPresent(u -> log.info("用户关联命中: authUserId={} -> bizUserCode={}", authUserId, u.bizUserCode()));
        return bizUser;
    }
}
