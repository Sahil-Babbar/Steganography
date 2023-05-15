package com.stegnography.controllers;

import com.stegnography.domain.images;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TextInImageController {
    @RequestMapping("/Textinimages")
    public String textInImage(ModelMap model){
        images image = new images();
        model.addAttribute("Image",image);
        return "text";
    }


}
