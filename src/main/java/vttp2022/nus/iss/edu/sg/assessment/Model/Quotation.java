package vttp2022.nus.iss.edu.sg.assessment.Model; 

import java.util.HashMap;
import java.util.Map;

public class Quotation {

    private String quoteId;
    private Map<String, Float> quotations = new HashMap<>();

    public String getQuoteId() {return quoteId;}
    public void setQuoteId(String quoteId) {this.quoteId = quoteId;}
    
    public Float getQuotation(String item) {return this.quotations.getOrDefault((Object)item, -1000000f);}
    
    public void setQuotations(Map<String, Float> quotations) {this.quotations = quotations;}
    public Map<String, Float> getQuotations() {return quotations;}
    
    public void addQuotation(String item, Float unitPrice) {this.quotations.put(item, unitPrice);}

}
