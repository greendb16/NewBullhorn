package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class HomeController {

    @Autowired
    MessageRepository repository;

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
    public String processMessageForm(@Valid  Message message, BindingResult result, @RequestParam("datePosted") String datePosted){
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
        model.addAttribute(repository.findById(id).get());
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
        return "list";
    }
}
