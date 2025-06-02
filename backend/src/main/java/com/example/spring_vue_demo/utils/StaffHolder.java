package com.example.spring_vue_demo.utils;

import com.example.spring_vue_demo.entity.Staff;

public class StaffHolder {
    private static final ThreadLocal<Staff> CURRENT_Staff = new ThreadLocal<>();

    public static void set(Staff Staff) {
        CURRENT_Staff.set(Staff);
    }

    public static Staff get() {
        return CURRENT_Staff.get();
    }

    public static void clear() {
        CURRENT_Staff.remove();
    }
}
