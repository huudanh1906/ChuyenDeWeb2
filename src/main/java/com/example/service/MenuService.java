package com.example.service;

import com.example.entity.Menu;
import com.example.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;

    /**
     * Get all active menu items
     */
    public List<Menu> getAllActiveMenus() {
        return menuRepository.findAll().stream()
                .filter(menu -> menu.getStatus() == 1)
                .sorted(Comparator.comparing(Menu::getParentId)
                        .thenComparing(Menu::getSortOrder))
                .collect(Collectors.toList());
    }

    /**
     * Get all trashed menu items
     */
    public List<Menu> getAllTrashedMenus() {
        return menuRepository.findAll().stream()
                .filter(menu -> menu.getStatus() == 0)
                .sorted(Comparator.comparing(Menu::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get menu by ID
     */
    public Optional<Menu> getMenuById(Long id) {
        return menuRepository.findById(id);
    }

    /**
     * Create a new menu item
     */
    public Menu createMenu(Menu menu) {
        menu.setCreatedAt(new Date());
        return menuRepository.save(menu);
    }

    /**
     * Update an existing menu item
     */
    public Menu updateMenu(Long id, Menu menuDetails) {
        Optional<Menu> optionalMenu = menuRepository.findById(id);
        if (optionalMenu.isEmpty()) {
            return null;
        }

        Menu menu = optionalMenu.get();
        menu.setName(menuDetails.getName());
        menu.setLink(menuDetails.getLink());
        menu.setType(menuDetails.getType());
        menu.setTableId(menuDetails.getTableId());
        menu.setParentId(menuDetails.getParentId());
        menu.setSortOrder(menuDetails.getSortOrder());
        menu.setPosition(menuDetails.getPosition());
        menu.setStatus(menuDetails.getStatus());
        menu.setUpdatedAt(new Date());

        return menuRepository.save(menu);
    }

    /**
     * Delete menu
     */
    public boolean deleteMenu(Long id) {
        if (!menuRepository.existsById(id)) {
            return false;
        }

        menuRepository.deleteById(id);
        return true;
    }

    /**
     * Soft delete menu (set status to 0)
     */
    public Menu softDeleteMenu(Long id) {
        Optional<Menu> optionalMenu = menuRepository.findById(id);
        if (optionalMenu.isEmpty()) {
            return null;
        }

        Menu menu = optionalMenu.get();
        menu.setStatus(0);
        menu.setUpdatedAt(new Date());

        return menuRepository.save(menu);
    }

    /**
     * Restore soft-deleted menu
     */
    public Menu restoreMenu(Long id) {
        Optional<Menu> optionalMenu = menuRepository.findById(id);
        if (optionalMenu.isEmpty()) {
            return null;
        }

        Menu menu = optionalMenu.get();
        menu.setStatus(1);
        menu.setUpdatedAt(new Date());

        return menuRepository.save(menu);
    }

    /**
     * Toggle menu status
     */
    public Menu toggleMenuStatus(Long id) {
        Optional<Menu> optionalMenu = menuRepository.findById(id);
        if (optionalMenu.isEmpty()) {
            return null;
        }

        Menu menu = optionalMenu.get();
        menu.setStatus(menu.getStatus() == 1 ? 2 : 1);
        menu.setUpdatedAt(new Date());

        return menuRepository.save(menu);
    }

    /**
     * Get all parent menu items (parentId = 0)
     */
    public List<Menu> getAllParentMenus() {
        return getAllActiveMenus().stream()
                .filter(menu -> menu.getParentId() == 0)
                .collect(Collectors.toList());
    }

    /**
     * Get child menu items by parentId
     */
    public List<Menu> getChildMenusByParentId(Long parentId) {
        return getAllActiveMenus().stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .collect(Collectors.toList());
    }

    /**
     * Get menu items by position
     */
    public List<Menu> getMenusByPosition(String position) {
        return getAllActiveMenus().stream()
                .filter(menu -> menu.getPosition().equals(position))
                .collect(Collectors.toList());
    }
}