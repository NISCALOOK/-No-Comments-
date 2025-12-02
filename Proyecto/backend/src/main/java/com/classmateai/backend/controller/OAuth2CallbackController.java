package com.classmateai.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class OAuth2CallbackController {

    @GetMapping("/oauth2/callback")
    public RedirectView handleOAuth2Callback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String state) {
        
        if (error != null) {
            // Redirect to frontend with error
            return new RedirectView("http://localhost:3000/calendar/callback?error=" + error);
        }
        
        if (code != null) {
            // Redirect to frontend with authorization code
            return new RedirectView("http://localhost:3000/calendar/callback?code=" + code);
        }
        
        // No code or error, something went wrong
        return new RedirectView("http://localhost:3000/calendar?error=no_code");
    }
}

