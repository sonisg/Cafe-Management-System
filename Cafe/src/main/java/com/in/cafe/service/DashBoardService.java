package com.in.cafe.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface DashBoardService {
    ResponseEntity<Map<String, Object>> getCount();
}
