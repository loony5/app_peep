package com.example.carpe.peep;

public class WritingData {

    private String writing;
    private String upImageUri;
    private String writingKeyword;

    public String getWritingKeyword() {
        return writingKeyword;
    }

    public void setWritingKeyword(String writingKeyword) {
        this.writingKeyword = writingKeyword;
    }

    public String getWriting() {
        return writing;
    }

    public void setWriting(String writing) {
        this.writing = writing;
    }

    public String getUpImageUri() {
        return upImageUri;
    }

    public void setUpImageUri(String upImageUri) {
        this.upImageUri = upImageUri;
    }

    public WritingData(String writing, String upImageUri, String writingKeyword) {
        this.writing = writing;
        this.upImageUri = upImageUri;
        this.writingKeyword = writingKeyword;
    }
}
