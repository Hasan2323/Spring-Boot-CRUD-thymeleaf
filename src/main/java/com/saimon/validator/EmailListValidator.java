package com.saimon.validator;

import org.springframework.util.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Created by Saimon
 * Email List validator
 */
public class EmailListValidator implements ConstraintValidator<EmailList, String> {

    final private Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$");
    private boolean result=true;
    //private int min;

    @Override
    public void initialize(EmailList emailList) {
        emailList.message();
        //min = constraintAnnotation.min();
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null){
            return false;
        }
        String[] emailList = email.split(",");
        for (String mail:emailList) {
            Matcher matcher = emailPattern.matcher(mail.trim());
            if (!matcher.matches()){
                result = false;
                break;
            }
        }
        return result;

//        if (email == null || email.isEmpty()){
//            return true;
//        }
//
//        String[] emailList = email.split(",");
//        for (String mail:emailList) {
//            if (!EmailValidator.getInstance(false).isValid(mail.trim())){
//                return false;
//            }
//        }
//
//        return true;
    }
//    @Override
//    public boolean isValid(final List<String> value, final ConstraintValidatorContext context) {
//        return !(value == null || value.isEmpty()) && value.stream().filter(e -> !StringUtils.isEmpty(e)).filter(e -> emailPattern.matcher(e).matches()).count() == value.size();
//    }
}