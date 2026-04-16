package com.jobhunter.jobhunter.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 404 — URL not have handler
    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handle404(Exception ex, Model model) {
        model.addAttribute("errorCode", "404");
        model.addAttribute("errorMessage", "Trang bạn tìm không tồn tại.");
        return "error/error";
    }

    // 400 — Input no valid(validation, business rule)
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(IllegalArgumentException ex, Model model) {
        model.addAttribute("errorCode", "400");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/error";
    }

    // 400 — State not valid (apply duplicate, job is close...)
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalState(IllegalStateException ex, Model model) {
        model.addAttribute("errorCode", "400");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/error";
    }

    // 403 — not authority
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handle403(Model model) {
        model.addAttribute("errorCode", "403");
        model.addAttribute("errorMessage", "Bạn không có quyền truy cập trang này.");
        return "error/error";
    }

    // 500 — RuntimeException (error server include entity not found)
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleRuntime(RuntimeException ex, Model model) {
        log.error("RuntimeException: {}", ex.getMessage(), ex);
        model.addAttribute("errorCode", "500");
        model.addAttribute("errorMessage", "Đã có lỗi xảy ra. Vui lòng thử lại sau.");
        return "error/error";
    }

    // 500 — All other exception
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handle500(Exception ex, Model model) {
        log.error("Unhandled Exception: {}", ex.getMessage(), ex);
        model.addAttribute("errorCode", "500");
        model.addAttribute("errorMessage", "Đã có lỗi xảy ra. Vui lòng thử lại sau.");
        return "error/error";
    }
}