package com.example.kristenvondrak.dartmouth.Parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Dictionary;

/**
 * Created by kristenvondrak on 1/10/16.
 */
@ParseClassName("Offering")
public class Recipe extends ParseObject {

    // Nutrients is a dictionary whose key type is a dictionary.
    // Look at Parse data browser to see how the JSON is formatted.
    // For the inner Dictionary, the value is of type NSObject because
    // it can be a string or an int, depending on the key.

    public int getDartmouthId() {
        return getInt("dartmouthId");
    }

    public String getName() {
        return getString("name");
    }

    public int getRank() {
        return getInt("rank");
    }

    public Dictionary<String, Dictionary<String, Object>> getNutrients() {
        return (Dictionary<String, Dictionary<String, Object>>) get("nutrients");
    }

    public String getUUID() {
        return getString("uuid");
    }

    public String getCategory() {
        return getString("category");
    }

    public void setDartmouthId(int value) {
        put("dartmouthId", value);
    }

    public void setName(String    value){
        put("name", value);
    }

    public void setRank(int value) {
        put("rank", value);
    }

    public void setNutrients(Dictionary<String, Dictionary<String, Object>> value) {
        put("nutrients", value);
    }

    public void setCategory(String value) {
        put("category", value);
    }

    public String getCalories() {
        return (String) getNutrients().get("result").get("calories");
    }

    public String getSugars() {
        return (String) getNutrients().get("result").get("sugars");
    }

    public String getFiber() {
        return (String) getNutrients().get("result").get("fiberdtry");
    }

    public String getTotalCarbs() {
        return (String) getNutrients().get("result").get("carbs");
    }

    public String getSodium() {
        return (String) getNutrients().get("result").get("sodium");
    }

    public String getCholestrol() {
        return (String) getNutrients().get("result").get("cholestrol");
    }

    public String getSaturatedFat() {
        return (String) getNutrients().get("result").get("sfa");
    }

    public String getTotalFat() {
        return (String) getNutrients().get("result").get("fat");
    }

    public String getFatCalories() {
        return (String) getNutrients().get("result").get("calfat");
    }

    public String getCalcium() {
        return (String) getNutrients().get("result").get("calcium");
    }

    public String getTransFat() {
        return (String) getNutrients().get("result").get("fatrans");
    }

    public String getProtein() {
        return (String) getNutrients().get("result").get("protein");
    }

    public String getFolacin() {
        return (String) getNutrients().get("result").get("folacin");
    }

    public String getIron() {
        return (String) getNutrients().get("result").get("iron");
    }

    public String getMufa() {
        return (String) getNutrients().get("result").get("mufa");
    }

    public String getNiacin() {
        return (String) getNutrients().get("result").get("niacin");
    }

    public String getPhosphorus() {
        return (String) getNutrients().get("result").get("phosphorus");
    }

    public String getPotassium() {
        return (String) getNutrients().get("result").get("potassium");
    }

    public String getPufa() {
        return (String) getNutrients().get("result").get("pufa");
    }

    public String getRiboflavin() {
        return (String) getNutrients().get("result").get("riboflavin");
    }

    public String getServingSize() {
        String g = (String) getNutrients().get("result").get("serving_size_grams");
        if (g == null)
            return (String) getNutrients().get("result").get("serving_size_mls");
        return g;
    }

    public String getServingsPerContainer() {
        return (String) getNutrients().get("result").get("servings_per_container");
    }

    public String getSfa() {
        return (String) getNutrients().get("result").get("sfa");
    }

    public String getThiamin() {
        return (String) getNutrients().get("result").get("thiamin");
    }

    public String getZinc() {
        return (String) getNutrients().get("result").get("zinc");
    }

    public String getVitb6() {
        return (String) getNutrients().get("result").get("vitb6");
    }

    public String getVitb12() {
        return (String) getNutrients().get("result").get("vitb12");
    }

    public String getVitc() {
        return (String) getNutrients().get("result").get("vitc");
    }

    public String getVitaiu() {
        return (String) getNutrients().get("result").get("vita_iu");
    }


}
