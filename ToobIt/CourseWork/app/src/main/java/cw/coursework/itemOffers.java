package cw.coursework;

public class itemOffers {

    private String comment;
    private double offerAmmount;
    private String username;
    private int Accepted;

    public itemOffers(int offersID, int itemsID,String comment, double offerAmmount, String username, int Accepted)
    {
        this.offersID = offersID;
        this.itemsID = itemsID;
        this.comment = comment;
        this.offerAmmount= offerAmmount;
        this.username = username;
        this.Accepted = Accepted;
    }

    public int getOffersID() {
        return offersID;
    }

    public void setOffersID(int offersID) {
        this.offersID = offersID;
    }

    private int offersID;

    public int getItemsID() {
        return itemsID;
    }

    private int itemsID;

    public String getComment() {
        return comment;
    }

    public double getOfferAmmount() {
        return offerAmmount;
    }

    public String getUsername() {
        return username;
    }

    public int getAccepted() {
        return Accepted;
    }



}
