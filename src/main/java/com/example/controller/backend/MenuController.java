package com.example.controller.backend;

import com.example.entity.Menu;
import com.example.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/menus")
@CrossOrigin(origins = "*")
public class MenuController {

    @Autowired
    private MenuService menuService;

    /**
     * Display a listing of the resource.
     */
    @GetMapping
    public ResponseEntity<List<Menu>> index() {
        List<Menu> menus = menuService.getAllActiveMenus();
        return ResponseEntity.ok(menus);
    }

    /**
     * Store a newly created resource in storage.
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> store(
            @RequestParam("name") String name,
            @RequestParam("link") String link,
            @RequestParam("type") String type,
            @RequestParam("table_id") String tableId,
            @RequestParam("parent_id") Long parentId,
            @RequestParam("sort_order") Integer sortOrder,
            @RequestParam("position") String position,
            @RequestParam("status") int status) {

        try {
            Menu menu = new Menu();
            menu.setName(name);
            menu.setLink(link);
            menu.setType(type);
            menu.setTableId(tableId);
            menu.setParentId(parentId);
            menu.setSortOrder(sortOrder);
            menu.setPosition(position);
            menu.setStatus(status);

            menuService.createMenu(menu);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Menu created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error creating menu: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Display the specified resource.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        Optional<Menu> menu = menuService.getMenuById(id);
        if (menu.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Menu not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(menu.get());
    }

    /**
     * Show the form for editing the specified resource.
     */
    @GetMapping("/{id}/edit")
    public ResponseEntity<?> edit(@PathVariable Long id) {
        Optional<Menu> menu = menuService.getMenuById(id);
        if (menu.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Menu not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        List<Menu> allMenus = menuService.getAllActiveMenus();

        // Get unique positions
        List<String> uniquePositions = allMenus.stream()
                .map(Menu::getPosition)
                .distinct()
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("menu", menu.get());
        result.put("positions", uniquePositions);
        result.put("menuItems", allMenus);

        return ResponseEntity.ok(result);
    }

    /**
     * Update the specified resource in storage.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> update(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("link") String link,
            @RequestParam("type") String type,
            @RequestParam("table_id") String tableId,
            @RequestParam("parent_id") Long parentId,
            @RequestParam("sort_order") Integer sortOrder,
            @RequestParam("position") String position,
            @RequestParam("status") int status) {

        try {
            Optional<Menu> optionalMenu = menuService.getMenuById(id);
            if (optionalMenu.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Menu not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Menu menuDetails = new Menu();
            menuDetails.setName(name);
            menuDetails.setLink(link);
            menuDetails.setType(type);
            menuDetails.setTableId(tableId);
            menuDetails.setParentId(parentId);
            menuDetails.setSortOrder(sortOrder);
            menuDetails.setPosition(position);
            menuDetails.setStatus(status);

            Menu updatedMenu = menuService.updateMenu(id, menuDetails);

            if (updatedMenu != null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Menu updated successfully");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Failed to update menu");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error updating menu: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Remove the specified resource from storage.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> destroy(@PathVariable Long id) {
        boolean isDeleted = menuService.deleteMenu(id);
        if (isDeleted) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Menu deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Menu not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Toggle the status of the specified resource.
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, String>> status(@PathVariable Long id) {
        Menu updatedMenu = menuService.toggleMenuStatus(id);
        if (updatedMenu != null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Menu status updated successfully");
            response.put("status", String.valueOf(updatedMenu.getStatus()));
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Menu not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Soft delete the specified resource.
     */
    @PutMapping("/{id}/delete")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        Menu deletedMenu = menuService.softDeleteMenu(id);
        if (deletedMenu != null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Menu moved to trash successfully");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Menu not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Display a listing of trashed resources.
     */
    @GetMapping("/trash")
    public ResponseEntity<List<Menu>> trash() {
        List<Menu> trashedMenus = menuService.getAllTrashedMenus();
        return ResponseEntity.ok(trashedMenus);
    }

    /**
     * Restore the specified resource from trash.
     */
    @PutMapping("/{id}/restore")
    public ResponseEntity<Map<String, String>> restore(@PathVariable Long id) {
        Menu restoredMenu = menuService.restoreMenu(id);
        if (restoredMenu != null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Menu restored successfully");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Menu not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}