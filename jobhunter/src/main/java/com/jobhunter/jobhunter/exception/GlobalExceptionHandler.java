package com.jobhunter.jobhunter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // ─── 404 — Not Found ────────────────────────────────────────
    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handle404(Exception ex, Model model) {
        model.addAttribute("errorCode", "404");
        model.addAttribute("errorMessage", "Trang bạn tìm không tồn tại.");
        return "error/error";
    }

    // ─── RuntimeException — dữ liệu không tìm thấy ──────────────
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(RuntimeException ex, Model model) {
        model.addAttribute("errorCode", "404");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/error";
    }

    // ─── 403 — Access Denied ────────────────────────────────────
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handle403(Model model) {
        model.addAttribute("errorCode", "403");
        model.addAttribute("errorMessage", "Bạn không có quyền truy cập trang này.");
        return "error/error";
    }

    // ─── 500 — Internal Server Error ────────────────────────────
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handle500(Exception ex, Model model) {
        model.addAttribute("errorCode", "500");
        model.addAttribute("errorMessage", "Đã có lỗi xảy ra. Vui lòng thử lại sau.");
        System.err.println("[ERROR] " + ex.getMessage());
        return "error/error";
    }
}