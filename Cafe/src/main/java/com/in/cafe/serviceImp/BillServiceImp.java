package com.in.cafe.serviceImp;

import com.in.cafe.constants.CafeConstants;
import com.in.cafe.dao.BillDao;
import com.in.cafe.jwt.JwtFilter;
import com.in.cafe.model.Bill;
import com.in.cafe.service.BillService;
import com.in.cafe.utils.CafeUtils;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.IOUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
public class BillServiceImp implements BillService {

    @Autowired
    BillDao billDao;

    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
        try{
            String fileName;
            if(validateRequestMap(requestMap)){
                if(requestMap.containsKey("isGenerate") && !(Boolean) requestMap.get("isGenerate")){
                    fileName = (String) requestMap.get("uuid");
                } else {
                    fileName = CafeUtils.getUUID();
                    requestMap.put("uuid", fileName);
                    insertBill(requestMap);
                }

                String data = "Name :" + requestMap.get("name") + "\n" + "Contact Number :" + requestMap.get("contactNumber")
                        + "\n" + "Email :" + requestMap.get("email") + "\n" + "Payment Method :" + requestMap.get("paymentMethod");

                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(CafeConstants.STORE_LOCATION + "/" + fileName + ".pdf"));

                document.open();
                setRectangleInPdf(document);

                Paragraph p = new Paragraph("Cafe Management System", getFont("Header"));
                p.setAlignment(Element.ALIGN_CENTER);
                document.add(p);

                Paragraph para = new Paragraph(data + "\n \n", getFont("Data"));
                document.add(para);

                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                addTableHeader(table);

                JSONArray jsonArray = CafeUtils.getJSONArray((String) requestMap.get("productDetails"));
                for(int i =0; i< jsonArray.length(); i++){
                    addRow(table, CafeUtils.getMapFromJSON(jsonArray.getString(i)));
                }
                document.add(table);

                Paragraph foot = new Paragraph("Total : "+ requestMap.get("total") + "\n" +
                        "Thank you for visiting. Please visit again!", getFont("Data"));
                document.add(foot);
                document.close();

                return new ResponseEntity<>("{\"uuid\":\"" + fileName + "\"}", HttpStatus.OK);

            }else{
                CafeUtils.getResponseEntity("Required Data Not Found", HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private void addRow(PdfPTable table, Map<String, Object> data) {
        table.addCell((String) data.get("name"));
        table.addCell((String) data.get("category"));
        table.addCell((String) data.get("quantity"));
        table.addCell(Double.toString((Double) data.get("price")));
        table.addCell(Double.toString((Double) data.get("total")));
    }

    private void addTableHeader(PdfPTable table) {
        Stream.of("Name", "Category", "Quantity", "Price", "Sub Total")
                .forEach(columntitle->{
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columntitle));
                    header.setBackgroundColor(BaseColor.YELLOW);
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setVerticalAlignment(Element.ALIGN_CENTER);
                    table.addCell(header);
                });
    }

    private Font getFont(String type) {

        switch(type){
            case "Header":
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, BaseColor.BLACK);
                headerFont.setStyle(Font.BOLD);
                return headerFont;
            case "Data" :
                Font dataFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, BaseColor.BLACK);
                dataFont.setStyle(Font.BOLD);
                return dataFont;
            default:
                return new Font();
        }
    }

    private void setRectangleInPdf(Document document) throws DocumentException {
        log.info("Inside setRectangleInPdf");
        Rectangle rect = new Rectangle(577, 825, 18, 15);
        rect.enableBorderSide(1);
        rect.enableBorderSide(2);
        rect.enableBorderSide(4);
        rect.enableBorderSide(8);
        rect.setBorderColor(BaseColor.BLACK);
        rect.setBorderWidth(1);
        document.add(rect);
    }

    private void insertBill(Map<String, Object> requestMap) {
        try{
            Bill bill = new Bill();
            bill.setUuid((String) requestMap.get("uuid"));
            bill.setContactNumber((String) requestMap.get("contactNumber"));
            bill.setName((String) requestMap.get("name"));
            bill.setPaymentMethod((String) requestMap.get("paymentMethod"));
            bill.setProductDetails((String) requestMap.get("productDetails"));
            bill.setTotal(Integer.valueOf((String) requestMap.get("total")));
            bill.setEmail((String) requestMap.get("email"));
            bill.setCreatedBy(jwtFilter.getCurrentUser());
            billDao.save(bill);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validateRequestMap(Map<String, Object> requestMap) {
        return requestMap.containsKey("name") && requestMap.containsKey("contactNumber")
                && requestMap.containsKey("email")
                && requestMap.containsKey("paymentMethod")
                && requestMap.containsKey("productDetails")
                && requestMap.containsKey("total");
    }

    @Override
    public ResponseEntity<List<Bill>> getBills() {
        List<Bill> list = new ArrayList<>();
        if(jwtFilter.isAdmin()){
            list = billDao.getAllBills();
        }else{
            list = billDao.getBillByUserName(jwtFilter.getCurrentUser());

        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<byte[]> getPdf(Map<String, Object> request) {
        try{
            byte[] byteArray = new byte[0];
            if(!request.containsKey("uuid") && validateRequestMap(request)){
               return new ResponseEntity<>(byteArray, HttpStatus.BAD_REQUEST) ;
            }
            String filePath = CafeConstants.STORE_LOCATION + "/" + (String) request.get("uuid") + ".pdf";
            if(CafeUtils.isFileExist(filePath)){
                byteArray = getByteArray(filePath);
                return new ResponseEntity<>(byteArray, HttpStatus.OK);
            } else {
                request.put("isGenerate", false);
                generateReport(request);
                byteArray = getByteArray(filePath);
                return new ResponseEntity<>(byteArray, HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseEntity<String> deleteBill(Integer id) {
       try{
           Optional op = billDao.findById(id);
           if(op.isPresent()){
               billDao.deleteById(id);
               return CafeUtils.getResponseEntity("Bill deleted successfully", HttpStatus.OK);
           }
           return CafeUtils.getResponseEntity("Bill id doesn't exist", HttpStatus.OK);

       } catch (Exception e) {
           e.printStackTrace();
       }
       return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private byte[] getByteArray(String filePath) throws Exception {
        File initialFile = new File(filePath);
        InputStream target = new FileInputStream(initialFile);
        byte[] byteArray = IOUtils.toByteArray(target);
        target.close();
        return byteArray;
    }
}
