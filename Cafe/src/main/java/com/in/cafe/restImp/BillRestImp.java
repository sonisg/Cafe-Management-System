package com.in.cafe.restImp;

import com.in.cafe.constants.CafeConstants;
import com.in.cafe.model.Bill;
import com.in.cafe.rest.BillRest;
import com.in.cafe.service.BillService;
import com.in.cafe.utils.CafeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class BillRestImp implements BillRest {

    @Autowired
    BillService billService;

    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
        try{
            return billService.generateReport(requestMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Bill>> getBills() {
        try{
            return billService.getBills();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public ResponseEntity<byte[]> getPdf(Map<String, Object> request) {
        try{
            return billService.getPdf(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseEntity<String> deleteBill(Integer id) {
        try{
            return billService.deleteBill(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
