package com.example.buihuudanh;

import org.springframework.web.bind.annotation.*;

@RestController
public class HelloController {

    private String a = "defaultA"; // Giá trị mặc định
    private String b = "defaultB"; // Giá trị mặc định

    @GetMapping("/")
    public String index() {
        return "Hello World from Spring Boot";
    }

    @PostMapping("/display")
    public String displayParams(@RequestBody Params params) {
        this.a = params.getA();
        this.b = params.getB();
        return "Received parameters: a = " + this.a + ", b = " + this.b;
    }

    @PutMapping("/update")
    public String updateParams(@RequestBody Params params) {
        this.a = params.getA();
        this.b = params.getB();
        return "Updated parameters: a = " + this.a + ", b = " + this.b;
    }

    @GetMapping("/get-values")
    public String getValues() {
        return "Current values: a = " + this.a + ", b = " + this.b;
    }

    @DeleteMapping("/delete")
    public String deleteParams() {
        this.a = null; // Xóa giá trị a
        this.b = null; // Xóa giá trị b
        return "Deleted parameters: a and b are now null";
    }
}

class Params {
    private String a;
    private String b;

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }
}
