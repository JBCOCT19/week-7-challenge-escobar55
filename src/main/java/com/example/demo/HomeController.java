package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @GetMapping("/register")
    public String showRegistrationPage(Model model){
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/register")
    public String processRegistrationPage(@Valid @ModelAttribute("user") User user, BindingResult result, Model model){
        model.addAttribute("user", user);
        if(result.hasErrors()){
            return "registration";
        }
        else {
            userService.saveUser(user);
            model.addAttribute("message", "User Account Created");
        }
        return "home";
    }
    //***not sure??
    /*
    @RequestMapping("/index")
    public String index() {
        return "home";
    }
    //****
    */

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @Autowired
    UserRepository userRepository;

    @RequestMapping("/secure")
    public String secure(Principal principal, Model model) {
        String username = principal.getName();
        model.addAttribute("user", userRepository.findByUsername(username));
        return "secure";
    }

    //******* search
     /*
    @PostMapping("/search")
    public String search(Model model, @RequestParam("search") String search){
        //model.addAttribute("departments", departmentRepository.findByDeptNameContainingIgnoreCase(search));
        return "search";
    }

      */
    //*******



    //***from boolhorn*****
    @RequestMapping("/")
    public String listMessages(Model model){
        model.addAttribute("messages", messageRepository.findAll());
        return "home";
    }

    @GetMapping("/add")
    public String messageForm(Model model){
        model.addAttribute("message", new Message());
        return "messageform";
    }

    @PostMapping("/process")
    public String processForm(@Valid Message message, BindingResult result, @ModelAttribute Message messagec,
                              @RequestParam("file") MultipartFile file ) {
        if (result.hasErrors()) {
            return "messageform";
        }

        if (file.isEmpty()) {
            return "redirect:/add";
        }
        try {
            Map uploadResults = cloudc.upload(file.getBytes(),
                    ObjectUtils.asMap("resourcetype", "auto"));
            messagec.setHeadshot(uploadResults.get("url").toString());
            messageRepository.save(messagec);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/add";
        }

        messageRepository.save(message);
        return "redirect:/";

    }
}




