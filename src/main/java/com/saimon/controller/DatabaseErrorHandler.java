package com.saimon.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DatabaseErrorHandler {

// ETA CHAILE KINTU CONTROLLER CLASS ER VITORE O LIKHA JAI.. Example given there

//    @ExceptionHandler(DataAccessException.class)
//    public String handleDatabaseException(Model model, DataAccessException exception) {
//        model.addAttribute("error", exception);
//        return "error";
//    }

    @ExceptionHandler(Exception.class)
    public String handleDatabaseException(Model model, Exception exception) {
        model.addAttribute("error", exception);
        return "error";
    }

}
