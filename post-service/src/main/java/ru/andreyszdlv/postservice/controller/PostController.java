package ru.andreyszdlv.postservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    @GetMapping()
    public List<String> getPost(){
        return List.of("Post1", "Post2", "Post3");
    }
}
