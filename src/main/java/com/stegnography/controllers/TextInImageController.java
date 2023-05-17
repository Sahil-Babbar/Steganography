package com.stegnography.controllers;

import com.stegnography.domain.images;
import com.stegnography.imageService.AudioService;
import com.stegnography.imageService.TextInsideImage;
import com.stegnography.imageService.imageService;
import com.stegnography.utility.MailConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

@Controller
public class TextInImageController {
    @Autowired
    private imageService imageservice;
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MailConstructor mailconstructer;

    @Autowired
    private AudioService audioservice;
    @Autowired
    ResourceLoader resourceLoader;
    @Autowired
    TextInsideImage textInsideImage;

    @RequestMapping("/Textinimages")
    public String textInImage(ModelMap model) {
        images image = new images();
        model.addAttribute("Image", image);
        return "text";
    }

    @GetMapping("/imageenc_confirmation")
    public String confirm(
            @RequestParam("email") String email,
            ModelMap model
    ) {

        model.addAttribute("email", email);
        return "audioenc_confermation";
    }

    @PostMapping("/imageenc")
    public String audioenc(
            @RequestParam MultipartFile image,
            @RequestParam String message,
            @RequestParam String email,
            HttpServletRequest request,
            ModelMap model
    ) {
        images img = new images();
        img.setImage(image);
        imageservice.save(img);
        String encodedString;

        String userid = img.getId().toString();
        try {
            File image1 = imageservice.convert(image);
            byte[] bytes = imageservice.fileresigedbytearray(image1);
            encodedString = textInsideImage.embedText(bytes,userid, message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String userId = img.getId().toString();
        model.addAttribute("email",email);
        mailSender.send(mailconstructer.constructOrderConfirmationEmail1(userId, encodedString, email));
        return "redirect:/imageenc_confirmation?email=" + email;
    }
}