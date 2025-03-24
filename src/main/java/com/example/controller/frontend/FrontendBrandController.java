package com.example.controller.frontend;

import com.example.entity.Brand;
import com.example.service.BrandService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/frontend/brands")
@CrossOrigin(origins = "*")
public class FrontendBrandController {

    @Autowired
    private BrandService brandService;

    /**
     * Get all active brands
     */
    @GetMapping
    public ResponseEntity<List<Brand>> index() {
        List<Brand> brands = brandService.getAllActiveBrands();
        return ResponseEntity.ok(brands);
    }
}