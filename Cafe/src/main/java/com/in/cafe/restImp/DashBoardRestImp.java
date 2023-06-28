package com.in.cafe.restImp;

import com.in.cafe.rest.DashboardRest;
import com.in.cafe.service.DashBoardService;
import com.in.cafe.serviceImp.DashboardServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DashBoardRestImp implements DashboardRest {

    @Autowired
    DashBoardService dashBoardService;

    @Override
    public ResponseEntity<Map<String, Object>> getCount() {
       return dashBoardService.getCount();
    }
}
