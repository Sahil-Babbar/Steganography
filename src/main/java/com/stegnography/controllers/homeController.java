package com.stegnography.controllers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.stegnography.Repository.UserRepository;
import com.stegnography.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.stegnography.imageService.imageService;
import com.stegnography.utility.MailConstructor;


@Controller
public class homeController {

	@Autowired
	private imageService imageservice;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private MailConstructor mailconstructer;

	@Autowired
	private UserRepository userRepository;

	String IMAGE_PREFIX = "http://www.inspireglobaleducation.com/images/";

	@RequestMapping(value = "/user")
	public String login(){
		return "login";
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String home(@ModelAttribute User user)
	{
		userRepository.save(user);
		return "index";
	}


	@RequestMapping(value = "/images")
	public String images()
	{
		return "image";
	}

	@RequestMapping(value = "/imagepage2")
	public String imagepage2(
			@RequestParam("no_of_helperimage") int noofhi, ModelMap model
	)
	{
		List<Integer> nohi = new ArrayList<>();
		for(int i=1;i<=noofhi;i++)
			nohi.add(i);

		model.addAttribute("nohi",nohi);
		model.addAttribute("noofhi",noofhi);
		return "imagepage2";
	}

	@RequestMapping(value = "/imagepage2" ,method = RequestMethod.POST)
	public String imagepage2post(
			@RequestParam("mainimage") MultipartFile mainimage,
			@RequestParam(value = "image1",required=false) MultipartFile image1,
			@RequestParam(value = "image2" , required=false) MultipartFile image2,
			@RequestParam(value = "image3" , required=false) MultipartFile image3,
			@RequestParam(value = "image4" , required=false) MultipartFile image4,
			@RequestParam(value = "image5" , required=false) MultipartFile image5,
			@RequestParam(value = "image6" , required=false) MultipartFile image6,
			@RequestParam(value = "image7" , required=false) MultipartFile image7,
			@RequestParam(value = "image8" , required=false) MultipartFile image8,
			HttpServletRequest request,
			@RequestParam("noofi") String noofhi,
			ModelMap model,
			@RequestParam("email") String em
	)
	{
		MultipartFile mainnimage = mainimage;

		com.stegnography.domain.images img = new com.stegnography.domain.images();

		img.setImage(image1);

		imageservice.save(img);


		try {
			File image = imageservice.convert(image1);
			byte[] bytes = imageservice.fileresigedbytearray(image);


			if(Integer.parseInt(noofhi) == 1)
			{
				File image11 = imageservice.convert(image1);
				byte[] bytes1 = imageservice.fileresigedbytearray(image11);

				BufferedImage mainimage1 = imageservice.toBufferedImage(bytes);
				BufferedImage helperimage = imageservice.toBufferedImage(bytes1);

				for(int i = 0;i<mainimage1.getWidth();i++)
				{
					for(int j = 0;j<mainimage1.getHeight();j++)
					{
						int pixelmain = mainimage1.getRGB(i, j);
						int pixelhelp = helperimage.getRGB(i, j);

						//System.out.println(Integer.toBinaryString(imageservice.getRed(pixelmain)));
						// for red
						int newred = imageservice.forone(imageservice.getRed(pixelmain), imageservice.getRed(pixelhelp));
						//System.out.println(Integer.toBinaryString(newred));


						//System.out.println(Integer.toBinaryString(imageservice.getGreen(pixelmain)));
						int newgreen = imageservice.forone(imageservice.getGreen(pixelmain), imageservice.getGreen(pixelhelp));
						//System.out.println(Integer.toBinaryString(newgreen));


						//System.out.println(Integer.toBinaryString(imageservice.getBlue(pixelmain)));
						int newblue = imageservice.forone(imageservice.getBlue(pixelmain), imageservice.getBlue(pixelhelp));
						//System.out.println(Integer.toBinaryString(newblue));


						//System.out.println(Integer.toBinaryString(pixelmain));
						int newforward = ( pixelhelp >> 24 ) & 0xFF;
						//System.out.println(Integer.toBinaryString(newforward));

						int pixel = imageservice.makepixel(newforward,newred,newgreen,newblue);

						helperimage.setRGB(i, j, pixel);
					}
				}

				String name = img.getId() + "h1.png";

				bytes1 = imageservice.tobytes(helperimage);

				imageservice.upload(name, bytes1);


			}

//	        	String name = img.getId() + ".png";
//
//	        	imageservice.upload(name,bytes);

			//String link = img.getId().toString() + "h1.png";

			String imageLink = IMAGE_PREFIX+"newzealand.jpg";
			mailSender.send(mailconstructer.constructOrderConfirmationEmail(imageLink , em));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return "index";
	}


}
