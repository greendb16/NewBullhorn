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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    MessageRepository repository;

    @Autowired
    CloudinaryConfig cloudc;

    @GetMapping("/")
    public String showAll(Model model){
        model.addAttribute("messages", repository.findAll());
        return "list";
    }

    @GetMapping("/add")
    public String newMessage(Model model){
        model.addAttribute("message", new Message());
            return "messageform";
    }

    @PostMapping("/process")
    public String processMessageForm(@Valid  Message message, BindingResult result, @RequestParam("datePosted") String datePosted,
                                     @RequestParam("file")MultipartFile file){
        if (file.isEmpty()){
            message.setPic(null);
        }

        if(!file.isEmpty()){
                    try{
                        Map uploadResult = cloudc.upload(file.getBytes(),
                                ObjectUtils.asMap("Resourcetype", "auto"));
            message.setPic(uploadResult.get("url").toString());
            repository.save(message);
        }catch (IOException e){
            e.printStackTrace();
            return "messageform";}
        }

        if(result.hasErrors()){
            return "messageform";
        }
        Date date = new Date();

        try{
            date = new SimpleDateFormat("yyyy-mm-dd").parse(datePosted);}

        catch (Exception e){
            e.printStackTrace();
        }
        message.setDatePosted(date);
        repository.save(message);
        return "redirect:/";
    }

    @RequestMapping("/update/{id}")
    public String updateMessage(@PathVariable("id") long id, Model model){
        model.addAttribute("message", repository.findById(id).get());
//        model.addAttribute(repository.findById(id).get());
        return "messageform";
    }

    @RequestMapping("/detail/{id}")
    public String detailMessage(@PathVariable("id") long id, Model model){
        model.addAttribute(repository.findById(id).get());
        return "show";
    }

    @RequestMapping("/delete/{id}")
    public String deleteMessage(@PathVariable("id") long id){
        repository.deleteById(id);
        return "redirect:/";
    }
}
