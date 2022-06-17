package org.akj.redis.service;

import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;
import java.util.stream.IntStream;

@Service
@Slf4j
public class SmsVerificationCodeService {

    static final String SMS_CODE_TEMPLATE = "Your SMS verification code is %s, please be reminded it will expires within 2 minutes " +
            "and only can be used once.";

    @Value("${spring.redis.sms-code.max-digits:6}")
    private Integer smsCodeMaxDigits;

    private final RedisTemplate redisTemplate;

    public SmsVerificationCodeService(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void sendSmsVerificationCode(String phoneNumber) {

        // generate random digits code
        String code = generateSmsCode();

        // assemble the sms message
        String sms = String.format(SMS_CODE_TEMPLATE, code);
        log.info("sms message is [{}]", sms);

        // call remote interface to send

        // record the ttl in redis
        String smsCodeKey = getSmsCodekey(phoneNumber);
        redisTemplate.opsForValue().set(smsCodeKey, code, Duration.ofMinutes(2));
    }

    public boolean checkSmsCodeSentAndValid(String phoneNumber) {
        return redisTemplate.hasKey(getSmsCodekey(phoneNumber));
    }


    private String generateSmsCode() {
        Random random = new Random();
        String code = IntStream.range(0, smsCodeMaxDigits)
                .map(i -> random.nextInt(10))
                .mapToObj(i -> i + "")
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();

        log.debug("generated sms code is {}", code);
        return code;
    }

    private String getSmsCodekey(String phoneNumber) {
        return String.format("sms:code:redis-test:%s", phoneNumber);
    }

    public boolean verifySmsCode(String phoneNumber, String smsCode) {
        if (StringUtils.isNotBlank(smsCode) && smsCode.length() == smsCodeMaxDigits) {
            return redisTemplate.opsForValue().get(getSmsCodekey(phoneNumber)).equals(smsCode);
        }

        return false;
    }
}
