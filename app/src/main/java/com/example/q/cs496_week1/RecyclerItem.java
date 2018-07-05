package com.example.q.cs496_week1;

public class RecyclerItem {
    private String name;
    private int image;
    private boolean starred;
    private boolean selected;



    public RecyclerItem (String name, int image, boolean starred, boolean selected) {
        this.name = name;
        if (image == -1)
            this.image = R.drawable.ic_tag_faces_black_24dp;
        else
            this.image = image;
        this.starred = starred;
        this.selected = selected;

    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public boolean getSelected() {return selected;}

    public void setSelected(boolean selected) {this.selected = selected;}

}
