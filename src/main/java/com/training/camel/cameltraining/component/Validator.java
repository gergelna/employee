package com.training.camel.cameltraining.component;

import org.springframework.stereotype.Component;

@Component
public class Validator {

    public void validatePhoneNumber(String number) {
        number = number.startsWith("+") ? number.substring(1) : number;
        if (!number.chars().allMatch(Character::isDigit)) {
            throw new IllegalStateException("PhoneNumber contains not only digits - " + number);
        }
    }
}
