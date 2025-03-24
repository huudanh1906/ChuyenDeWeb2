package com.example.controller.frontend;

import com.example.entity.Menu;
import com.example.service.MenuService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/frontend/menus")
@CrossOrigin(origins = "*")
public class FrontendMenuController {

    @Autowired
    private MenuService menuService;

    /**
     * Fetch all menu items with parent-child relationships
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> index() {
        List<Menu> menus = menuService.getAllActiveMenus();

        Map<String, Object> response = new HashMap<>();
        response.put("menus", menus);

        return ResponseEntity.ok(response);
    }
}