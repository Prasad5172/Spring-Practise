package com.example.demo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/greetings")
public class GrettingController {
    @GetMapping
    public ResponseEntity<String> sayHellow()
    {
        return ResponseEntity.ok("Hellow from api");
    }
    @GetMapping("/saygoodbye")
    public ResponseEntity<String> sayGoodBoy()
    {
        return ResponseEntity.ok("Good Bye");
    }
}
