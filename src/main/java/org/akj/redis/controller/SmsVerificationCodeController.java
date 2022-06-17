package org.akj.redis.controller;

import org.akj.redis.service.SmsVerificationCodeService;
import org.akj.springboot.common.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/v1/sms-verification-code")
public class SmsVerificationCodeController {

    private static final String PHONE_NUMBER_REGEX =
            "^(?:(?:\\+|00)86)?1(?:3\\d|4[4-9]|5[0-35-9]|6[67]|7[013-8]|8\\d|9\\d)\\d{8}$";
    private final SmsVerificationCodeService smsVerificationCodeService;

    public SmsVerificationCodeController(SmsVerificationCodeService smsVerificationCodeService) {
        this.smsVerificationCodeService = smsVerificationCodeService;
    }


    @PutMapping
    public ResponseEntity sendSmsVerifyCode(@NotNull @RequestParam("phoneNumber") String phoneNumber) {

        if (!phoneNumber.matches(PHONE_NUMBER_REGEX)) {
            throw new BusinessException("E001-001", "invalid phone number.");
        }

        if (smsVerificationCodeService.checkSmsCodeSentAndValid(phoneNumber)) {
            throw new BusinessException("E001-001", "sms code has already been sent, if you don't receive it please wait for few seconds.");
        }

        smsVerificationCodeService.sendSmsVerificationCode(phoneNumber);
        return ResponseEntity.status(HttpStatus.OK).body("SMS verification has been sent successfully.");
    }

    @PostMapping
    public ResponseEntity verifySmsCode(@NotNull @RequestParam("phoneNumber") String phoneNumber,
                                        @NotNull @RequestParam("smsCode") String smsCode) {
        if (!phoneNumber.matches(PHONE_NUMBER_REGEX)) {
            throw new BusinessException("E001-001", "invalid phone number.");
        }

        if (!smsVerificationCodeService.checkSmsCodeSentAndValid(phoneNumber)) {
            throw new BusinessException("E001-003", "no valid sms code for current phone number.");
        }

        if (!smsVerificationCodeService.verifySmsCode(phoneNumber, smsCode)) {
            throw new BusinessException("E001-004", "Invalid sms code.");
        }

        return ResponseEntity.status(HttpStatus.OK).body("Sms verification code has been verified.");
    }

}
