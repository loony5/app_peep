package com.example.carpe.peep;

public class KeywordData {

    private String keyword;
    boolean selected;

    public boolean isSelected() {
        return  selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword){
        this.keyword = keyword;
    }

    public KeywordData(String keyword){
        this.keyword = keyword;
    }

}
