package com.example.demo;

public class ReadMe {
    // 1
    // @PathVariable
    // http://localhost:8080/spring-mvc-basics/foos/abc
    // ----
    // ID: abc

    // @GetMapping("/foos/{id}")
    // @ResponseBody
    // public String getFooById(@PathVariable String id) {
    // return "ID: " + id;
    // }


    // 2
    // @RequestParam
    // http://localhost:8080/spring-mvc-basics/foos?id=abc
    // ----
    // ID: abc

    // @GetMapping("/foos")
    // @ResponseBody
    // public String getFooByIdUsingQueryParam(@RequestParam String id) {
    //     return "ID: " + id;
    // }


    // ManyToMany Mapping link - https://www.baeldung.com/role-and-privilege-for-spring-security-registration#user-role-and-privilege


}
