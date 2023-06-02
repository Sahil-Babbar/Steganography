package com.stegnography.controllers;

import com.stegnography.domain.images;
import com.stegnography.imageService.AudioService;
import com.stegnography.imageService.TextInsideImage;
import com.stegnography.imageService.imageService;
import com.stegnography.utility.MailConstructor;
import com.stegnography.utility.MailConstructorText;
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

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Controller
public class TextInImageController {
    @Autowired
    private imageService imageservice;
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MailConstructorText mailconstructer;

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
        System.out.println("User id is: "+userid);
        try {
            File image1 = imageservice.convert(image);
//            encodedString = textInsideImage.embedText(bytes,userid, message);
            encodedString = textInsideImage.encode("/home/knoldus/mca/Steganography/"+image1,message,"1");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String userId = img.getId().toString();
        model.addAttribute("email",email);
        mailSender.send(mailconstructer.constructOrderConfirmationEmail1(userId, encodedString, email));
        return "redirect:/imageenc_confirmation?email=" + email;
    }

    @GetMapping("/imagedecoding")
    public String decodeaudio(
            @RequestParam("id") String userid,
            ModelMap model
    )
    {
        model.addAttribute("id",userid);
        return "decodeimage";
    }
    @PostMapping("/decodeimagee")
    public String decode(
            @RequestParam("user_id") String User_id,
            @RequestParam("password") String pass,
            ModelMap model
    ) throws IOException
    {
//        File file2 = new File("src/main/resources/static/image/" + User_id + ".jpg");
//        BufferedImage img = ImageIO.read(file2);
        String decodedmsg = textInsideImage.decode("/home/knoldus/mca/Steganography/image_text_out.png", "1");
        System.out.println(decodedmsg);
        model.addAttribute("decodedmsg" , decodedmsg);

        return "decoded_msg_viewer";
    }

}