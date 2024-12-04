package com.Tic_Tac_Toe.Tic_Tac_Toe.game.controllers;

import cn.apiclub.captcha.Captcha;
import com.Tic_Tac_Toe.Tic_Tac_Toe.game.entity.User;
import com.Tic_Tac_Toe.Tic_Tac_Toe.game.services.IUserService;
import com.Tic_Tac_Toe.Tic_Tac_Toe.game.util.CaptchaUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService service;

    @GetMapping("/register")
    public String registerUser(Model model) {
        User user = new User();
        getCaptcha(user);
        model.addAttribute("user", user);
        return "registerUser";
    }

    @PostMapping("/save")
    public String saveUser(
        @ModelAttribute User user,
        Model model
    ) {
        if (user.getCaptcha().equals(user.getHiddenCaptcha())) {
            service.createUser(user);
            model.addAttribute("message", "User Registered successfully!");
            // Fixed the redirect to include the correct path
            return "redirect:/user/allUsers"; // Redirect to /user/allUsers after saving the user
        } else {
            model.addAttribute("message", "Invalid Captcha");
            getCaptcha(user);
            model.addAttribute("user", user);
        }
        return "registerUser"; // Return to registration page in case of error
    }

    @GetMapping("/allUsers")
    public String getAllUsers(Model model) {
        List<User> userList = service.getAllUsers();
        model.addAttribute("userList", userList);
        return "listUsers";  // Ensure this is the correct template name.
    }

    @GetMapping("/login")
    public String loginForm(Model model, HttpServletRequest request) {
        User user = new User();
        getCaptcha(user);
        model.addAttribute("user", user);
        model.addAttribute("captcha", user.getRealCaptcha());

        // Explicitly add CSRF token
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        model.addAttribute("_csrf", csrfToken);

        return "login";
    }

    @PostMapping("/login")
    public String loginUser(
        @RequestParam String username,
        @RequestParam String password,
        @RequestParam String captcha,
        Model model
    ) {
        User user = new User();  // Only for managing CAPTCHA
        if (!captcha.equals(user.getHiddenCaptcha())) {
            model.addAttribute("message", "Invalid CAPTCHA");
            getCaptcha(user);
            model.addAttribute("captcha", user.getRealCaptcha());
            return "login";
        }

        Optional<User> existingUser = service.findByUsernameAndPassword(username, password);
        if (existingUser.isPresent()) {
            model.addAttribute("message", "Login Successful!");
            return "redirect:/user/allUsers";
        } else {
            model.addAttribute("message", "Invalid Username or Password");
            getCaptcha(user);
            model.addAttribute("captcha", user.getRealCaptcha());
            return "login";
        }
    }

    private void getCaptcha(User user) {
        Captcha captcha = CaptchaUtil.createCaptcha(240, 70);
        user.setHiddenCaptcha(captcha.getAnswer());
        user.setCaptcha(""); // Clear user input for captcha
        user.setRealCaptcha(CaptchaUtil.encodeCaptcha(captcha));
    }


}
