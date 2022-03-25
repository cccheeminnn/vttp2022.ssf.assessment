package vttp2022.nus.iss.edu.sg.assessment.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp2022.nus.iss.edu.sg.assessment.Model.Quotation;

@Service
public class QuotationService {
    
    public Optional<Quotation> getQuotations(List<String> items) 
    {
        //building a JsonArray to be used as body for our RequestEntity
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        items.stream()
            .forEach(s -> {
                arrayBuilder.add(s.replace("\"", ""));
            });
        JsonArray itemsArray = arrayBuilder.build();
        
        RestTemplate template = new RestTemplate();
        RequestEntity<String> req = RequestEntity
            .post("https://quotation.chuklee.com/quotation")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(itemsArray.toString(), String.class);

        ResponseEntity<String> resp = template.exchange(req, String.class);
        InputStream is = new ByteArrayInputStream(resp.getBody().getBytes());
        JsonReader reader = Json.createReader(is);
        JsonObject responseObj = reader.readObject();
        //created a JsonArray to house the item and unitPrice
        JsonArray responseArr = responseObj.getJsonArray("quotations");
        
        
        Quotation quote = new Quotation();
        Map<String, Float> map = new HashMap<>();
        //setQuoteId from the returned JsonObject
        quote.setQuoteId(responseObj.getString("quoteId"));
        for (int i = 0; i < responseArr.size(); i++)
        {
            //for each fruits in the returned JsonObject it is mapped to a HashMap
            map.put(responseArr.getJsonObject(i).getString("item")
                , Float.parseFloat(responseArr.getJsonObject(i).get("unitPrice").toString()));
            //simply parse in the HashMap back to to the instantiated Quotation object
            quote.setQuotations(map);
        }

        return Optional.of(quote);    
    }

    public Float calculateTotalCost(Optional<Quotation> quotationOptional, JsonArray purchaseOrder, List<String> items) 
    {
        //quotationOptional consist of the Quotation class object with quoteId and Map
        //purchaseOrder consist of the details fruits and their purchased quantity
        //items is simply a list of fruits' name

        Float totalCost = 0f;
        Map<String, Integer> purchaseOrderMap = new HashMap<>();
        
        //for each of the fruits purchased, i map each of their quantity to their respective names
        for (int i = 0; i < purchaseOrder.size(); i++) 
        {
            purchaseOrderMap.put(purchaseOrder.getJsonObject(i).getJsonString("item").toString(), 
                Integer.parseInt(purchaseOrder.getJsonObject(i).get("quantity").toString()));
        }
        
        for (String s: items) 
        {
            //Returns me the quantuty purchased for each fruits
            Integer itemQty = purchaseOrderMap.get(s);
            
            //as the HashMap key is without inverted commas, it is removed
            //eg "durian" becomes durian and used as the key
            s = s.replace("\"", "");
            Float itemPrice = quotationOptional.get().getQuotations().get(s);

            totalCost = totalCost + itemQty * itemPrice;
        }
        
        return totalCost;
    }
}
