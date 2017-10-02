package com.example.khidalgo_countbook;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Represents a counter object
 */
public class Counter{
        private String name;
        private String comment;
        private Date date;
        private Integer value; //should be non-negative
        private Integer init_val;

    /**
     * Constructs a new counter object with a comment
     * @param newname counter name
     * @param start_val counter starting value
     * @param newcomment counter comment
     */
    Counter(String newname, Integer start_val, String newcomment) {
        name = newname;
        comment = newcomment;
        date = new Date();
        init_val = start_val;
        value = start_val;
    }

    /**
     * Constructs a new counter object without a comment
     * @param newname counter name
     * @param start_val counter starting value
     */
    Counter(String newname, Integer start_val) {
        name = newname;
        comment = "";
        date = new Date();
        init_val = start_val;
        value = start_val;
    }

    /**
     * Increments counter value by 1
     * Updates date
     */
    public void countIncrement(){
        value += 1;
        date = new Date();
    }

    /**
     * Decrements counter value by 1
     * Does nothing if value is already at 0
     * Updates date if decrement is successful
     */
    public void countDecrement(){
        if (value == 0){
            return; //if at 0, don't got lower, no negatives allowed!
        }else{
            value -=1;
            date = new Date();
        }
    }

    /**
     * Sets counter value to the initial value
     * Updates date
     */
    public void countReset(){
        value = init_val;
        date = new Date();
    }

    public void setName(String newname){
        name = newname;
    }

    public void setComment(String newcomment){
        comment = newcomment;
    }

    public void setInitVal(Integer newstart){
        init_val = newstart;
    }

    public void setVal(Integer newval){
        value = newval;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    public Date getDate() {
        return date;
    }

    public Integer getValue() {
        return value;
    }

    public Integer getInitVal() {
        return init_val;
    }

    @Override
    public String toString() {
        return name + "\n" + value.toString() + "|" + date.toString();
    }
}
