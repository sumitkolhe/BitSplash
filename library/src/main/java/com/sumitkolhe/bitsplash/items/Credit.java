package com.sumitkolhe.bitsplash.items;



public class Credit {

    private final String mName;
    private final String mContribution;
    private final String mImage;
    private final String mLink;

    public Credit(String name, String contribution, String image, String link) {
        mName = name;
        mContribution = contribution;
        mImage = image;
        mLink = link;
    }

    public String getName() {
        return mName;
    }

    public String getContribution() {
        return mContribution;
    }

    public String getImage() {
        return mImage;
    }

    public String getLink() {
        return mLink;
    }
}
