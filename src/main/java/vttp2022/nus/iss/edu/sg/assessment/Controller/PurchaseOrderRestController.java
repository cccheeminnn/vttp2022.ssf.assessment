package vttp2022.nus.iss.edu.sg.assessment.Controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp2022.nus.iss.edu.sg.assessment.Model.Quotation;
import vttp2022.nus.iss.edu.sg.assessment.Service.QuotationService;

@RestController
@RequestMapping(path="")
public class PurchaseOrderRestController {
    
    @Autowired
    private QuotationService quotationSvc;

    @PostMapping(path="/api/po")
    public ResponseEntity<String> postPurchaseOrder(@RequestBody String payload) 
    {   
        try 
        {
            InputStream is = new ByteArrayInputStream(payload.getBytes());
            JsonReader reader = Json.createReader(is);
            JsonObject jObj = reader.readObject();
            JsonArray jArray = jObj.getJsonArray("lineItems");
    
            List<String> items = new ArrayList<>();
            
            //for loop to add all fruits' name into items, an ArrayList<String>();
            for (int i = 0; i < jArray.size(); i++) 
            {
                items.add(jArray.getJsonObject(i).getJsonString("item").toString());
            }
            
            //instantiate a Quotation object and make it optional
            Quotation quotation = new Quotation();
            Optional<Quotation> quotationOptional = Optional.of(quotation);

            //the instantiated Quotation object goes to get quotations from the API
            quotationOptional = quotationSvc.getQuotations(items);

            //parse in the Quotation object, jArray with the purchase order details, items which is
            //our list of fruits' name
            Float totalCost = quotationSvc.calculateTotalCost(quotationOptional, jArray, items);
            
            //build a JsonObject to send back as ResponseEntity
            JsonObject totalCostJObj = Json.createObjectBuilder()
                .add("invoiceId", quotationOptional.get().getQuoteId())
                .add("name", jObj.getString("name"))
                .add("total", totalCost)
                .build();
    
            return ResponseEntity.ok(totalCostJObj.toString());
        //Whenever an error occurs an empty JsonObject is returned with a 404 status code
        } catch (Exception ex) {
            JsonObject emptyJObj = Json.createObjectBuilder()
                .build();
            return ResponseEntity.status(404).body(emptyJObj.toString());
        }
    }
}
