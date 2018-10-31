package pers.liujunyi.common.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

/***
 * AuditorAware　设置当前用户ＩＤ
 */
@Configuration
public class UserIDAuditorBean implements AuditorAware<Long> {


    @Override
    public Optional<Long> getCurrentAuditor() {
        Long currentUserId = null;

        return Optional.ofNullable(currentUserId);
    }
}