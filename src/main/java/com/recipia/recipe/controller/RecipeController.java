package com.recipia.recipe.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/recipe")
public class RecipeController {

    @GetMapping("hte")
    public String kafkatest () {
        log.info("recipe-controller");
        return null;
    }

}
